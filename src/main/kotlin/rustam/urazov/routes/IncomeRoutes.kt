package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.familyService
import rustam.urazov.incomeService
import rustam.urazov.models.body.IncomeBody
import rustam.urazov.storage.Income
import rustam.urazov.userService
import java.util.*

fun Route.incomeRouting() {
    authenticate("auth-jwt") {
        get("/income") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            userService.getAllUser().find { it.username == username }?.let { user ->
                familyService.getAllFamilies().find { it.id == user.familyId }?.let { family ->
                    userService.getAllUser().filter { it.familyId == family.id }.let { users ->
                        val incomes = mutableListOf<Income>()

                        for (u in users) {
                            incomes.addAll(incomeService.getAllIncomes().filter { it.userId == u.id })
                        }
                        call.respond(incomes)
                    }
                } ?: let {
                    val incomes = incomeService.getAllIncomes().filter { it.userId == user.id }
                    call.respond(incomes)
                }
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        get("/income/{id?}") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val id = call.parameters["id"] ?: return@get call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Income not found"
            )

            userService.getAllUser().find { it.username == username }?.let { user ->
                incomeService.getAllIncomes().find { it.id.toString() == id }
                    ?.let { income ->
                        if (user.id == income.id) call.respond(income)
                        else call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = "You can't view other people's incomes"
                        )
                    } ?: call.respond(status = HttpStatusCode.NotFound, message = "Income not found")
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        post("/income") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val income = call.receive<IncomeBody>()

            val userId = userService.getAllUser().find { it.username == username }?.id

            if (userId != null) {
                incomeService.addIncome(mapToIncome(income, userId))
                call.respond(
                    status = HttpStatusCode.Created, message = "Income stored correctly"
                )
            } else call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }
    }
}

fun mapToIncome(income: IncomeBody, userId: Int): Income = Income(
    id = 0,
    userId = userId,
    name = income.name,
    sum = income.sum,
    creationDate = Date().toString()
)