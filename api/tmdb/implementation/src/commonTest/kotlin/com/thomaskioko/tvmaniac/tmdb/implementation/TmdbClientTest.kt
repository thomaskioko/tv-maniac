package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.core.connectivity.testing.FakeInternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TmdbClientTest {

    private val accessToken = "tmdb-secret-token"
    private val logger = FakeLogger()

    private fun client(engine: MockEngine): HttpClient =
        tmdbHttpClient(
            tmdbAccessToken = accessToken,
            json = Json { ignoreUnknownKeys = true },
            httpClientEngine = engine,
            kermitLogger = logger,
            internetConnectionChecker = FakeInternetConnectionChecker(),
        )

    @Test
    fun `should send the access token as a bearer header and keep it out of the url given a request`() = runTest {
        var sentUrl = ""
        var sentAuthorization: String? = null
        val engine = MockEngine { request ->
            sentUrl = request.url.toString()
            sentAuthorization = request.headers[HttpHeaders.Authorization]
            respond(content = "{}", status = HttpStatusCode.OK)
        }

        client(engine).get("/3/tv/1?append_to_response=credits")

        sentAuthorization shouldBe "Bearer $accessToken"
        sentUrl shouldContain "api.themoviedb.org/3/tv/1"
        sentUrl shouldNotContain "api_key"
        sentUrl shouldNotContain accessToken
    }

    @Test
    fun `should report no token or api key given a socket timeout`() = runTest {
        val engine = MockEngine { request ->
            throw SocketTimeoutException("Socket timeout has expired [url=${request.url}, socket_timeout=60000] ms")
        }

        assertFailsWith<SocketTimeoutException> { client(engine).get("/3/tv/1?append_to_response=credits") }

        val reported = reportedText()
        reported shouldContain "SocketTimeoutException"
        reported shouldNotContain accessToken
        reported shouldNotContain "api_key"
    }

    @Test
    fun `should report no token or api key given a request timeout`() = runTest {
        val engine = MockEngine { request ->
            throw HttpRequestTimeoutException(request.url.toString(), 60_000)
        }

        assertFailsWith<HttpRequestTimeoutException> { client(engine).get("/3/tv/1?append_to_response=credits") }

        val reported = reportedText()
        reported shouldContain "HttpRequestTimeoutException"
        reported shouldNotContain accessToken
        reported shouldNotContain "api_key"
    }

    private fun reportedText(): String {
        val throwables = logger.recordedErrors.map { it.throwable } +
            logger.recordedWarnings.map { it.throwable } +
            logger.recordedExceptions.map { it.throwable }
        val causeChains = throwables.flatMap { throwable ->
            generateSequence(throwable) { it.cause }.map { "${it::class.simpleName} ${it.message}" }.toList()
        }
        val messages = logger.recordedErrors.map { "${it.message} ${it.keys}" } +
            logger.recordedWarnings.map { "${it.message} ${it.keys}" } +
            logger.recordedExceptions.map { "${it.keys}" } +
            logger.breadcrumbs
        return (messages + causeChains).joinToString("\n")
    }
}
