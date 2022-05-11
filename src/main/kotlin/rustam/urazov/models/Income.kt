package rustam.urazov.models

import kotlinx.serialization.Serializable

@Serializable
data class Income(
    val id: Int,
    val userId: Int,
    val name: String,
    val sum: Double,
    val date: String
)

val incomeStorage = mutableListOf<Income>()