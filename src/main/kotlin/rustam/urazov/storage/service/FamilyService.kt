package rustam.urazov.storage.service

import org.jetbrains.exposed.sql.transactions.transaction
import rustam.urazov.storage.Family
import rustam.urazov.storage.FamilyEntity

class FamilyService {

    fun getAllFamilies(): Iterable<Family> = transaction {
        FamilyEntity.all().map(FamilyEntity::toFamily)
    }

    fun addFamily(family: Family) = transaction {
        FamilyEntity.new {

        }
    }

    fun deleteFamily(familyId: Int) = transaction {
        FamilyEntity[familyId].delete()
    }
}