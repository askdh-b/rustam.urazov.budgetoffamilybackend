package rustam.urazov.storage.service

import org.jetbrains.exposed.sql.transactions.transaction
import rustam.urazov.storage.Spending
import rustam.urazov.storage.SpendingEntity

class SpendingService {

    fun getAllSpendings() = transaction {
        SpendingEntity.all().map(SpendingEntity::toSpending)
    }

    fun addSpending(spending: Spending) = transaction {
        SpendingEntity.new {
            this.userId = spending.userId
            this.sum = spending.sum
            this.name = spending.name
            this.creationDate = spending.creationDate
        }
    }
}