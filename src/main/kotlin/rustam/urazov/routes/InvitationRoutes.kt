package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.familyService
import rustam.urazov.invitationService
import rustam.urazov.models.body.InvitationBody
import rustam.urazov.storage.Family
import rustam.urazov.storage.Invitation
import rustam.urazov.storage.User
import rustam.urazov.userService

fun Route.invitationRouting() {
    authenticate("auth-jwt") {
        get("/invitation") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            userService.getAllUser().find { it.username == username }?.let { user ->
                val invitation = invitationService.getAllInvitations().filter { it.recipientId == user.id }

                call.respond(invitation)
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        post("/invitation") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val invitation = call.receive<InvitationBody>()

            val senderId = userService.getAllUser().find { it.username == username }?.id
            var familyId = userService.getAllUser().find { it.username == username }?.familyId

            if (senderId != null && familyId != null) {
                if (familyId == 1) {
                    familyService.addFamily(Family(0))
                    userService.getAllUser().find { it.username == username }?.let { user ->
                        userService.editUser(
                            User(
                                id = user.id,
                                familyId = familyService.getAllFamilies().last().id,
                                firstName = user.firstName,
                                lastName = user.lastName,
                                username = user.username,
                                password = user.password
                            )
                        )
                        familyId = user.familyId
                    } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
                }
                if (senderId != invitation.recipientId) {
                    userService.getAllUser().find { it.id == invitation.recipientId }?.let { user ->
                        if (user.familyId == 1) {
                            invitationService.addInvitation(
                                mapToInvitation(
                                    invitation,
                                    senderId,
                                    familyId ?: 1
                                )
                            )
                            call.respond(status = HttpStatusCode.Created, message = "Invitation sent correctly")
                        } else call.respond(status = HttpStatusCode.BadRequest, message = "The user is already in the family")
                    } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")

                } else call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = "You cannot send an invitation to yourself"
                )
            } else call.respond(status = HttpStatusCode.NotFound, message = "User or family is null")
        }

        post("/invitation/{id?}") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val id = call.parameters["id"] ?: return@post call.respond(
                status = HttpStatusCode.BadRequest, message = "Invitation id is null"
            )

            invitationService.getAllInvitations().find { it.id.toString() == id }?.let { invitation ->
                userService.getAllUser().find { it.username == username }?.let { user ->
                    if (user.id == invitation.recipientId) {
                        if (user.familyId == 1) {
                            userService.editUser(
                                User(
                                    id = user.id,
                                    familyId = userService.getAllUser().find { it.id == invitation.senderId }?.familyId,
                                    firstName = user.firstName,
                                    lastName = user.lastName,
                                    username = user.username,
                                    password = user.password
                                )
                            )
                            invitationService.deleteInvitation(invitation.id)
                            call.respond(status = HttpStatusCode.OK, message = "Invitation accepted")
                        } else {
                            call.respond(
                                status = HttpStatusCode.BadRequest,
                                message = "You are already in the family"
                            )
                        }
                    } else {
                        call.respond(status = HttpStatusCode.NotFound, message = "Invitation not found")
                    }
                } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "Invitation not found")
        }

        delete("/invitation/{id?}") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val id = call.parameters["id"] ?: return@delete call.respond(
                status = HttpStatusCode.BadRequest, message = "Invitation id is null"
            )

            userService.getAllUser().find { it.username == username }?.let { user ->
                val senderId = invitationService.getAllInvitations().find { it.id.toString() == id }?.senderId
                val recipientId = invitationService.getAllInvitations().find { it.id.toString() == id }?.recipientId

                if (user.id == recipientId) {
                    invitationService.deleteInvitation(id.toInt())
                    userService.getAllUser().find { it.id == senderId }?.let { user2 ->
                        val familyId = user2.familyId
                        if (userService.getAllUser().filter { it.familyId == familyId }.size == 1) {
                            userService.editUser(
                                User(
                                    id = user2.id,
                                    familyId = 1,
                                    firstName = user2.firstName,
                                    lastName = user2.lastName,
                                    username = user2.username,
                                    password = user2.password
                                )
                            )
                            if (familyId != null) familyService.deleteFamily(familyId)
                        }
                    } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
                    call.respond(status = HttpStatusCode.OK, message = "Invitation removed correctly")
                } else call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = "You cannot remove someone else's invitations"
                )
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }
    }
}

fun mapToInvitation(invitation: InvitationBody, senderId: Int, familyId: Int) = Invitation(
    id = 0,
    senderId = senderId,
    recipientId = invitation.recipientId,
    familyId = familyId
)