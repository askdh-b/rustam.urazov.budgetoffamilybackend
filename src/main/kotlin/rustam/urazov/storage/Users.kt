package rustam.urazov.storage

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val familyId = integer("family_id")
    val firstName = varchar("user_first_name", 255)
    val lastName = varchar("user_last_name", 255)
    val username = varchar("user_username", 255)
    val password = varchar("user_password", 255)
}

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var familyId by Users.familyId
    var firstName by Users.firstName
    var lastName by Users.lastName
    var username by Users.username
    var password by Users.password

    fun toUser() = User(id.value, familyId, firstName, lastName, username, password)
}

data class User(
    val id: Int,
    val familyId: Int?,
    val firstName: String,
    val lastName: String,
    val username: String,
    val password: String
)