package rustam.urazov.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.authRouting() {
    authenticate("auth-jwt") {
        get("/user") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())?: let {
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
            call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
        }
    }
}