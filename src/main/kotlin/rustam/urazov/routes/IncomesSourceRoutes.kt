package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.incomesSourceService
import rustam.urazov.models.body.IncomesSourceBody
import rustam.urazov.storage.IncomesSource
import rustam.urazov.userService

fun Route.incomesSourceRouting() {
    authenticate("auth-jwt") {
        get("/incomesSource") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            userService.getAllUser().find { it.username == username }?.let { user ->
                incomesSourceService.getAllIncomesSources().filter { it.userId == user.id }.let {
                    call.respond(it)
                }
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        get("/incomesSource/{id?}") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val id = call.parameters["id"] ?: return@get call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Incomes source not found"
            )

            userService.getAllUser().find { it.username == username }?.let { user ->
                incomesSourceService.getAllIncomesSources().find { it.id.toString() == id }
                    ?.let { incomesSource ->
                        if (user.id == incomesSource.id) call.respond(incomesSource)
                        else call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = "You can't view other people's incomes sources"
                        )
                    } ?: call.respond(status = HttpStatusCode.NotFound, message = "Incomes source not found")
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        post("/incomesSource") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val incomesSource = call.receive<IncomesSourceBody>()

            val userId = userService.getAllUser().find { it.username == username }?.id

            if (userId != null) {
                incomesSourceService.addIncomesSource(mapToIncomesSource(incomesSource, userId))
                call.respond(status = HttpStatusCode.Created, message = "Incomes source stored correctly")
            } else call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        put("/incomesSource/{id?}") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val newIncomesSource = call.receive<IncomesSourceBody>()

            val id = call.parameters["id"] ?: return@put call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Incomes source not found"
            )

            incomesSourceService.getAllIncomesSources().find { it.id.toString() == id }?.let { incomesSource ->
                userService.getAllUser().find { it.username == username }?.let { user ->
                    if (user.id == incomesSource.userId) {
                        incomesSourceService.editIncomesSource(
                            IncomesSource(
                                id = incomesSource.id,
                                userId = user.id,
                                name = newIncomesSource.name,
                                sum = newIncomesSource.sum,
                                monthDay = newIncomesSource.monthDay
                            )
                        )
                        call.respond(status = HttpStatusCode.OK, message = "Incomes source edited correctly")
                    } else call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "You cannot edit else's incomes sources"
                    )
                } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "Incomes source not found")
        }

        delete("/incomesSource/{id?}") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

            userService.getAllUser().find { it.username == username }?.let { user ->
                incomesSourceService.getAllIncomesSources().find { it.id.toString() == id }?.let { incomesSource ->
                    if (user.id == incomesSource.userId) {
                        incomesSourceService.deleteIncomesSource(id.toInt())
                        call.respond(
                            status = HttpStatusCode.OK, message = "Incomes source removed correctly"
                        )
                    } else call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "You cannot remove someone else's incomes sources"
                    )
                } ?: call.respond(status = HttpStatusCode.NotFound, message = "Incomes source not found")
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }
    }
}

fun mapToIncomesSource(incomesSource: IncomesSourceBody, userId: Int): IncomesSource = IncomesSource(
    id = 0,
    userId = userId,
    name = incomesSource.name,
    sum = incomesSource.sum,
    monthDay = incomesSource.monthDay
)