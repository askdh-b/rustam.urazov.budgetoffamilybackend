package rustam.urazov.models

import kotlinx.serialization.Serializable

@Serializable
data class Family(val id: Int)

val familyStorage = mutableListOf<Family>()