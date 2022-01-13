package com.thomaskioko.tvmaniac.remote

import io.ktor.client.HttpClient

expect class KtorClientFactory() {
    fun build(): HttpClient
}
