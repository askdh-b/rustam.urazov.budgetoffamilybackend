package rustam.urazov

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import rustam.urazov.plugins.configureAuthentication
import rustam.urazov.plugins.configureRouting
import rustam.urazov.plugins.configureSerialization
import rustam.urazov.storage.*
import rustam.urazov.storage.service.*

val familyService by lazy { FamilyService() }
val userService by lazy { UserService() }
val goalService by lazy { GoalService() }
val invitationService by lazy { InvitationService() }
val incomeService by lazy { IncomeService() }
val spendingService by lazy { SpendingService() }
val incomesSourceService by lazy { IncomesSourceService() }
val spendingsSourceService by lazy { SpendingsSourceService() }

fun main() {
    embeddedServer(Netty, environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        config = HoconApplicationConfig(ConfigFactory.load())

        module {
            initDB()
            configureRouting()
            configureSerialization()
            configureAuthentication()
        }

        connector {
            port = 8080
        }
    }).start(true)
}

const val HIKARI_CONFIG_KEY = "ktor.hikariconfig"

fun Application.initDB() {
    val configPath = environment.config.property(HIKARI_CONFIG_KEY).getString()
    val dbConfig = HikariConfig(configPath)
    val dataSource = HikariDataSource(dbConfig)
    Database.connect(dataSource)
    createTables()
    LoggerFactory.getLogger(Application::class.simpleName).info("Initialized Database")
}

private fun createTables() = transaction {
    SchemaUtils.create(
        Families,
        Users,
        Goals,
        Invitations,
        Incomes,
        Spendings,
        IncomesSources,
        SpendingsSources
    )
}