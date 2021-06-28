package com.thomaskioko.tvmaniac.datasource.network

import io.ktor.client.HttpClient

expect class KtorClientFactory() {
    fun build(): HttpClient
}