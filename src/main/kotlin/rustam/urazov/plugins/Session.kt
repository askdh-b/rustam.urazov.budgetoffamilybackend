package rustam.urazov.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import java.io.File

data class AuthSession(val userId: Int, val token: String)

val sessionStorage = mutableListOf<AuthSession>()

fun Application.configureSession() {
    install(Sessions) {
        val secretSignKey = hex("6819b57a326945c1968f45236589")
        header<AuthSession>("auth_session", directorySessionStorage(File("build/.sessions"))) {
            transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
        }
    }
}