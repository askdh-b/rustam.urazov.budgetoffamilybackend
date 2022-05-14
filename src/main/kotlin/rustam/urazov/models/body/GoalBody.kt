package rustam.urazov.models.body

import kotlinx.serialization.Serializable

@Serializable
data class GoalBody(
    val name: String,
    val incomePercentile: Float,
    val sum: Float
)