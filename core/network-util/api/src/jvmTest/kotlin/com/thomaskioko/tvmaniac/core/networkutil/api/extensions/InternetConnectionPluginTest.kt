package com.thomaskioko.tvmaniac.core.networkutil.api.extensions

import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.NoInternetException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlin.test.Test

class InternetConnectionPluginTest {

    private fun createClient(checker: InternetConnectionChecker? = null): HttpClient {
        val engine = MockEngine { _ ->
            respond(
                content = """{"id":1,"name":"test"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        return HttpClient(engine) {
            install(ContentNegotiation) { json() }
            install(InternetConnectionPlugin) {
                internetConnectionChecker = checker
            }
        }
    }

    @Test
    fun `should proceed with request given device is connected`() = runTest {
        val checker = object : InternetConnectionChecker {
            override fun isConnected(): Boolean = true
        }
        val client = createClient(checker)

        val response = client.get("/test")

        response.status shouldBe HttpStatusCode.OK
    }

    @Test
    fun `should throw NoInternetException given device is disconnected`() = runTest {
        val checker = object : InternetConnectionChecker {
            override fun isConnected(): Boolean = false
        }
        val client = createClient(checker)

        shouldThrow<NoInternetException> {
            client.get("/test")
        }
    }

    @Test
    fun `should proceed with request given no checker is configured`() = runTest {
        val client = createClient(checker = null)

        val response = client.get("/test")

        response.status shouldBe HttpStatusCode.OK
    }

    @Test
    fun `should return OfflineError given NoInternetException is thrown`() = runTest {
        val checker = object : InternetConnectionChecker {
            override fun isConnected(): Boolean = false
        }
        val engine = MockEngine { _ ->
            respond(
                content = """{"id":1,"name":"test"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) { json() }
            install(InternetConnectionPlugin) {
                internetConnectionChecker = checker
            }
        }

        val result: ApiResponse<JsonObject> = client.safeRequest {
            url { path("test") }
            method = HttpMethod.Get
        }

        result.shouldBeInstanceOf<ApiResponse.Error.OfflineError>()
    }
}
