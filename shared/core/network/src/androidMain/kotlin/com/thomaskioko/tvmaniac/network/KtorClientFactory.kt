package com.thomaskioko.tvmaniac.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.DefaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger.Companion as KermitLogger

private const val TIME_OUT = 60_000

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
actual class KtorClientFactory {
    actual fun build(httpUrl: String): HttpClient {
        return HttpClient(Android) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                        explicitNulls = false
                    }
                )

                engine {
                    connectTimeout = TIME_OUT
                    socketTimeout = TIME_OUT
                }
            }

            install(Logging) {
                level = LogLevel.INFO
                logger = object : Logger {
                    override fun log(message: String) {
                        KermitLogger.d { message }
                    }
                }
            }

            install(ResponseObserver) {
                onResponse { response ->
                    KermitLogger.d { "HTTP status: ${response.status.value}" }
                }
            }

            install(DefaultRequest) {
                val endpointUrlBuilder = URLBuilder(httpUrl)
                url {
                    host = endpointUrlBuilder.host
                    protocol = endpointUrlBuilder.protocol
                }
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }
}
