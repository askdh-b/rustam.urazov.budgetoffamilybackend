package rustam.urazov.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Invitations : IntIdTable() {
    val senderId = integer("sender_id")
    val recipientId = integer("recipient_id")
    val familyId = integer("family_id")
}

class InvitationEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<InvitationEntity>(Invitations)

    var senderId by Invitations.senderId
    var recipientId by Invitations.recipientId
    var familyId by Invitations.familyId

    fun toInvitation() = Invitation(id.value, senderId, recipientId, familyId)
}

data class Invitation(
    val id: Int,
    val senderId: Int,
    val recipientId: Int,
    val familyId: Int
)