package com.thomaskioko.tvmaniac.datasource.network

import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import io.ktor.http.hostWithPort
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.date.GMTDate
import kotlinx.serialization.json.Json
import kotlin.test.assertEquals

abstract class TvShowsServiceMockEngine {

    private var lastRequest: HttpRequestData? = null
    private var apiUrl: String = ""
    private lateinit var mockResponse: HttpResponseData

    fun enqueueMockResponse(
        endpointSegment: String,
        responseBody: String,
        httpStatusCode: Int = 200
    ) {
        apiUrl = endpointSegment

        runBlockingTest {
            mockResponse = HttpResponseData(
                statusCode = HttpStatusCode.fromValue(httpStatusCode),
                headers = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString())),
                body = responseBody,
                version = HttpProtocolVersion.HTTP_1_1,
                requestTime = GMTDate(),
                callContext = coroutineContext
            )
        }
    }

    protected open fun httpClient() = HttpClient(MockEngine) {
        install(ContentNegotiation) {
            json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                }
            )
        }

        defaultRequest {
            if (url.host == "localhost") {
                url.protocol = URLProtocol.HTTPS
                url.host = "api.themoviedb.org/3/"
            }
        }

        engine {
            addHandler { request ->
                lastRequest = request

                when (request.url.fullUrl) {
                    apiUrl -> {
                        respond(
                            content = mockResponse.body.toString(),
                            headers = mockResponse.headers,
                            status = HttpStatusCode.fromValue(mockResponse.statusCode.value),
                        )
                    }
                    else -> error("Unhandled ${request.url.fullUrl}")
                }
            }
        }
    }

    fun verifyRequestContainsHeader(key: String, expectedValue: String) {
        val value = lastRequest!!.headers[key]
        expectedValue shouldBe value
    }

    fun verifyRequestBody(addTaskRequest: String) {
        val body = (lastRequest!!.body as TextContent).text

        assertEquals(addTaskRequest, body)
    }

    fun verifyGetRequest() {
        lastRequest!!.method.value shouldBe HttpMethod.Get.value
    }

    fun verifyPostRequest() {
        lastRequest!!.method.value shouldBe HttpMethod.Post.value
    }

    fun verifyPutRequest() {
        lastRequest!!.method.value shouldBe HttpMethod.Put.value
    }

    fun verifyDeleteRequest() {
        lastRequest!!.method.value shouldBe HttpMethod.Delete.value
    }
}

private val Url.hostWithPortIfRequired: String get() = if (port == protocol.defaultPort) host else hostWithPort
private val Url.fullUrl: String get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"
