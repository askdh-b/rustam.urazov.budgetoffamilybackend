package rustam.urazov

import com.typesafe.config.ConfigFactory
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory
import rustam.urazov.plugins.configureAuthentication
import rustam.urazov.plugins.configureRouting
import rustam.urazov.plugins.configureSerialization

fun main() {
    embeddedServer(Netty, environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        config = HoconApplicationConfig(ConfigFactory.load())

        module {
            configureRouting()
            configureSerialization()
            configureAuthentication()
        }

        connector {
            port = 8080
            host = "127.0.0.1"
        }
    }).start(true)
}