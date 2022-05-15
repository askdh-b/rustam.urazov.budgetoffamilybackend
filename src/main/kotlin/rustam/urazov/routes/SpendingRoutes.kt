package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.familyService
import rustam.urazov.models.body.SpendingBody
import rustam.urazov.spendingService
import rustam.urazov.storage.Spending
import rustam.urazov.userService
import java.util.*

fun Route.spendingRouting() {
    authenticate("auth-jwt") {
        get("/spending") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            userService.getAllUser().find { it.username == username }?.let { user ->
                familyService.getAllFamilies().find { it.id == user.familyId }?.let { family ->
                    userService.getAllUser().filter { it.familyId == family.id }.let { users ->
                        val spendings = mutableListOf<Spending>()

                        for (u in users) {
                            spendings.addAll(spendingService.getAllSpendings().filter { it.userId == u.id })
                        }
                        call.respond(spendings)
                    }
                } ?: let {
                    val spendings = spendingService.getAllSpendings().filter { it.userId == user.id }
                    call.respond(spendings)
                }
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        post("/spending") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val spending = call.receive<SpendingBody>()

            val userId = userService.getAllUser().find { it.username == username }?.id

            if (userId != null) {
                spendingService.addSpending(mapToSpending(spending, userId))
                call.respond(
                    status = HttpStatusCode.Created, message = "Spending stored correctly"
                )
            } else call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }
    }
}

fun mapToSpending(spending: SpendingBody, userId: Int): Spending = Spending(
    id = 0,
    userId = userId,
    name = spending.name,
    sum = spending.sum,
    creationDate = Date().toString()
)