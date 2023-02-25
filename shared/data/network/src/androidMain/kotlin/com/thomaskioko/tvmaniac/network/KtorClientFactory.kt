package com.thomaskioko.tvmaniac.network

import io.ktor.client.HttpClient

@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
actual class KtorClientFactory {
    actual fun httpClient(httpClient: HttpClient): HttpClient = httpClient
}
