package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.md5
import rustam.urazov.models.User
import rustam.urazov.models.body.UserBody
import rustam.urazov.models.familyStorage
import rustam.urazov.models.userStorage
import rustam.urazov.toHex

fun Route.userRouting() {
    authenticate("auth-jwt") {
        get("/search") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            userStorage.find { it.username == username }?.let { user ->
                familyStorage.find { it.id == user.familyId }?.let { family ->
                    userStorage.filter { it.familyId != family.id }.let { users ->
                        val foundUsers = mutableListOf<User>()

                        for (u in users) {
                            if (u.username.contains(call.request.queryParameters["q"]?: "")) {
                                foundUsers.add(u)
                            }
                        }
                        call.respond(foundUsers)
                    }
                } ?: call.respond(status = HttpStatusCode.NotFound, message = "Family not found")
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User npt found")
        }
    }

    post("/register") {
        val user = call.receive<UserBody>()

        if (checkUserNameForUniqueness(user.username)) {
            userStorage.add(mapToUser(user))
            call.respond(status = HttpStatusCode.Created, message = "Registration is successfully")
        } else call.respond(status = HttpStatusCode.Conflict, message = "This username is taken")
    }
}

fun generateUserId(): Int = try {
    userStorage.last().id + 1
} catch (e: Exception) {
    1
}

fun mapToUser(user: UserBody): User = User(
    id = generateUserId(),
    familyId = null,
    firstName = user.firstName,
    lastName = user.lastName,
    username = user.username,
    password = md5(user.password).toHex()
)

fun checkUserNameForUniqueness(userName: String): Boolean =
    userStorage.find { it.username == userName }?.let { false } ?: true