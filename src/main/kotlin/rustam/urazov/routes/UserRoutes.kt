package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.familyService
import rustam.urazov.md5
import rustam.urazov.models.body.UserBody
import rustam.urazov.storage.User
import rustam.urazov.toHex
import rustam.urazov.userService

fun Route.userRouting() {

    authenticate("auth-jwt") {
        get("/search") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            userService.getAllUser().find { it.username == username }?.let { user ->
                familyService.getAllFamilies().find { it.id == user.familyId }?.let { family ->
                    userService.getAllUser().filter { it.familyId != family.id }.let { users ->
                        val foundUsers = mutableListOf<User>()

                        for (u in users) {
                            if (u.username.contains(call.request.queryParameters["q"]?: "")) {
                                foundUsers.add(u)
                            }
                        }
                        call.respond(foundUsers)
                    }
                } ?: call.respond(status = HttpStatusCode.NotFound, message = "Family not found")
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        post("/leave") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            userService.getAllUser().find { it.username == username }?.let { user ->
                familyService.getAllFamilies().find { it.id == user.familyId }?.let { family ->
                    if (user.familyId != 1) {
                        userService.editUser(User(
                            id = user.id,
                            familyId = 1,
                            firstName = user.firstName,
                            lastName = user.lastName,
                            username = user.username,
                            password = user.password
                        ))
                        if (userService.getAllUser().none { it.familyId == family.id }) {
                            familyService.deleteFamily(family.id)
                        }
                        call.respond(status = HttpStatusCode.OK, message = "You left your family")
                    } else call.respond(status = HttpStatusCode.BadRequest, message = "You are not in the family")
                }
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }
    }

    post("/register") {
        val user = call.receive<UserBody>()

        if (checkUserNameForUniqueness(user.username)) {
            userService.addUser(mapToUser(user))
            call.respond(status = HttpStatusCode.Created, message = "Registration is successfully")
        } else call.respond(status = HttpStatusCode.Conflict, message = "This username is taken")
    }
}


fun mapToUser(user: UserBody): User = User(
    id = 0,
    familyId = null,
    firstName = user.firstName,
    lastName = user.lastName,
    username = user.username,
    password = md5(user.password).toHex()
)

fun checkUserNameForUniqueness(userName: String): Boolean =
    userService.getAllUser().find { it.username == userName }?.let { false } ?: true