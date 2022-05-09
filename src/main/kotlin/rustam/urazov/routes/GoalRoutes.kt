package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import rustam.urazov.isAuthorized
import rustam.urazov.models.Goal
import rustam.urazov.models.goalStorage
import rustam.urazov.plugins.AuthSession
import java.text.SimpleDateFormat
import java.util.*

fun Route.goalRouting() {
    route("/goal") {
        get("{userId?}") {
            val authSession = call.sessions.get<AuthSession>()
            val id = call.parameters["userId"] ?: return@get call.respondText(
                "Missing id", status = HttpStatusCode.BadRequest
            )

            if (isAuthorized(authSession)) {
                val goal = goalStorage.filter { it.userId.toString() == id }
                call.respond(goal)
            } else {
                call.respondText("Authentication error", status = HttpStatusCode.Unauthorized)
            }
        }

        post {
            val goal = call.receive<Goal>()

            goalStorage.add(goal.apply {
                id = generateGoalId()
                creationDate = SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(Date())
            })
            call.respondText(
                "Goal stored correctly", status = HttpStatusCode.Created
            )
        }

        put("{id?}") {
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

        delete("{id?}") {
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