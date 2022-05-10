package rustam.urazov.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthBody(
    val username: String,
    val password: String,
)