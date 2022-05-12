package rustam.urazov.storage

import org.jetbrains.exposed.sql.transactions.transaction

class UserService {

    fun getAllUser(): Iterable<User> = transaction {
        UserEntity.all().map(UserEntity::toUser)
    }

    fun addUser(user: User) = transaction {
        UserEntity.new {
            this.familyId = user.familyId ?: 0
            this.firstName = user.firstName
            this.lastName = user.lastName
            this.username = user.username
            this.password = user.password
        }
    }
}