package rustam.urazov.models.body

import kotlinx.serialization.Serializable

@Serializable
data class InvitationBody(val recipientId: Int)