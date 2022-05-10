package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.models.Invitation
import rustam.urazov.models.body.InvitationBody
import rustam.urazov.models.invitationStorage
import rustam.urazov.models.userStorage

fun Route.invitationRouting() {
    authenticate("auth-jwt") {
        get("/invitation") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            userStorage.find { it.username == username }?.let { user ->
                val invitation = invitationStorage.filter { it.recipientId == user.id }

                call.respond(invitation)
            }
        }

        post("/invitation") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val invitation = call.receive<InvitationBody>()

            val senderId = userStorage.find { it.username == username }?.id
            val familyId = userStorage.find { it.username == username }?.familyId

            if (senderId != null && familyId != null) {
                invitationStorage.add(
                    mapToInvitation(
                        invitation,
                        senderId,
                        familyId
                    )
                )
                call.respond(status = HttpStatusCode.Created, message = "Invitation sent correctly")
            } else call.respond(status = HttpStatusCode.NotFound, message = "User or family is null")
        }

        post("/invitation/{id?}") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val id = call.parameters["id"] ?: return@post call.respond(
                status = HttpStatusCode.BadRequest, message = "Invitation id is null"
            )

            invitationStorage.find { it.id.toString() == id }?.let { invitation ->
                userStorage.find { it.username == username }?.let { user ->
                    if (user.id == invitation.recipientId) {
                        if (user.familyId == null) {
                            user.familyId = userStorage.find { it.id == invitation.senderId }?.familyId
                            invitationStorage.removeIf { it.id == invitation.id }
                            call.respond(status = HttpStatusCode.OK, message = "Invitation accepted")
                        } else {
                            call.respond(
                                status = HttpStatusCode.BadRequest,
                                message = "This user is already in the family"
                            )
                        }
                    } else {
                        call.respond(status = HttpStatusCode.NotFound, message = "Invitation not found")
                    }
                }
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "Invitation not found")
        }

        delete("/invitation/{id?}") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val id = call.parameters["id"] ?: return@delete call.respond(
                status = HttpStatusCode.BadRequest, message = "Invitation id is null"
            )

            userStorage.find { it.username == username }?.let { user ->
                val recipientId = invitationStorage.find { it.id.toString() == id }?.recipientId

                if (user.id == recipientId) {
                    invitationStorage.removeIf { it.id.toString() == id }.let {
                        call.respond(status = HttpStatusCode.OK, message = "Invitation removed correctly")
                    }
                } else call.respond(status = HttpStatusCode.BadRequest, message = "You cannot remove else's invitation")
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }
    }
}

fun mapToInvitation(invitation: InvitationBody, senderId: Int, familyId: Int) = Invitation(
    id = generateInvitationId(),
    senderId = senderId,
    recipientId = invitation.recipientId,
    familyId = familyId
)

fun generateInvitationId(): Int = try {
    invitationStorage.last().id + 1
} catch (e: Exception) {
    1
}