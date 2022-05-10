package rustam.urazov.models

import kotlinx.serialization.Serializable

@Serializable
data class Invitation(
    val id: Int,
    val senderId: Int,
    val recipientId: Int,
    val familyId: Int
)

val invitationStorage = mutableListOf<Invitation>()