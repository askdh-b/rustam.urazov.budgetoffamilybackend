package rustam.urazov.models

import kotlinx.serialization.Serializable

@Serializable
data class SpendingsSource(
    val id: Int,
    val userId: Int,
    var name: String,
    var sum: Double,
    var monthDay: Int
)

val spendingsSourceStorage = mutableListOf<SpendingsSource>()