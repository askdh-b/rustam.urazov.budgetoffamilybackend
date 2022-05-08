package rustam.urazov.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    var id: Int,
    var familyId: Int?,
    val firstName: String,
    val lastName: String,
    val userName: String,
    var password: String
)

val userStorage = mutableListOf<User>()