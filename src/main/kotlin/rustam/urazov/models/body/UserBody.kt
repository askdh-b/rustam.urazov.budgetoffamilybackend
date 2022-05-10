package rustam.urazov.models.body

import kotlinx.serialization.Serializable

@Serializable
data class UserBody(
    val firstName: String,
    val lastName: String,
    val username: String,
    val password: String,
)