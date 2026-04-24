package com.thomaskioko.tvmaniac.core.networkutil.api.extensions

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test

class ApiResponseExtensionsTest {

    private fun createClient(engine: MockEngine): HttpClient = HttpClient(engine) {
        install(ContentNegotiation) { json() }
        expectSuccess = true
    }

    @Test
    fun `should return Success given successful response`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = loadJson("success_response.json"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = createClient(engine)

        val result: ApiResponse<JsonObject> = client.safeRequest {
            url { path("test") }
            method = HttpMethod.Get
        }

        result.shouldBeInstanceOf<ApiResponse.Success<JsonObject>>()
        result.body["id"]!!.jsonPrimitive.long shouldBe 1L
        result.body["name"]!!.jsonPrimitive.content shouldBe "test"
    }

    @Test
    fun `should return HttpError given client error response`() = runTest {
        val engine = MockEngine { _ ->
            respondError(
                status = HttpStatusCode.BadRequest,
                content = loadJson("error_response.json"),
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = createClient(engine)

        val result: ApiResponse<JsonObject> = client.safeRequest {
            url { path("test") }
            method = HttpMethod.Get
        }

        result.shouldBeInstanceOf<ApiResponse.Error.HttpError<JsonObject>>()
        result.code shouldBe 400
    }

    @Test
    fun `should return SerializationError given malformed response body`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = """not json""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = createClient(engine)

        val result: ApiResponse<JsonObject> = client.safeRequest {
            url { path("test") }
            method = HttpMethod.Get
        }

        result.shouldBeInstanceOf<ApiResponse.Error.SerializationError>()
    }

    @Test
    fun `should return NetworkFailure Unknown given unexpected exception`() = runTest {
        val boom = RuntimeException("network failure")
        val engine = MockEngine { _ ->
            throw boom
        }
        val client = createClient(engine)

        val result: ApiResponse<JsonObject> = client.safeRequest {
            url { path("test") }
            method = HttpMethod.Get
        }

        val failure = result.shouldBeInstanceOf<ApiResponse.Error.NetworkFailure>()
        failure.kind shouldBe ApiResponse.Error.NetworkFailure.Kind.Unknown
        failure.cause shouldBe boom
    }

    @Test
    fun `should return NetworkFailure Timeout given HttpRequestTimeoutException`() = runTest {
        val engine = MockEngine { _ ->
            throw HttpRequestTimeoutException(url = "http://test/test", timeoutMillis = 1_000L)
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json() }
            install(HttpTimeout) {
                requestTimeoutMillis = 1_000L
            }
            expectSuccess = true
        }

        val result: ApiResponse<JsonObject> = client.safeRequest {
            url { path("test") }
            method = HttpMethod.Get
        }

        val failure = result.shouldBeInstanceOf<ApiResponse.Error.NetworkFailure>()
        failure.kind shouldBe ApiResponse.Error.NetworkFailure.Kind.Timeout
    }

    @Test
    fun `should propagate CancellationException instead of swallowing it`() = runTest {
        val engine = MockEngine { _ ->
            throw CancellationException("cancelled by test")
        }
        val client = createClient(engine)

        shouldThrow<CancellationException> {
            client.safeRequest<JsonObject> {
                url { path("test") }
                method = HttpMethod.Get
            }
        }
    }

    @Test
    fun `should return Unauthenticated given AuthenticationException is thrown`() = runTest {
        val engine = MockEngine { _ ->
            throw AuthenticationException(message = "User is not authenticated")
        }
        val client = createClient(engine)

        val result: ApiResponse<JsonObject> = client.safeRequest {
            url { path("test") }
            method = HttpMethod.Get
        }

        result.shouldBeInstanceOf<ApiResponse.Unauthenticated>()
    }

    @Test
    fun `should return HttpError given unauthorized response`() = runTest {
        val engine = MockEngine { _ ->
            respondError(
                status = HttpStatusCode.Unauthorized,
                content = """{"error":"unauthorized"}""",
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = createClient(engine)

        val result: ApiResponse<JsonObject> = client.safeRequest {
            url { path("test") }
            method = HttpMethod.Get
        }

        result.shouldBeInstanceOf<ApiResponse.Error.HttpError<JsonObject>>()
        result.code shouldBe 401
    }

    @Test
    fun `should set RequiresAuth attribute given authSafeRequest is used`() = runTest {
        var capturedRequiresAuth: Boolean? = null
        val engine = MockEngine { request ->
            capturedRequiresAuth = request.attributes.getOrNull(RequiresAuth)
            respond(
                content = loadJson("success_response.json"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = createClient(engine)
        client.attributes.put(IsAuthenticated) { true }

        client.authSafeRequest<JsonObject> {
            url { path("test") }
            method = HttpMethod.Get
        }

        capturedRequiresAuth shouldBe true
    }

    @Test
    fun `should return Unauthenticated given IsAuthenticated attribute is missing`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = loadJson("success_response.json"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = createClient(engine)

        val result: ApiResponse<JsonObject> = client.authSafeRequest {
            url { path("test") }
            method = HttpMethod.Get
        }

        result.shouldBeInstanceOf<ApiResponse.Unauthenticated>()
        engine.requestHistory.size shouldBe 0
    }

    @Test
    fun `should return Unauthenticated given user is not logged in`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = loadJson("success_response.json"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = createClient(engine)
        client.attributes.put(IsAuthenticated) { false }

        val result: ApiResponse<JsonObject> = client.authSafeRequest {
            url { path("test") }
            method = HttpMethod.Get
        }

        result.shouldBeInstanceOf<ApiResponse.Unauthenticated>()
        engine.requestHistory.size shouldBe 0
    }

    @Test
    fun `should proceed with request given user is authenticated`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = loadJson("success_response.json"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = createClient(engine)
        client.attributes.put(IsAuthenticated) { true }

        val result: ApiResponse<JsonObject> = client.authSafeRequest {
            url { path("test") }
            method = HttpMethod.Get
        }

        result.shouldBeInstanceOf<ApiResponse.Success<JsonObject>>()
        engine.requestHistory.size shouldBe 1
    }
}
