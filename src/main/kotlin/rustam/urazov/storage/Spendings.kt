package rustam.urazov.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Spendings : IntIdTable() {
    val userId = integer("user_id")
    val sum = float("spending_sum")
    val name = varchar("spending_name", 255)
    val creationDate = varchar("spending_creation_date", 255)
}

class SpendingEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SpendingEntity>(Spendings)

    var userId by Spendings.userId
    var sum by Spendings.sum
    var name by Spendings.name
    var creationDate by Spendings.creationDate

    fun toSpending() = Spending(id.value, userId, sum, name, creationDate)
}

data class Spending(
    val id: Int,
    val userId: Int,
    val sum: Float,
    val name: String,
    val creationDate: String
)