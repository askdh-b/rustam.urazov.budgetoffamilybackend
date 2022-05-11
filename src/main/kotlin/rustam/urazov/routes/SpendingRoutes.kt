package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.models.*
import rustam.urazov.models.body.SpendingBody
import java.util.*

fun Route.spendingRouting() {
    authenticate("auth-jwt") {
        get("/spending") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            userStorage.find { it.username == username }?.let { user ->
                familyStorage.find { it.id == user.id }?.let { family ->
                    userStorage.filter { it.familyId == family.id }.let { users ->
                        val spendings = mutableListOf<Spending>()

                        for (u in users) {
                            spendings.addAll(spendingStorage.filter { it.userId == u.id })
                        }
                        call.respond(spendings)
                    }
                } ?: let {
                    val incomes = spendingStorage.filter { it.userId == user.id }
                    call.respond(incomes)
                }
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        post("/spending") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val spending = call.receive<SpendingBody>()

            val userId = userStorage.find { it.username == username }?.id

            if (userId != null) {
                spendingStorage.add(mapToSpending(spending, userId))
                call.respond(
                    status = HttpStatusCode.Created, message = "Spending stored correctly"
                )
            } else call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }
    }
}

fun mapToSpending(spending: SpendingBody, userId: Int): Spending = Spending(
    id = generateSpendingId(),
    userId = userId,
    name = spending.name,
    sum = spending.sum,
    date = Date().toString()
)

fun generateSpendingId(): Int = try {
    spendingStorage.last().id + 1
} catch (e: Exception) {
    1
}