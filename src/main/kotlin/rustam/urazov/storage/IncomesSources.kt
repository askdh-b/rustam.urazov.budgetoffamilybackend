package rustam.urazov.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object IncomesSources : IntIdTable() {
    val userId = integer("user_id")
    val name = varchar("incomes_source_name", 255)
    val sum = float("incomes_source_sum")
    val monthDay = integer("incomes_source_month_day")
}

class IncomesSourceEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<IncomesSourceEntity>(IncomesSources)

    var userId by IncomesSources.userId
    var name by IncomesSources.name
    var sum by IncomesSources.sum
    var monthDay by IncomesSources.monthDay

    fun toIncomesSource() = IncomesSource(id.value, userId, name, sum, monthDay)
}

data class IncomesSource(
    val id: Int,
    val userId: Int,
    val name: String,
    val sum: Float,
    val monthDay: Int
)