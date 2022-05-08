package rustam.urazov.models

import kotlinx.serialization.Serializable

@Serializable
data class Invitation(
    var id: Int,
    val senderId: Int,
    val recipientId: Int,
    var familyId: Int?,
)

val invitationStorage = mutableListOf<Invitation>()