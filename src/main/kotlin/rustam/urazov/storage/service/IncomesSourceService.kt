package rustam.urazov.storage.service

import org.jetbrains.exposed.sql.transactions.transaction
import rustam.urazov.storage.IncomesSource
import rustam.urazov.storage.IncomesSourceEntity

class IncomesSourceService {

    fun getAllIncomesSources() = transaction {
        IncomesSourceEntity.all().map(IncomesSourceEntity::toIncomesSource)
    }

    fun addIncomesSource(incomesSource: IncomesSource) = transaction {
        IncomesSourceEntity.new {
            this.userId = incomesSource.userId
            this.name = incomesSource.name
            this.sum = incomesSource.sum
            this.monthDay = incomesSource.monthDay
        }
    }

    fun editIncomesSource(incomesSource: IncomesSource) = transaction {
        IncomesSourceEntity[incomesSource.id].apply {
            userId = incomesSource.userId
            name = incomesSource.name
            sum = incomesSource.sum
            monthDay = incomesSource.monthDay
        }
    }

    fun deleteIncomesSource(incomesSourceId: Int) = transaction {
        IncomesSourceEntity[incomesSourceId].delete()
    }
}