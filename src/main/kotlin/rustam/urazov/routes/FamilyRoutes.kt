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

            familyStorage.add(mapToFamily())

            userStorage.find { it.username == username }?.familyId = familyStorage.last().id

            call.respond(status = HttpStatusCode.Created, message = "Family created correctly")
        }

        delete("/family") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            userStorage.find { it.username == username }?.let { user ->
                userStorage.filter { it.familyId == user.familyId }.let { users ->
                    for (u in users) {
                        u.familyId = null
                    }

                    familyStorage.remove(familyStorage.find { it.id == user.familyId })

                    call.respond(status = HttpStatusCode.OK, message = "Family deleted correctly")
                }
            }
        }
    }
}

fun mapToFamily(): Family = Family(generateFamilyId())

fun generateFamilyId(): Int = try {
    familyStorage.last().id + 1
} catch (e: Exception) {
    1
}