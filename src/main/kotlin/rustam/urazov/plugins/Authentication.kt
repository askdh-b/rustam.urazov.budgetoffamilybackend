package rustam.urazov.plugins

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import rustam.urazov.applicationHttpClient

fun Application.configureAuthentication(httpClient: HttpClient = applicationHttpClient) {
    install(Authentication) {
        oauth("auth-oauth") {
            urlProvider = { "http://127.0.0.1:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "budgetoffamily",
                    authorizeUrl = "http://127.0.0.1:8080/oauth2/auth",
                    accessTokenUrl = "http://127.0.0.1:8080/oayth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("CLIENT_ID"),
                    clientSecret = System.getenv("CLIENT_SECRET")
                )
            }
            client = httpClient
        }
    }
}

