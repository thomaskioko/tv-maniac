package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.core.connectivity.testing.FakeInternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.safeRequest
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class TmdbClientTest {

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

        val thrown = assertFailsWith<Throwable> { client(engine).fetchShow() }

        val received = causeChainText(thrown)
        received shouldContain "SocketTimeoutException"
        received shouldNotContain accessToken
        received shouldNotContain "api_key"
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

        val thrown = assertFailsWith<Throwable> { client(engine).fetchShow() }

        val received = causeChainText(thrown)
        received shouldContain "HttpRequestTimeoutException"
        received shouldNotContain accessToken
        received shouldNotContain "api_key"
        val reported = reportedText()
        reported shouldContain "HttpRequestTimeoutException"
        reported shouldNotContain accessToken
        reported shouldNotContain "api_key"
    }

    private suspend fun HttpClient.fetchShow(): String =
        safeRequest<String> { url("/3/tv/1?append_to_response=credits") }.getOrThrow()

    private fun causeChainText(throwable: Throwable): String =
        generateSequence(throwable) { it.cause }.joinToString("\n") { "${it::class.simpleName} ${it.message}" }

    private fun reportedText(): String {
        val throwables = logger.recordedErrors.map { it.throwable } +
            logger.recordedWarnings.map { it.throwable } +
            logger.recordedExceptions.map { it.throwable }
        val causeChains = throwables.map { causeChainText(it) }
        val messages = logger.recordedErrors.map { "${it.message} ${it.keys}" } +
            logger.recordedWarnings.map { "${it.message} ${it.keys}" } +
            logger.recordedExceptions.map { "${it.keys}" } +
            logger.breadcrumbs
        return (messages + causeChains).joinToString("\n")
    }
}
