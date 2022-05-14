package rustam.urazov.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Families : IntIdTable()

class FamilyEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FamilyEntity>(Families)

    fun toFamily() = Family(id.value)
}

data class Family(val id: Int)