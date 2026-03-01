package com.thomaskioko.tvmaniac.tmdb.implementation

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.EMPTY
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.thomaskioko.tvmaniac.core.logger.Logger as KermitLogger

internal const val TIMEOUT_DURATION: Long = 60_000

internal fun tmdbHttpClient(
    isDebug: Boolean = false,
    tmdbApiKey: String,
    json: Json,
    httpClientEngine: HttpClientEngine,
    kermitLogger: KermitLogger,
) =
    HttpClient(httpClientEngine) {
        install(ContentNegotiation) { json(json = json) }

        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.themoviedb.org"

                parameters.append("api_key", tmdbApiKey)

                headers {
                    append(HttpHeaders.Accept, "application/vnd.api+json")
                    append(HttpHeaders.ContentType, "application/vnd.api+json")
                }
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = TIMEOUT_DURATION
            connectTimeoutMillis = TIMEOUT_DURATION
            socketTimeoutMillis = TIMEOUT_DURATION
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

        install(Logging) {
            level = if (isDebug) LogLevel.BODY else LogLevel.NONE
            logger = if (isDebug) {
                object : Logger {
                    override fun log(message: String) {
                        kermitLogger.info("TmbdHttp", message)
                    }
                }
            } else {
                Logger.EMPTY
            }
        }
    }
