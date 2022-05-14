package rustam.urazov.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Incomes : IntIdTable() {
    val userId = integer("user_id")
    val sum = float("income_sum")
    val name = varchar("income_name", 255)
    val creationDate = varchar("income_creation_date", 255)
}

class IncomeEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<IncomeEntity>(Incomes)

    var userId by Incomes.userId
    var sum by Incomes.sum
    var name by Incomes.name
    var creationDate by Incomes.creationDate

    fun toIncome() = Income(id.value, userId, sum, name, creationDate)
}

data class Income(
    val id: Int,
    val userId: Int,
    val sum: Float,
    val name: String,
    val creationDate: String
)