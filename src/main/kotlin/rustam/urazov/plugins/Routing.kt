package rustam.urazov.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import rustam.urazov.routes.*

fun Application.configureRouting() {
    routing {
        userRouting()
        invitationRouting()
        goalRouting()
        incomeRouting()
        spendingRouting()
        incomesSourceRouting()
        spendingsSourceRouting()
    }
}
