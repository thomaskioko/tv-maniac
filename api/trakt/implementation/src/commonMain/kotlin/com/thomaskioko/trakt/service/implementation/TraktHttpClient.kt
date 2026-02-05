package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.model.HttpExceptions
import com.thomaskioko.tvmaniac.traktauth.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import io.ktor.client.HttpClient
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
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import com.thomaskioko.tvmaniac.core.logger.Logger as KermitLogger

internal const val TIMEOUT_DURATION: Long = 60_000

internal fun traktHttpClient(
    isDebug: Boolean = false,
    traktClientId: String,
    json: TraktJson,
    httpClientEngine: TraktHttpClientEngine,
    kermitLogger: KermitLogger,
    traktAuthRepository: () -> TraktAuthRepository,
) =
    HttpClient(httpClientEngine) {
        expectSuccess = true

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

        install(Auth) {
            bearer {
                loadTokens {
                    traktAuthRepository().getAuthState()
                        ?.takeIf { it.isAuthorized && it.accessToken.isNotBlank() }
                        ?.let { state ->
                            BearerTokens(
                                accessToken = state.accessToken,
                                refreshToken = state.refreshToken,
                            )
                        }
                }

                refreshTokens {
                    when (val result = traktAuthRepository().refreshTokens()) {
                        is TokenRefreshResult.Success -> BearerTokens(
                            accessToken = result.authState.accessToken,
                            refreshToken = result.authState.refreshToken,
                        )
                        else -> null
                    }
                }

                sendWithoutRequest { true }
            }
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

        HttpResponseValidator {
            validateResponse { response ->
                if (!response.status.isSuccess()) {
                    val failureReason =
                        when (response.status) {
                            HttpStatusCode.Unauthorized -> "Unauthorized request"
                            HttpStatusCode.Forbidden -> "${response.status.value} Missing API key."
                            HttpStatusCode.NotFound -> "Invalid Request"
                            HttpStatusCode.TooManyRequests -> "Rate limited. Please try again in a moment."
                            HttpStatusCode.UpgradeRequired -> "Upgrade to VIP"
                            HttpStatusCode.RequestTimeout -> "Network Timeout"
                            in HttpStatusCode.InternalServerError..HttpStatusCode.GatewayTimeout ->
                                "${response.status.value} Server Error"
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

        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT_DURATION
            connectTimeoutMillis = TIMEOUT_DURATION
            socketTimeoutMillis = TIMEOUT_DURATION
        }

        install(Logging) {
            level = if (isDebug) LogLevel.BODY else LogLevel.NONE
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
