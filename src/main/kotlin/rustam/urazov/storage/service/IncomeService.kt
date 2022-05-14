package rustam.urazov.storage.service

import org.jetbrains.exposed.sql.transactions.transaction
import rustam.urazov.storage.Income
import rustam.urazov.storage.IncomeEntity

class IncomeService {

    fun getAllIncomes() = transaction {
        IncomeEntity.all().map(IncomeEntity::toIncome)
    }

    fun addIncome(income: Income) = transaction {
        IncomeEntity.new {
            this.userId = income.userId
            this.sum = income.sum
            this.name = income.name
            this.creationDate = income.creationDate
        }
    }
}