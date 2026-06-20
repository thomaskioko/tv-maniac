package com.thomaskioko.tvmaniac.simkl.implementation

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
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.thomaskioko.tvmaniac.core.logger.Logger as KermitLogger

internal const val SIMKL_DATA_TIMEOUT_DURATION: Long = 60_000

internal fun simklDataHttpClient(
    isDebug: Boolean = false,
    json: Json,
    httpClientEngine: HttpClientEngine,
    kermitLogger: KermitLogger,
): HttpClient = HttpClient(httpClientEngine) {
    install(ContentNegotiation) { json(json = json) }

    install(HttpRequestRetry) {
        retryIf(3) { _, httpResponse ->
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
            host = "data.simkl.in"
        }
    }

    install(HttpTimeout) {
        requestTimeoutMillis = SIMKL_DATA_TIMEOUT_DURATION
        connectTimeoutMillis = SIMKL_DATA_TIMEOUT_DURATION
        socketTimeoutMillis = SIMKL_DATA_TIMEOUT_DURATION
    }

    install(Logging) {
        level = if (isDebug) LogLevel.INFO else LogLevel.NONE
        logger = if (isDebug) {
            object : Logger {
                override fun log(message: String) {
                    kermitLogger.info("SimklDataHttp", message)
                }
            }
        } else {
            Logger.EMPTY
        }
    }
}
