package rustam.urazov.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthBody(
    val userName: String,
    val password: String,
)