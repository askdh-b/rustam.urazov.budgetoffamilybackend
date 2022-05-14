package rustam.urazov.storage.service

import org.jetbrains.exposed.sql.transactions.transaction
import rustam.urazov.storage.SpendingsSource
import rustam.urazov.storage.SpendingsSourceEntity

class SpendingsSourceService {

    fun getAllSpendingsSources() = transaction {
        SpendingsSourceEntity.all().map(SpendingsSourceEntity::toSpendingsSource)
    }

    fun addSpendingsSource(spendingsSource: SpendingsSource) = transaction {
        SpendingsSourceEntity.new {
            this.userId = spendingsSource.userId
            this.name = spendingsSource.name
            this.sum = spendingsSource.sum
            this.monthDay = spendingsSource.monthDay
        }
    }

    fun editSpendingsSource(spendingsSource: SpendingsSource) = transaction {
        SpendingsSourceEntity[spendingsSource.id].apply {
            userId = spendingsSource.userId
            name = spendingsSource.name
            sum = spendingsSource.sum
            monthDay = spendingsSource.monthDay
        }
    }

    fun deleteSpendingsSource(spendingsSourceId: Int) = transaction {
        SpendingsSourceEntity[spendingsSourceId].delete()
    }
}