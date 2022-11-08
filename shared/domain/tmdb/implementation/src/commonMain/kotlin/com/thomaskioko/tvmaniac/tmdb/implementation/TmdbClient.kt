package com.thomaskioko.tvmaniac.tmdb.implementation

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun tmdbHttpClient(
    isDebug: Boolean = false,
    tmdbApiKey: String,
    json: Json,
    httpClientEngine: HttpClientEngine,
) = HttpClient(httpClientEngine) {
    install(ContentNegotiation) {
        json(json = json)
    }

    defaultRequest {

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
        requestTimeoutMillis = 60000
        connectTimeoutMillis = 60000
        socketTimeoutMillis = 60000
    }


    install(Logging) {
        level = LogLevel.INFO
        logger = if (isDebug) {
            object : Logger {
                override fun log(message: String) {
                    co.touchlab.kermit.Logger.d { message }
                }
            }

        } else {
            Logger.DEFAULT
        }
    }
}