package rustam.urazov.models

import kotlinx.serialization.Serializable

@Serializable
data class Goal(
    val id: Int,
    val userId: Int,
    var name: String,
    var incomePercentile: Double,
    var progress: Double,
    var sum: Double,
    val creationDate: String
)

val goalStorage = mutableListOf<Goal>()