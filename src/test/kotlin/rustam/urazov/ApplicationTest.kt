package rustam.urazov

import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*
import rustam.urazov.models.User

class ApplicationTest {
    @Test
    fun testCreteUser() = testApplication {
        val response = client.post("goal/")
        assertEquals(
            """""",
            response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }
}