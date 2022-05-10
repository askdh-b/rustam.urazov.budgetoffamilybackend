package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.models.Goal
import rustam.urazov.models.familyStorage
import rustam.urazov.models.goalStorage
import rustam.urazov.models.userStorage
import java.util.*

fun Route.goalRouting() {
    authenticate("auth-jwt") {
        get("/goal") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            principal.expiresAt?.time?.minus(System.currentTimeMillis()) ?: let {
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }

            userStorage.find { it.username == username }?.let { user ->
                familyStorage.find { it.id == user.familyId }?.let { family ->
                    userStorage.filter { it.familyId == family.id }.let { users ->
                        val goals = mutableListOf<Goal>()
                        for (u in users) {
                            goals.addAll(goalStorage.filter { it.userId == user.id })
                        }
                        call.respond(goals)
                    }
                } ?: let {
                    val goals = goalStorage.filter { it.userId == user.id }
                    call.respond(goals)
                }
            } ?: call.respond(HttpStatusCode.NotFound, "Goals not found")
        }

        post("/goal") {
            val goal = call.receive<Goal>()

            goalStorage.add(goal.apply {
                id = generateGoalId()
                creationDate = Date().toString()
            })

            call.respondText(
                "Goal stored correctly", status = HttpStatusCode.Created
            )
        }

        put("/goal/{id?}") {
            val newGoal = call.receive<Goal>()
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)

            goalStorage.find { it.id.toString() == id }?.let {
                it.apply {
                    name = newGoal.name
                    incomePercentile = newGoal.incomePercentile
                    progress = newGoal.progress
                    sum = newGoal.sum
                }

                call.respondText("Goal edited correctly", status = HttpStatusCode.OK)
            } ?: call.respondText("Goal not found", status = HttpStatusCode.NotFound)
        }

        delete("/goal/{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

            if (goalStorage.removeIf { it.id.toString() == id }) {
                call.respondText(
                    "Goal removed correctly", status = HttpStatusCode.OK
                )
            } else {
                call.respondText(
                    "Not found", status = HttpStatusCode.NotFound
                )
            }
        }
    }
}

fun generateGoalId(): Int = try {
    goalStorage.last().id + 1
} catch (e: Exception) {
    1
}