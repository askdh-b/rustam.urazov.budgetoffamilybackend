package rustam.urazov.routes

import com.auth0.jwt.JWT
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.generateToken
import rustam.urazov.md5
import rustam.urazov.models.*
import rustam.urazov.toHex

fun Route.authRouting() {

}