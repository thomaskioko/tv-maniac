package com.thomaskioko.tvmaniac.network

import io.ktor.client.HttpClient

expect class KtorClientFactory() {
    fun httpClient(httpClient: HttpClient): HttpClient
}
