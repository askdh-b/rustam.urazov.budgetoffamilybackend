package rustam.urazov

import com.typesafe.config.ConfigFactory
import io.ktor.server.netty.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import org.slf4j.LoggerFactory
import rustam.urazov.plugins.*

fun main() {
    embeddedServer(Netty, environment = applicationEngineEnvironment {
        log =LoggerFactory.getLogger("ktor.application")
        config = HoconApplicationConfig(ConfigFactory.load())

        module {
            configureRouting()
            configureSerialization()
        }

        connector {
            port = 8080
            host = "127.0.0.1"
        }
    }).start(true)
}
