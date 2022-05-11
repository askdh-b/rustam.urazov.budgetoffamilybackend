package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.models.IncomesSource
import rustam.urazov.models.body.IncomesSourceBody
import rustam.urazov.models.incomesSourceStorage
import rustam.urazov.models.userStorage

fun Route.incomesSourceRouting() {
    authenticate("auth-jwt") {
        get("/incomesSource") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            userStorage.find { it.username == username }?.let { user ->
                incomesSourceStorage.filter { it.userId == user.id }.let {
                    call.respond(it)
                }
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        post("/incomesSource") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val incomesSource = call.receive<IncomesSourceBody>()

            val userId = userStorage.find { it.username == username }?.id

            if (userId != null) {
                incomesSourceStorage.add(mapToIncomesSource(incomesSource, userId))
                call.respond(status = HttpStatusCode.Created, "Incomes source stored correctly")
            } else call.respond(status = HttpStatusCode.NotFound, message = "User npt found")
        }

        put("/incomesSource/{id?}") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val newIncomesSource = call.receive<IncomesSourceBody>()

            val id = call.parameters["id"] ?: return@put call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Incomes source not found"
            )

            incomesSourceStorage.find { it.id.toString() == id }?.let { incomesSource ->
                userStorage.find { it.username == username }?.let { user ->
                    if (user.id == incomesSource.userId) {
                        incomesSource.apply {
                            name = newIncomesSource.name
                            sum = newIncomesSource.sum
                            monthDay = newIncomesSource.monthDay
                        }
                        call.respond(status = HttpStatusCode.OK, message = "Incomes source edited correctly")
                    } else call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "You cannot edit else's incomes source"
                    )
                } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "Incomes source not found")
        }

        delete("/incomesSource/{id?}") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

            userStorage.find { it.username == username }?.let { user ->
                incomesSourceStorage.find { it.id.toString() == id }?.let { incomesSource ->
                    if (user.id == incomesSource.userId) {
                        if (incomesSourceStorage.removeIf { it.id.toString() == id }) {
                            call.respond(
                                status = HttpStatusCode.OK, message = "Incomes source removed correctly"
                            )
                        } else {
                            call.respond(
                                status = HttpStatusCode.BadRequest, message = "Incomes source not removed"
                            )
                        }
                    } else call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "You cannot remove someone else's incomes source"
                    )
                } ?: call.respond(status = HttpStatusCode.NotFound, message = "Incomes source not found")
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }
    }
}

fun mapToIncomesSource(incomesSource: IncomesSourceBody, userId: Int): IncomesSource = IncomesSource(
    id = generateIncomesSourceId(),
    userId = userId,
    name = incomesSource.name,
    sum = incomesSource.sum,
    monthDay = incomesSource.monthDay
)

fun generateIncomesSourceId(): Int = try {
    incomesSourceStorage.last().id + 1
} catch (e: Exception) {
    1
}