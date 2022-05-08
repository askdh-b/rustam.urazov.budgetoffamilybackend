package rustam.urazov.models

import kotlinx.serialization.Serializable

@Serializable
data class Family(
    var id: Int,
)

val familyStorage = mutableListOf<Family>()