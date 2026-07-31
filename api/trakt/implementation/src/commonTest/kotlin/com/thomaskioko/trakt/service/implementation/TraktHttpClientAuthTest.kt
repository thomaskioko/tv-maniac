package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.core.connectivity.testing.FakeInternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test

class TraktHttpClientAuthTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun client(
        repository: FakeTraktAuthRepository,
        statuses: MutableList<HttpStatusCode>,
        authorizationHeaders: MutableList<String?> = mutableListOf(),
    ): HttpClient {
        val engine = MockEngine { request ->
            authorizationHeaders += request.headers[HttpHeaders.Authorization]
            respond(
                content = "{}",
                status = statuses.removeFirst(),
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        return traktHttpClient(
            traktClientId = "test-client-id",
            json = json,
            httpClientEngine = engine,
            kermitLogger = FakeLogger(),
            oAuthRepository = repository,
            internetConnectionChecker = FakeInternetConnectionChecker(),
        )
    }

    private fun authState(accessToken: String, refreshToken: String) = AuthState(
        accessToken = accessToken,
        refreshToken = refreshToken,
        isAuthorized = true,
    )

    @Test
    fun `should refresh tokens and retry given the first request is unauthorized`() = runTest {
        val repository = FakeTraktAuthRepository()
        repository.setAuthState(authState("stale-access", "stale-refresh"))
        repository.setRefreshOutcome(
            TokenRefreshResult.Success(authState("fresh-access", "fresh-refresh")),
        )
        val sentAuthorization = mutableListOf<String?>()
        val client = client(
            repository = repository,
            statuses = mutableListOf(HttpStatusCode.Unauthorized, HttpStatusCode.OK),
            authorizationHeaders = sentAuthorization,
        )

        val response = client.get("https://api.trakt.tv/users/me")

        response.status shouldBe HttpStatusCode.OK
        repository.refreshTokenCallCount() shouldBe 1
        sentAuthorization.first() shouldBe "Bearer stale-access"
        sentAuthorization.last() shouldBe "Bearer fresh-access"
    }

    @Test
    fun `should reuse the stored tokens given another caller already refreshed them`() = runTest {
        val repository = FakeTraktAuthRepository()
        repository.setAuthState(authState("stale-access", "stale-refresh"))
        repository.setRefreshOutcome(
            TokenRefreshResult.Success(authState("unused-access", "unused-refresh")),
        )
        val sentAuthorization = mutableListOf<String?>()
        val statuses = mutableListOf(HttpStatusCode.Unauthorized, HttpStatusCode.OK)
        val engine = MockEngine { request ->
            sentAuthorization += request.headers[HttpHeaders.Authorization]
            val status = statuses.removeFirst()
            if (status == HttpStatusCode.Unauthorized) {
                repository.setAuthState(authState("other-caller-access", "other-caller-refresh"))
            }
            respond(
                content = "{}",
                status = status,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = traktHttpClient(
            traktClientId = "test-client-id",
            json = json,
            httpClientEngine = engine,
            kermitLogger = FakeLogger(),
            oAuthRepository = repository,
            internetConnectionChecker = FakeInternetConnectionChecker(),
        )

        val response = client.get("https://api.trakt.tv/users/me")

        response.status shouldBe HttpStatusCode.OK
        repository.refreshTokenCallCount() shouldBe 0
        sentAuthorization.first() shouldBe "Bearer stale-access"
        sentAuthorization.last() shouldBe "Bearer other-caller-access"
    }

    @Test
    fun `should not retry given the refresh fails`() = runTest {
        val repository = FakeTraktAuthRepository()
        repository.setAuthState(authState("stale-access", "stale-refresh"))
        repository.setRefreshOutcome(TokenRefreshResult.NotLoggedIn)
        val client = client(
            repository = repository,
            statuses = mutableListOf(HttpStatusCode.Unauthorized),
        )

        val response = client.get("https://api.trakt.tv/users/me")

        response.status shouldBe HttpStatusCode.Unauthorized
        repository.refreshTokenCallCount() shouldBe 1
    }
}
