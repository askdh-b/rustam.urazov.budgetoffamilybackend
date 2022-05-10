package rustam.urazov.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import rustam.urazov.routes.familyRouting
import rustam.urazov.routes.goalRouting
import rustam.urazov.routes.invitationRouting
import rustam.urazov.routes.userRouting

fun Application.configureRouting() {
    routing {
        userRouting()
        familyRouting()
        invitationRouting()
        goalRouting()
    }
}
