package rustam.urazov.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.md5
import rustam.urazov.models.Token
import rustam.urazov.models.body.AuthBody
import rustam.urazov.models.Username
import rustam.urazov.models.userStorage
import rustam.urazov.toHex
import java.util.*

fun Application.configureAuthentication() {
    val secret = "I hate niggers"
    val issuer = "http://127.0.0.1:8080/"
    val audience = "http://127.0.0.1:8080/"
    val myRealm = ""

    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm

            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )

            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

            challenge { defaultScheme, realm ->
                call.respond(status = HttpStatusCode.Unauthorized, message = "Token is not valid or has expired")
            }
        }

        jwt("refresh-auth-jwt") {
            realm = myRealm

            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )

            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    routing {
        post("/auth") {
            val user = call.receive<AuthBody>()

            userStorage.find { it.username == user.username }?.let {
                if (it.password == md5(user.password).toHex()) {
                    val accessToken = JWT.create()
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .withClaim("username", user.username)
                        .withExpiresAt(Date(System.currentTimeMillis() + 86400000))
                        .sign(Algorithm.HMAC256(secret))

                    val refreshToken = JWT.create()
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .withClaim("username", user.username)
                        .sign(Algorithm.HMAC256(secret))

                    val token = Token(accessToken = accessToken, refreshToken = refreshToken)
                    call.respond(token)
                }
            } ?: call.respond(status = HttpStatusCode.Unauthorized, message = "Invalid username or password")
        }

        authenticate("refresh-auth-jwt") {
            post("/refresh") {
                val user = call.receive<Username>()

                val accessToken = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("username", user.username)
                    .withExpiresAt(Date(System.currentTimeMillis() + 86400000))
                    .sign(Algorithm.HMAC256(secret))

                val refreshToken = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withClaim("username", user.username)
                    .sign(Algorithm.HMAC256(secret))

                val token = Token(accessToken = accessToken, refreshToken = refreshToken)
                call.respond(token)
            }
        }
    }
}