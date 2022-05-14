package rustam.urazov.models.body

import kotlinx.serialization.Serializable

@Serializable
data class IncomesSourceBody(
    val name: String,
    val sum: Float,
    val monthDay: Int
)