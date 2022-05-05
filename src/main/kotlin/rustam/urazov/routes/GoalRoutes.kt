package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.models.Goal
import rustam.urazov.models.goalStorage
import java.text.SimpleDateFormat
import java.util.*

fun Route.goalRouting() {
    route("/goal") {
        get("{userId?}") {
            val id = call.parameters["userId"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val goal = goalStorage.find { it.userId.toString() == id } ?: return@get call.respondText(
                "No goals with userId $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(goal)
        }
        post {
            val goal = call.receive<Goal>()
            goalStorage.add(
                Goal(
                    id = generateGoalId(),
                    userId = goal.userId,
                    name = goal.name,
                    incomePercentile = goal.incomePercentile,
                    progress = goal.progress,
                    sum = goal.sum,
                    creationDate = SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(Date())
                )
            )
            call.respondText(
                "Goal stored correctly",
                status = HttpStatusCode.Created
            )
        }
        put("{id?}") {
            val newGoal = call.receive<Goal>()
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val oldGoal =
                goalStorage.find { it.id.toString() == id } ?: return@put call.respond(HttpStatusCode.NotFound)
            goalStorage.removeIf { it.id.toString() == id }
            goalStorage.add(
                Goal(
                    id = oldGoal.id,
                    userId = oldGoal.userId,
                    name = newGoal.name,
                    incomePercentile = newGoal.incomePercentile,
                    progress = newGoal.progress,
                    sum = newGoal.sum,
                    creationDate = oldGoal.creationDate
                )
            )
            call.respondText(
                "Goal edited correctly",
                status = HttpStatusCode.Created
            )
        }
        delete("{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (goalStorage.removeIf { it.id.toString() == id }) {
                call.respondText(
                    "Goal removed correctly",
                    status = HttpStatusCode.NotFound
                )
            } else {
                call.respondText(
                    "Not found",
                    status = HttpStatusCode.NotFound
                )
            }
        }
    }
}

fun generateGoalId(): Int = if (goalStorage.size > 0) goalStorage[goalStorage.lastIndex].id + 1 else 1