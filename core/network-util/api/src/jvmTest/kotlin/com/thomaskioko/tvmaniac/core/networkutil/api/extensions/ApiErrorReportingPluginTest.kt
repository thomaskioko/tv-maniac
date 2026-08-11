package com.thomaskioko.tvmaniac.core.networkutil.api.extensions

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiHttpException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.HttpExceptions
import com.thomaskioko.tvmaniac.core.networkutil.api.model.NoInternetException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test

class ApiErrorReportingPluginTest {

    private val reported = mutableListOf<Pair<String, Throwable>>()

    private fun createClient(engine: MockEngine, maxRetries: Int = 0): HttpClient = HttpClient(engine) {
        install(ContentNegotiation) { json() }

        install(ApiErrorReportingPlugin) {
            onError = { message, throwable -> reported.add(message to throwable) }
        }

        if (maxRetries > 0) {
            install(HttpRequestRetry) {
                retryIf(maxRetries) { _, httpResponse -> httpResponse.status.value in 500..599 }
                constantDelay(millis = 1)
            }
        }
    }

    private suspend fun HttpClient.requestTestPath(): ApiResponse<JsonObject> = safeRequest {
        url { path("test") }
        method = HttpMethod.Get
    }

    @Test
    fun `should not report failure given successful response`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = loadJson("success_response.json"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        createClient(engine).requestTestPath()

        reported.shouldBeEmpty()
    }

    @Test
    fun `should report failure given error response`() = runTest {
        val engine = MockEngine { _ ->
            respondError(
                status = HttpStatusCode.BadRequest,
                content = loadJson("error_response.json"),
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        createClient(engine).requestTestPath()

        reported.size shouldBe 1
        reported.first().first shouldContain "HTTP 400"
        reported.first().first shouldContain "bad request"
        val throwable = reported.first().second.shouldBeInstanceOf<ApiHttpException>()
        throwable.code shouldBe 400
    }

    @Test
    fun `should report failure given response validator throws`() = runTest {
        val engine = MockEngine { _ ->
            respondError(
                status = HttpStatusCode.InternalServerError,
                content = loadJson("error_response.json"),
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json() }

            install(ApiErrorReportingPlugin) {
                onError = { message, throwable -> reported.add(message to throwable) }
            }

            HttpResponseValidator {
                validateResponse { response ->
                    if (!response.status.isSuccess()) {
                        throw HttpExceptions(
                            response = response,
                            failureReason = "${response.status.value} Server Error",
                            cachedResponseText = response.bodyAsText(),
                        )
                    }
                }
            }
        }

        val result = client.requestTestPath()

        result.shouldBeInstanceOf<ApiResponse.Error.HttpError<JsonObject>>()
        reported.size shouldBe 1
        reported.first().first shouldContain "bad request"
    }

    @Test
    fun `should report failure once given retries are exhausted`() = runTest {
        val engine = MockEngine { _ ->
            respondError(
                status = HttpStatusCode.InternalServerError,
                content = loadJson("error_response.json"),
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        createClient(engine, maxRetries = 2).requestTestPath()

        reported.size shouldBe 1
        reported.first().first shouldContain "HTTP 500"
    }

    @Test
    fun `should not report failure given response recovers after retry`() = runTest {
        var attempts = 0
        val engine = MockEngine { _ ->
            attempts++
            if (attempts == 1) {
                respondError(
                    status = HttpStatusCode.InternalServerError,
                    content = loadJson("error_response.json"),
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            } else {
                respond(
                    content = loadJson("success_response.json"),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        }

        val result = createClient(engine, maxRetries = 2).requestTestPath()

        result.shouldBeInstanceOf<ApiResponse.Success<JsonObject>>()
        reported.shouldBeEmpty()
    }

    @Test
    fun `should report failure given request times out`() = runTest {
        val engine = MockEngine { _ ->
            throw HttpRequestTimeoutException(url = "http://test/test", timeoutMillis = 1_000L)
        }

        val result = createClient(engine).requestTestPath()

        result.shouldBeInstanceOf<ApiResponse.Error.NetworkFailure>()
        reported.size shouldBe 1
        reported.first().second.shouldBeInstanceOf<HttpRequestTimeoutException>()
    }

    @Test
    fun `should report failure given malformed response body`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = """not json""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        val result = createClient(engine).requestTestPath()

        result.shouldBeInstanceOf<ApiResponse.Error.SerializationError>()
        reported.size shouldBe 1
        reported.first().first shouldContain "Deserialization failed"
        reported.first().first shouldContain "not json"
    }

    @Test
    fun `should not report failure given device is offline`() = runTest {
        val engine = MockEngine { _ -> throw NoInternetException }

        val result = createClient(engine).requestTestPath()

        result.shouldBeInstanceOf<ApiResponse.Error.OfflineError>()
        reported.shouldBeEmpty()
    }

    @Test
    fun `should not report failure given user is not authenticated`() = runTest {
        val engine = MockEngine { _ -> throw AuthenticationException(message = "User is not authenticated") }

        val result = createClient(engine).requestTestPath()

        result.shouldBeInstanceOf<ApiResponse.Unauthenticated>()
        reported.shouldBeEmpty()
    }

    @Test
    fun `should not report failure given request is cancelled`() = runTest {
        val engine = MockEngine { _ -> throw CancellationException("cancelled by test") }
        val client = createClient(engine)

        shouldThrow<CancellationException> {
            client.safeRequest<JsonObject> {
                url { path("test") }
                method = HttpMethod.Get
            }
        }

        reported.shouldBeEmpty()
    }
}
