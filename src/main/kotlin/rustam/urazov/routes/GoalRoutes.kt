package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.familyService
import rustam.urazov.goalService
import rustam.urazov.models.body.GoalBody
import rustam.urazov.models.body.GoalBodyForEdit
import rustam.urazov.storage.Goal
import rustam.urazov.userService

fun Route.goalRouting() {
    authenticate("auth-jwt") {
        get("/goal") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            userService.getAllUser().find { it.username == username }?.let { user ->
                familyService.getAllFamilies().find { it.id == user.familyId }?.let { family ->
                    userService.getAllUser().filter { it.familyId == family.id }.let { users ->
                        val goals = mutableListOf<Goal>()

                        for (u in users) {
                            goals.addAll(goalService.getAllGoals().filter { it.userId == u.id })
                        }
                        call.respond(goals)
                    }
                } ?: let {
                    val goals = goalService.getAllGoals().filter { it.userId == user.id }
                    call.respond(goals)
                }
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        get("/goal/{id?}") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val id = call.parameters["id"] ?: return@get call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Goal not found"
            )

            userService.getAllUser().find { it.username == username }?.let { user ->
                goalService.getAllGoals().find { it.id.toString() == id }
                    ?.let { goal ->
                        if (user.id == goal.id) call.respond(goal)
                        else call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = "You can't view other people's goals"
                        )
                    } ?: call.respond(status = HttpStatusCode.NotFound, message = "Goal not found")
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }

        post("/goal") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val goal = call.receive<GoalBody>()

            val userId = userService.getAllUser().find { it.username == username }?.id

            if (userId != null) {
                goalService.addGoal(mapToGoal(goal, userId))
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

            goalService.getAllGoals().find { it.id.toString() == id }?.let { goal ->
                userService.getAllUser().find { it.username == username }?.let { user ->
                    if (user.id == goal.userId) {
                        goalService.editGoal(
                            Goal(
                                id = goal.id,
                                userId = user.id,
                                name = newGoal.name,
                                incomePercentile = newGoal.incomePercentile,
                                actualSum = newGoal.progress,
                                necessarySum = newGoal.sum
                            )
                        )
                        call.respondText("Goal edited correctly", status = HttpStatusCode.OK)
                    } else {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = "You cannot edit someone else's goals"
                        )
                    }
                } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "Goal not found")
        }

        delete("/goal/{id?}") {
            val principal = call.principal<JWTPrincipal>()

            val username = principal!!.payload.getClaim("username").asString()

            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

            userService.getAllUser().find { it.username == username }?.let { user ->
                goalService.getAllGoals().find { it.id.toString() == id }?.let { goal ->
                    if (user.id == goal.userId) {
                        goalService.deleteGoal(id.toInt())
                        call.respond(
                            status = HttpStatusCode.OK, message = "Goal removed correctly"
                        )
                    } else call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "You cannot remove someone else's goals"
                    )
                } ?: call.respond(status = HttpStatusCode.NotFound, message = "Goal not found")
            } ?: call.respond(status = HttpStatusCode.NotFound, message = "User not found")
        }
    }
}

fun mapToGoal(goal: GoalBody, userId: Int): Goal = Goal(
    id = 0,
    userId = userId,
    name = goal.name,
    incomePercentile = goal.incomePercentile,
    actualSum = 0.0F,
    necessarySum = goal.sum
)