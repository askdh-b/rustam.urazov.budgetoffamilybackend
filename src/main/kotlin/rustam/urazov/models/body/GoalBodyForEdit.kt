package rustam.urazov.models.body

import kotlinx.serialization.Serializable

@Serializable
data class GoalBodyForEdit(
    val name: String,
    val incomePercentile: Double,
    val progress: Double,
    val sum: Double
)