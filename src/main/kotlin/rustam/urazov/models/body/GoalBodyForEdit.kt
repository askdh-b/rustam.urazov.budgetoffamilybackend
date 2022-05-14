package rustam.urazov.models.body

import kotlinx.serialization.Serializable

@Serializable
data class GoalBodyForEdit(
    val name: String,
    val incomePercentile: Float,
    val progress: Float,
    val sum: Float
)