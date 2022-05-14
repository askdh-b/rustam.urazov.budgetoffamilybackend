package rustam.urazov.storage.service

import org.jetbrains.exposed.sql.transactions.transaction
import rustam.urazov.storage.Invitation
import rustam.urazov.storage.InvitationEntity

class InvitationService {

    fun getAllInvitations() = transaction {
        InvitationEntity.all().map(InvitationEntity::toInvitation)
    }

    fun addInvitation(invitation: Invitation) = transaction {
        InvitationEntity.new {
            this.senderId = invitation.senderId
            this.recipientId = invitation.recipientId
            this.familyId = invitation.familyId
        }
    }

    fun deleteInvitation(invitationId: Int) = transaction {
        InvitationEntity[invitationId].delete()
    }
}