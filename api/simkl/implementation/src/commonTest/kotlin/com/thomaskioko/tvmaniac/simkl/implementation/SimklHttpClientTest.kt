package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.connectivity.testing.FakeInternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.oauth.testing.FakeAuthStateHolder
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test

internal class SimklHttpClientTest {

    @Test
    fun `should send the new access token given a different account signed in`() = runTest {
        val authStateHolder = FakeAuthStateHolder()
        val authorizationHeaders = mutableListOf<String?>()
        val engine = MockEngine { request ->
            authorizationHeaders += request.headers[HttpHeaders.Authorization]
            respond(
                content = "{}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = simklHttpClient(
            simklClientId = "client-id",
            json = Json,
            httpClientEngine = engine,
            kermitLogger = FakeLogger(),
            authStateHolder = authStateHolder,
            internetConnectionChecker = FakeInternetConnectionChecker(),
        )

        authStateHolder.setAuthState(SyncProviderSource.SIMKL, AuthState(accessToken = "token-a", refreshToken = "refresh-a"))
        client.get("https://api.simkl.com/users/settings")
        authStateHolder.setAuthState(SyncProviderSource.SIMKL, AuthState(accessToken = "token-b", refreshToken = "refresh-b"))
        client.get("https://api.simkl.com/users/settings")

        authorizationHeaders shouldBe listOf("Bearer token-a", "Bearer token-b")
    }

    @Test
    fun `should send the request again with the new access token given a different account signed in during the request`() = runTest {
        val authStateHolder = FakeAuthStateHolder()
        val authorizationHeaders = mutableListOf<String?>()
        val engine = MockEngine { request ->
            val authorization = request.headers[HttpHeaders.Authorization]
            authorizationHeaders += authorization
            if (authorization == "Bearer token-a") {
                authStateHolder.setAuthState(SyncProviderSource.SIMKL, AuthState(accessToken = "token-b", refreshToken = "refresh-b"))
                respond(content = "{}", status = HttpStatusCode.Unauthorized)
            } else {
                respond(
                    content = "{}",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }
        }
        val client = simklHttpClient(
            simklClientId = "client-id",
            json = Json,
            httpClientEngine = engine,
            kermitLogger = FakeLogger(),
            authStateHolder = authStateHolder,
            internetConnectionChecker = FakeInternetConnectionChecker(),
        )

        authStateHolder.setAuthState(SyncProviderSource.SIMKL, AuthState(accessToken = "token-a", refreshToken = "refresh-a"))
        val response = client.get("https://api.simkl.com/users/settings")

        response.status shouldBe HttpStatusCode.OK
        authorizationHeaders shouldBe listOf("Bearer token-a", "Bearer token-b")
        authStateHolder.authError(SyncProviderSource.SIMKL).first() shouldBe null
    }

    @Test
    fun `should report the token as expired given the signed in token is rejected`() = runTest {
        val authStateHolder = FakeAuthStateHolder()
        val engine = MockEngine { respond(content = "{}", status = HttpStatusCode.Unauthorized) }
        val client = simklHttpClient(
            simklClientId = "client-id",
            json = Json,
            httpClientEngine = engine,
            kermitLogger = FakeLogger(),
            authStateHolder = authStateHolder,
            internetConnectionChecker = FakeInternetConnectionChecker(),
        )

        authStateHolder.setAuthState(SyncProviderSource.SIMKL, AuthState(accessToken = "token-a", refreshToken = "refresh-a"))
        client.get("https://api.simkl.com/users/settings")

        authStateHolder.authError(SyncProviderSource.SIMKL).first() shouldBe AuthError.TokenExpired
    }
}
