package rustam.urazov.storage.service

import org.jetbrains.exposed.sql.transactions.transaction
import rustam.urazov.storage.User
import rustam.urazov.storage.UserEntity

class UserService {

    fun getAllUser(): Iterable<User> = transaction {
        UserEntity.all().map(UserEntity::toUser)
    }

    fun addUser(user: User) = transaction {
        UserEntity.new {
            this.familyId = user.familyId ?: 1
            this.firstName = user.firstName
            this.lastName = user.lastName
            this.username = user.username
            this.password = user.password
        }
    }

    fun editUser(user: User) = transaction {
        UserEntity[user.id].apply {
            familyId = user.familyId ?: 1
            firstName = user.firstName
            lastName = user.lastName
            username = user.username
            password = user.password
        }
    }
}