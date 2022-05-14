package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.models.body.SpendingsSourceBody
import rustam.urazov.spendingsSourceService
import rustam.urazov.storage.SpendingsSource
import rustam.urazov.userService

fun Route.spendingsSourceRouting() {
    authenticate("auth-jwt") {
        get("/spendingsSource") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            userService.getAllUser().find { it.username == username }?.let { user ->
                spendingsSourceService.getAllSpendingsSources().filter { it.userId == user.id }.let {
                    call.respond(it)
                }
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        post("/spendingsSource") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val spendingsSource = call.receive<SpendingsSourceBody>()

            val userId = userService.getAllUser().find { it.username == username }?.id

            if (userId != null) {
                spendingsSourceService.addSpendingsSource(mapToSpendingsSource(spendingsSource, userId))
                call.respond(status = HttpStatusCode.Created, "Spendings source stored correctly")
            } else call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        put("/spendingsSource/{id?}") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val newSpendingsSource = call.receive<SpendingsSourceBody>()

            val id = call.parameters["id"] ?: return@put call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Spendings source not found"
            )

            spendingsSourceService.getAllSpendingsSources().find { it.id.toString() == id }?.let { spendingsSource ->
                userService.getAllUser().find { it.username == username }?.let { user ->
                    if (user.id == spendingsSource.userId) {
                        spendingsSourceService.editSpendingsSource(
                            SpendingsSource(
                                id = spendingsSource.id,
                                userId = user.id,
                                name = newSpendingsSource.name,
                                sum = newSpendingsSource.sum,
                                monthDay = newSpendingsSource.monthDay
                            )
                        )
                        call.respond(status = HttpStatusCode.OK, message = "Spendings source edited correctly")
                    } else call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "You cannot edit else's spendings source"
                    )
                } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "Spendings source not found")
        }

        delete("/spendingsSource/{id?}") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

            userService.getAllUser().find { it.username == username }?.let { user ->
                spendingsSourceService.getAllSpendingsSources().find { it.id.toString() == id }
                    ?.let { spendingsSource ->
                        if (user.id == spendingsSource.userId) {
                            spendingsSourceService.deleteSpendingsSource(id.toInt())
                            call.respond(
                                status = HttpStatusCode.OK, message = "Spendings source removed correctly"
                            )
                        } else call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = "You cannot remove someone else's spendings source"
                        )
                    } ?: call.respond(status = HttpStatusCode.NotFound, message = "Spendings source not found")
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }
    }
}

fun mapToSpendingsSource(spendingsSource: SpendingsSourceBody, userId: Int): SpendingsSource = SpendingsSource(
    id = 0,
    userId = userId,
    name = spendingsSource.name,
    sum = spendingsSource.sum,
    monthDay = spendingsSource.monthDay
)