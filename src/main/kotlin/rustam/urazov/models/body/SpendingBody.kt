package rustam.urazov.models.body

import kotlinx.serialization.Serializable

@Serializable
data class SpendingBody(val name: String, val sum: Float)