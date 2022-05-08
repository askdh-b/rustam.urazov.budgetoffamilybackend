package rustam.urazov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rustam.urazov.models.Family
import rustam.urazov.models.familyStorage
import rustam.urazov.models.userStorage

fun Route.familyRouting() {
    route("/family") {
        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )

            val familyMembers = userStorage.filter { it.familyId.toString() == id }
            call.respond(familyMembers)
        }

        post {
            val family = call.receive<Family>()

            familyStorage.add(family.apply {
                id = generateFamilyId()
            })
            call.respondText("Family created correctly", status = HttpStatusCode.Created)
        }

        delete("{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (familyStorage.removeIf { it.id.toString() == id }) {
                call.respondText(
                    "Family deleted correctly",
                    status = HttpStatusCode.OK
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

fun generateFamilyId(): Int = try {
    familyStorage.last().id + 1
} catch (e: Exception) {
    1
}