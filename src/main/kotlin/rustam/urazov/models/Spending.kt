package rustam.urazov.models

import kotlinx.serialization.Serializable

@Serializable
data class Spending(
    val id: Int,
    val userId: Int,
    val name: String,
    val sum: Double,
    val date: String
)

val spendingStorage = mutableListOf<Spending>()