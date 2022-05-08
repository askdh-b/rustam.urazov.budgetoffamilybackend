package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.models.Invitation
import rustam.urazov.models.invitationStorage
import rustam.urazov.models.userStorage

fun Route.invitationRouting() {
    route("/invitation") {
        get("{id?}") {
            val id = call.parameters["recipientId"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )

            val invitation = invitationStorage.filter { it.recipientId.toString() == id }
            call.respond(invitation)
        }

        post {
            val invitation = call.receive<Invitation>()

            invitationStorage.add(
                invitation.apply {
                    id = generateInvitationId()
                    familyId = userStorage.find { it.id == senderId }?.let { familyId }
                }
            )
            call.respondText(
                "Invitation sent correctly",
                status = HttpStatusCode.Created
            )
        }
    }
}


fun generateInvitationId(): Int = try {
    invitationStorage.last().id + 1
} catch (e: Exception) {
    1
}