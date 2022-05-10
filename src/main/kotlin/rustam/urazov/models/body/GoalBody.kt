package rustam.urazov.models.body

import kotlinx.serialization.Serializable

@Serializable
data class GoalBody(
    val name: String,
    val incomePercentile: Double,
    val sum: Double
)