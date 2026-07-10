package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.IsAuthenticated
import com.thomaskioko.tvmaniac.core.networkutil.api.model.HttpExceptions
import com.thomaskioko.tvmaniac.oauth.api.AuthStateHolder
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.EMPTY
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.thomaskioko.tvmaniac.core.logger.Logger as KermitLogger

internal const val SIMKL_TIMEOUT_DURATION: Long = 60_000

internal fun simklHttpClient(
    isDebug: Boolean = false,
    simklClientId: String,
    json: Json,
    httpClientEngine: HttpClientEngine,
    kermitLogger: KermitLogger,
    authStateHolder: AuthStateHolder,
): HttpClient {
    val client = HttpClient(httpClientEngine) {
        install(ContentNegotiation) { json(json = json) }

        install(HttpRequestRetry) {
            retryIf(5) { _, httpResponse ->
                when {
                    httpResponse.status.value in 500..599 -> true
                    httpResponse.status == HttpStatusCode.TooManyRequests -> true
                    else -> false
                }
            }
            exponentialDelay(
                base = 2.0,
                maxDelayMs = 60_000L,
                randomizationMs = 1000L,
            )
        }

        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.simkl.com"
            }

            headers {
                append(HttpHeaders.ContentType, "application/json")
                append("simkl-api-key", simklClientId)
            }
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val state = authStateHolder.getAuthState(SyncProviderSource.SIMKL)
                        ?.takeIf { it.isAuthorized && it.accessToken.isNotBlank() }
                        ?: return@loadTokens null

                    BearerTokens(state.accessToken, state.refreshToken)
                }

                refreshTokens {
                    val currentState = authStateHolder.getAuthState(SyncProviderSource.SIMKL)
                        ?: return@refreshTokens null

                    if (oldTokens?.accessToken != currentState.accessToken) {
                        return@refreshTokens BearerTokens(currentState.accessToken, currentState.refreshToken)
                    }

                    authStateHolder.setAuthError(SyncProviderSource.SIMKL, AuthError.TokenExpired)
                    null
                }

                sendWithoutRequest { request -> request.url.host == "api.simkl.com" }
            }
        }

        HttpResponseValidator {
            validateResponse { response ->
                if (!response.status.isSuccess() && response.status != HttpStatusCode.Unauthorized) {
                    val failureReason = when {
                        response.status == HttpStatusCode.Forbidden -> "${response.status.value} Missing API key."
                        response.status == HttpStatusCode.NotFound -> "Endpoint not found: ${response.call.request.url}"
                        response.status == HttpStatusCode.TooManyRequests ->
                            "Rate limited. Please try again in a moment."
                        response.status == HttpStatusCode.RequestTimeout -> "Network Timeout"
                        response.status.value in 500..599 -> "${response.status.value} Server Error"
                        else -> "Network error!"
                    }

                    throw HttpExceptions(
                        response = response,
                        failureReason = failureReason,
                        cachedResponseText = response.bodyAsText(),
                    )
                }
            }
        }

        install(SimklAuthGuard) {
            isAuthenticated = { authStateHolder.isLoggedIn(SyncProviderSource.SIMKL) }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = SIMKL_TIMEOUT_DURATION
            connectTimeoutMillis = SIMKL_TIMEOUT_DURATION
            socketTimeoutMillis = SIMKL_TIMEOUT_DURATION
        }

        install(Logging) {
            level = if (isDebug) LogLevel.INFO else LogLevel.NONE
            logger = if (isDebug) {
                object : Logger {
                    override fun log(message: String) {
                        kermitLogger.info("SimklHttp", message)
                    }
                }
            } else {
                Logger.EMPTY
            }
        }
    }
    client.attributes.put(IsAuthenticated) { authStateHolder.isLoggedIn(SyncProviderSource.SIMKL) }
    return client
}
