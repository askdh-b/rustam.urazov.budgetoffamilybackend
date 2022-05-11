package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.models.Family
import rustam.urazov.models.familyStorage
import rustam.urazov.models.userStorage

fun Route.familyRouting() {
    authenticate("auth-jwt") {
        post("/family") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val user = userStorage.find { it.username == username }

            if (user != null) {
                if (user.familyId == null) {
                    familyStorage.add(mapToFamily())

                    userStorage.find { it == user }?.familyId = familyStorage.last().id
                    call.respond(status = HttpStatusCode.Created, message = "Family created correctly")
                } else call.respond(status = HttpStatusCode.BadRequest, message = "You are already in the family")
            } else call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        delete("/family") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            userStorage.find { it.username == username }?.let { user ->
                userStorage.filter { it.familyId == user.familyId }.let { users ->
                    var usersCount = 0

                    for (u in users) {
                        usersCount += 1
                    }

                    if (usersCount >= 1) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = "It is impossible to remove to a family while there is more than one person in it"
                        )
                    } else {
                        user.familyId = null
                        familyStorage.remove(familyStorage.find { it.id == user.familyId })
                        call.respond(status = HttpStatusCode.OK, message = "Family deleted correctly")
                    }
                }
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }
    }
}

fun mapToFamily(): Family = Family(generateFamilyId())

fun generateFamilyId(): Int = try {
    familyStorage.last().id + 1
} catch (e: Exception) {
    1
}