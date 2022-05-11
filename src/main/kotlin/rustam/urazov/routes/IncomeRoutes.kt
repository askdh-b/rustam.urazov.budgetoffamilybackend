package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.models.*
import rustam.urazov.models.body.IncomeBody
import java.util.*

fun Route.incomeRouting() {
    authenticate("auth-jwt") {
        get("/income") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            userStorage.find { it.username == username }?.let { user ->
                familyStorage.find { it.id == user.id }?.let { family ->
                    userStorage.filter { it.familyId == family.id }.let { users ->
                        val incomes = mutableListOf<Income>()

                        for (u in users) {
                            incomes.addAll(incomeStorage.filter { it.userId == u.id })
                        }
                        call.respond(incomes)
                    }
                } ?: let {
                    val incomes = incomeStorage.filter { it.userId == user.id }
                    call.respond(incomes)
                }
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        post("/income") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val income = call.receive<IncomeBody>()

            val userId = userStorage.find { it.username == username }?.id

            if (userId != null) {
                incomeStorage.add(mapToIncome(income, userId))
                call.respond(
                    status = HttpStatusCode.Created, message = "Income stored correctly"
                )
            } else call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }
    }
}

fun mapToIncome(income: IncomeBody, userId: Int): Income = Income(
    id = generateIncomeId(),
    userId = userId,
    name = income.name,
    sum = income.sum,
    date = Date().toString()
)

fun generateIncomeId(): Int = try {
    incomeStorage.last().id + 1
} catch (e: Exception) {
    1
}