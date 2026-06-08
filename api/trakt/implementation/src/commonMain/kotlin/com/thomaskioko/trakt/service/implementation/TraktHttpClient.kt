package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.InternetConnectionPlugin
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.IsAuthenticated
import com.thomaskioko.tvmaniac.core.networkutil.api.model.HttpExceptions
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
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
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.thomaskioko.tvmaniac.core.logger.Logger as KermitLogger

internal const val TIMEOUT_DURATION: Long = 60_000

private const val OAUTH_PATH = "oauth/"

private const val TRAKT_ACCOUNT_LIMIT_STATUS: Int = 420

internal fun traktHttpClient(
    isDebug: Boolean = false,
    traktClientId: String,
    json: Json,
    httpClientEngine: HttpClientEngine,
    kermitLogger: KermitLogger,
    traktAuthRepository: TraktAuthRepository,
    internetConnectionChecker: InternetConnectionChecker,
): HttpClient {
    val client = HttpClient(httpClientEngine) {
        install(ContentNegotiation) { json(json = json) }

        install(InternetConnectionPlugin) {
            this.internetConnectionChecker = internetConnectionChecker
        }

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
                host = "api.trakt.tv"
            }

            headers {
                append(HttpHeaders.ContentType, "application/json")
                append("trakt-api-version", "2")
                append("trakt-api-key", traktClientId)
            }
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val state = traktAuthRepository.getAuthState()
                        ?.takeIf { it.isAuthorized && it.accessToken.isNotBlank() }
                        ?: return@loadTokens null

                    BearerTokens(state.accessToken, state.refreshToken)
                }

                refreshTokens {
                    val currentState = traktAuthRepository.getAuthState()
                        ?: return@refreshTokens null

                    if (oldTokens?.refreshToken != null && oldTokens?.refreshToken != currentState.refreshToken) {
                        return@refreshTokens BearerTokens(currentState.accessToken, currentState.refreshToken)
                    }

                    val result = traktAuthRepository.refreshTokens()
                    if (result is TokenRefreshResult.Success) {
                        BearerTokens(result.authState.accessToken, result.authState.refreshToken)
                    } else {
                        null
                    }
                }

                sendWithoutRequest { request ->
                    !request.url.encodedPath.startsWith("/$OAUTH_PATH")
                }
            }
        }

        HttpResponseValidator {
            validateResponse { response ->
                if (!response.status.isSuccess() && response.status != HttpStatusCode.Unauthorized) {
                    val failureReason = when {
                        response.status == HttpStatusCode.Forbidden -> "${response.status.value} Missing API key."
                        response.status == HttpStatusCode.NotFound -> "Invalid Request"
                        response.status == HttpStatusCode.TooManyRequests ->
                            "Rate limited. Please try again in a moment."
                        response.status.value == TRAKT_ACCOUNT_LIMIT_STATUS ->
                            "Trakt account limit reached. Upgrade your Trakt account to sync more shows."
                        response.status == HttpStatusCode.UpgradeRequired -> "Upgrade to VIP"
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

        install(TraktAuthGuard) {
            isAuthenticated = { traktAuthRepository.isLoggedIn() }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT_DURATION
            connectTimeoutMillis = TIMEOUT_DURATION
            socketTimeoutMillis = TIMEOUT_DURATION
        }

        install(Logging) {
            level = if (isDebug) LogLevel.INFO else LogLevel.NONE
            logger = if (isDebug) {
                object : Logger {
                    override fun log(message: String) {
                        kermitLogger.info("TraktHttp", message)
                    }
                }
            } else {
                Logger.EMPTY
            }
        }
    }
    client.attributes.put(IsAuthenticated) { traktAuthRepository.isLoggedIn() }
    return client
}
