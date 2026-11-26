package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.RequiresAuth
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldNotContain
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

class SimklAuthGuardTest {

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
            install(SimklAuthGuard) {
                this.isAuthenticated = isAuthenticated
            }
        }
    }

    @Test
    fun `should not include request url in message given auth required and user not authenticated`() = runTest {
        val client = createClient(isAuthenticated = { false })

        val exception = shouldThrow<AuthenticationException> {
            client.get("https://api.example.test/users/jane-doe/lists?extended=full") {
                attributes.put(RequiresAuth, true)
            }
        }

        exception.message.orEmpty() shouldNotContain "jane-doe"
        exception.message.orEmpty() shouldNotContain "api.example.test"
        exception.message.orEmpty() shouldNotContain "extended=full"
    }
}
