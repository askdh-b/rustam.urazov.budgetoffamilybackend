package rustam.urazov.models.body

import kotlinx.serialization.Serializable

@Serializable
data class SpendingsSourceBody(
    val name: String,
    val sum: Float,
    val monthDay: Int
)