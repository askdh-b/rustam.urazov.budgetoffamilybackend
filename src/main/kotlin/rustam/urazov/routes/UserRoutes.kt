package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.md5
import rustam.urazov.models.User
import rustam.urazov.models.userStorage
import rustam.urazov.toHex

fun Route.userRouting() {
    route("/register") {
        post {
            val user = call.receive<User>()

            if (checkUserNameForUniqueness(user.userName)) {
                userStorage.add(
                    user.apply {
                        id = generateUserId()
                        password = md5(user.password).toHex()
                    }
                )
                call.respondText("Registration is successfully", status = HttpStatusCode.Created)
            } else call.respondText("This username is taken", status = HttpStatusCode.BadRequest)
        }
    }
}

fun generateUserId(): Int = try {
    userStorage.last().id + 1
} catch (e: Exception) {
    1
}

fun checkUserNameForUniqueness(userName: String): Boolean =
    userStorage.find { it.userName == userName }?.let { false } ?: true