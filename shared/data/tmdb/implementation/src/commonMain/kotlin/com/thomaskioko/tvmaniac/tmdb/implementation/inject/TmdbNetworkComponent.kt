package com.thomaskioko.tvmaniac.tmdb.implementation.inject

import com.thomaskioko.tvmaniac.base.model.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json

interface TmdbNetworkComponent {

    fun json(): Json

    fun httpClientEngine(): HttpClientEngine

    fun httpClient(
        appConfig: AppConfig,
        json: Json,
        httpClientEngine: HttpClientEngine
    ): HttpClient
}