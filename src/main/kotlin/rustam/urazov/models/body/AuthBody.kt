package rustam.urazov.models.body

import kotlinx.serialization.Serializable

@Serializable
data class AuthBody(
    val username: String,
    val password: String
)