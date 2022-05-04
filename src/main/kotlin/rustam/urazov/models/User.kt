package rustam.urazov.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val userName: String,
    val password: String,
)

val userStorage = mutableListOf<User>()