package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.generateToken
import rustam.urazov.md5
import rustam.urazov.models.AuthBody
import rustam.urazov.models.AuthResponse
import rustam.urazov.models.userStorage
import rustam.urazov.toHex

fun Route.authRouting() {
    route("/auth") {
        post {
            val authBody = call.receive<AuthBody>()
            for (user in userStorage) {
                if (user.userName == authBody.userName && user.password == md5(authBody.password).toHex()) {
                    val authResponse = AuthResponse(generateToken())
                    call.respond(authResponse)
                } else {
                    call.respondText("Invalid username or password", status = HttpStatusCode.NotAcceptable)
                }
            }
        }
    }
}