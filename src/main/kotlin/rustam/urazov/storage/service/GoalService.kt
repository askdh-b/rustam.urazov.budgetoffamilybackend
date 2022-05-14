package rustam.urazov.storage.service

import org.jetbrains.exposed.sql.transactions.transaction
import rustam.urazov.storage.Goal
import rustam.urazov.storage.GoalEntity

class GoalService {

    fun getAllGoals(): Iterable<Goal> = transaction {
        GoalEntity.all().map(GoalEntity::toGoal)
    }

    fun addGoal(goal: Goal) = transaction {
        GoalEntity.new {
            this.userId = goal.userId
            this.name = goal.name
            this.incomePercentile = goal.incomePercentile
            this.actualSum = goal.actualSum
            this.necessarySum = goal.necessarySum
        }
    }

    fun editGoal(goal: Goal) = transaction {
        GoalEntity[goal.id].apply {
            userId = goal.userId
            name = goal.name
            incomePercentile = goal.incomePercentile
            actualSum = goal.actualSum
            necessarySum = goal.necessarySum
        }
    }

    fun deleteGoal(goalId: Int) = transaction {
        GoalEntity[goalId].delete()
    }
}