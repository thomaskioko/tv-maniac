package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.util.KermitLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.EMPTY
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json

fun traktHttpClient(
    isDebug: Boolean = false,
    traktClientId: String,
    json: TraktJson,
    httpClientEngine: TraktHttpClientEngine,
    kermitLogger: KermitLogger,
) = HttpClient(httpClientEngine) {
    install(ContentNegotiation) {
        json(json = json)
    }

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = "api.trakt.tv"
        }

        // TODO:: Move this to Auth interceptor
        headers {
            append(HttpHeaders.ContentType, "application/json")
            append("trakt-api-version", "2")
            append("trakt-api-key", traktClientId)
        }
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 60000
        connectTimeoutMillis = 60000
        socketTimeoutMillis = 60000
    }

    install(Logging) {
        level = LogLevel.INFO
        logger = if (isDebug) {
            object : Logger {
                override fun log(message: String) {
                    kermitLogger.debug(message)
                }
            }
        } else {
            Logger.EMPTY
        }
    }
}
