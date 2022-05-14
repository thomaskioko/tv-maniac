package com.thomaskioko.tvmaniac.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.ios.Ios
import io.ktor.client.features.DefaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import co.touchlab.kermit.Logger.Companion as KermitLogger

private const val TMDB_API_KEY = "https://api.themoviedb.org/3/"

actual class KtorClientFactory {
    actual fun build(): HttpClient {
        return HttpClient(Ios) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(
                    kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                        explicitNulls = false
                    }
                )
            }

            install(Logging) {
                level = LogLevel.INFO
                logger = object : Logger {
                    override fun log(message: String) {
                        KermitLogger.d { message }
                    }
                }
            }

            install(DefaultRequest) {
                val endpointUrlBuilder = URLBuilder(TMDB_API_KEY)
                url {
                    protocol = endpointUrlBuilder.protocol
                    host = endpointUrlBuilder.host
                }
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                parameter("api_key", BuildKonfig.TMDB_API_KEY)
            }
        }
    }
}
