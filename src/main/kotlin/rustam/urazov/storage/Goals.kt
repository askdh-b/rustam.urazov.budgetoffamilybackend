package rustam.urazov.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Goals : IntIdTable() {
    val userId = integer("user_id")
    val name = varchar("goal_name", 255)
    val incomePercentile = float("goal_income_percentile")
    val actualSum = float("goal_actual_sum")
    val necessarySum = float("goal_necessary_sum")
}

class GoalEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<GoalEntity>(Goals)

    var userId by Goals.userId
    var name by Goals.name
    var incomePercentile by Goals.incomePercentile
    var actualSum by Goals.actualSum
    var necessarySum by Goals.necessarySum

    fun toGoal() = Goal(id.value, userId, name, incomePercentile, actualSum, necessarySum)
}

data class Goal(
    val id: Int,
    val userId: Int,
    val name: String,
    val incomePercentile: Float,
    val actualSum: Float,
    val necessarySum: Float
)