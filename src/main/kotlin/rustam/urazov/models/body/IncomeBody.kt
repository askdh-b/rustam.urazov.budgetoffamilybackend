package rustam.urazov.models.body

import kotlinx.serialization.Serializable

@Serializable
data class IncomeBody(val name: String, val sum: Float)