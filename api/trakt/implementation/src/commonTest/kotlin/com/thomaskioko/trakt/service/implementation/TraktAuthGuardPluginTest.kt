package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.RequiresAuth
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class TraktAuthGuardPluginTest {

    private fun createClient(isAuthenticated: () -> Boolean): HttpClient {
        val mockEngine = MockEngine { _ ->
            respond(
                content = """{}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        return HttpClient(mockEngine) {
            install(ContentNegotiation) { json() }
            install(TraktAuthGuard) {
                this.isAuthenticated = isAuthenticated
            }
        }
    }

    @Test
    fun `should throw AuthenticationException given auth required and user not authenticated`() = runTest {
        val client = createClient(isAuthenticated = { false })

        shouldThrow<AuthenticationException> {
            client.get("/test") {
                attributes.put(RequiresAuth, true)
            }
        }
    }

    @Test
    fun `should allow request given auth required and user is authenticated`() = runTest {
        val client = createClient(isAuthenticated = { true })

        val response = client.get("/test") {
            attributes.put(RequiresAuth, true)
        }

        response.status shouldBe HttpStatusCode.OK
    }

    @Test
    fun `should allow request given auth not required and user not authenticated`() = runTest {
        val client = createClient(isAuthenticated = { false })

        val response = client.get("/test")

        response.status shouldBe HttpStatusCode.OK
    }

    @Test
    fun `should allow request given RequiresAuth is false`() = runTest {
        val client = createClient(isAuthenticated = { false })

        val response = client.get("/test") {
            attributes.put(RequiresAuth, false)
        }

        response.status shouldBe HttpStatusCode.OK
    }
}
