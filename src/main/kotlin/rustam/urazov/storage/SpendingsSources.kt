package rustam.urazov.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object SpendingsSources : IntIdTable() {
    val userId = integer("user_id")
    val name = varchar("spendings_source_name", 255)
    val sum = float("spendings_source_sum")
    val monthDay = integer("spendings_source_month_day")
}

class SpendingsSourceEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SpendingsSourceEntity>(SpendingsSources)

    var userId by SpendingsSources.userId
    var name by SpendingsSources.name
    var sum by SpendingsSources.sum
    var monthDay by SpendingsSources.monthDay

    fun toSpendingsSource() = SpendingsSource(id.value, userId, name, sum, monthDay)
}

data class SpendingsSource(
    val id: Int,
    val userId: Int,
    val name: String,
    val sum: Float,
    val monthDay: Int
)