package com.thomaskioko.tvmaniac.trakt.implementation

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger.Companion as KermitLogger


object TraktHttpClient {

    @OptIn(ExperimentalSerializationApi::class)
    fun traktHttpClient(
        httpUrl: String,
        interceptor: TraktAuthInterceptor
    ) = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                    explicitNulls = false
                    encodeDefaults = true
                }
            )
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15000L
            connectTimeoutMillis = 15000L
            socketTimeoutMillis = 15000L
        }

        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    KermitLogger.d { message }
                }
            }
        }

        install(DefaultRequest) {
            val endpointUrlBuilder = URLBuilder(httpUrl)
            url {
                host = endpointUrlBuilder.host
                protocol = endpointUrlBuilder.protocol
            }
            contentType(ContentType.Application.Json)
        }

        engine {
            addInterceptor(interceptor)
        }

    }
}