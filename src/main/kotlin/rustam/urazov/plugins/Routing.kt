package rustam.urazov.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import rustam.urazov.routes.authRouting
import rustam.urazov.routes.userRouting

fun Application.configureRouting() {
    routing {
        userRouting()
        authRouting()
    }
}
