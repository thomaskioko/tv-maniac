package com.thomaskioko.tvmaniac.datasource.network

import com.thomaskioko.tvmaniac.shared.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.ios.Ios
import io.ktor.client.features.DefaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders

actual class KtorClientFactory {
    actual fun build(): HttpClient {
        return HttpClient(Ios) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(
                    kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                    }
                )
            }

            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.INFO
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                parameter("api_key", BuildKonfig.TMDB_API_KEY)
            }
        }
    }
}
