package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.models.Goal
import rustam.urazov.models.body.GoalBody
import rustam.urazov.models.body.GoalBodyForEdit
import rustam.urazov.models.familyStorage
import rustam.urazov.models.goalStorage
import rustam.urazov.models.userStorage
import java.util.*

fun Route.goalRouting() {
    authenticate("auth-jwt") {
        get("/goal") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            userStorage.find { it.username == username }?.let { user ->
                familyStorage.find { it.id == user.familyId }?.let { family ->
                    userStorage.filter { it.familyId == family.id }.let { users ->
                        val goals = mutableListOf<Goal>()
                        for (u in users) {
                            goals.addAll(goalStorage.filter { it.userId == u.id })
                        }
                        call.respond(goals)
                    }
                } ?: let {
                    val goals = goalStorage.filter { it.userId == user.id }
                    call.respond(goals)
                }
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        post("/goal") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val goal = call.receive<GoalBody>()

            val userId = userStorage.find { it.username == username }?.id

            if (userId != null) {
                goalStorage.add(mapToGoal(goal, userId))
                call.respond(
                    status = HttpStatusCode.Created, message = "Goal stored correctly"
                )
            } else call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        put("/goal/{id?}") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val newGoal = call.receive<GoalBodyForEdit>()

            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)

            goalStorage.find { it.id.toString() == id }?.let { goal ->
                userStorage.find { it.username == username }?.let { user ->
                    if (user.id == goal.userId) {
                        goal.apply {
                            name = newGoal.name
                            incomePercentile = newGoal.incomePercentile
                            progress = newGoal.progress
                            sum = newGoal.sum
                        }
                        call.respondText("Goal edited correctly", status = HttpStatusCode.OK)
                    } else {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = "You cannot edit someone else's target"
                        )
                    }
                }
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "Goal not found")
        }

        delete("/goal/{id?}") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

            if (goalStorage.removeIf { it.id.toString() == id }) {
                call.respond(
                    status = HttpStatusCode.OK, message = "Goal removed correctly"
                )
            } else {
                call.respond(
                    status = HttpStatusCode.NotFound, message = "Not found"
                )
            }
        }
    }
}

fun mapToGoal(goal: GoalBody, userId: Int): Goal = Goal(
    id = generateGoalId(),
    userId = userId,
    name = goal.name,
    incomePercentile = goal.incomePercentile,
    progress = 0.00,
    sum = goal.sum,
    creationDate = Date().toString()
)

fun generateGoalId(): Int = try {
    goalStorage.last().id + 1
} catch (e: Exception) {
    1
}