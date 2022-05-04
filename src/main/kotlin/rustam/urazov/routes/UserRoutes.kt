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
            userStorage.add(User(
                id = user.id,
                firstName = user.firstName,
                lastName = user.lastName,
                userName = user.userName,
                password = md5(user.password).toHex()
            ))
            call.respondText("Registration is successfully", status = HttpStatusCode.Created)
        }
    }
}