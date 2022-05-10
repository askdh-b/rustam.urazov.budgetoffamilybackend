package rustam.urazov.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    var familyId: Int?,
    val firstName: String,
    val lastName: String,
    val username: String,
    val password: String
)

val userStorage = mutableListOf<User>()