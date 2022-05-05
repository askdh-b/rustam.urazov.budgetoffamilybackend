package rustam.urazov.models

import kotlinx.serialization.Serializable

@Serializable
data class Goal(
    val id: Int,
    val userId: Int,
    val name: String,
    val incomePercentile: Double,
    val progress: Double,
    val sum: Double,
    val creationDate: String,
)

val goalStorage = mutableListOf<Goal>()