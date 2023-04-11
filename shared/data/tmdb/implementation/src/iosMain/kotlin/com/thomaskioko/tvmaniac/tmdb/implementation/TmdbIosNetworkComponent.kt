package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.base.model.AppConfig
import com.thomaskioko.tvmaniac.base.scope.Singleton
import com.thomaskioko.tvmaniac.tmdb.implementation.inject.TmdbNetworkComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

@Singleton
@Inject
class TmdbIosNetworkComponent : TmdbNetworkComponent {


    override fun json(): Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        useAlternativeNames = false
        explicitNulls = false
    }


    override fun httpClientEngine(): HttpClientEngine = Darwin.create()


    override fun httpClient(
        appConfig: AppConfig,
        json: Json,
        httpClientEngine: HttpClientEngine
    ): HttpClient = tmdbHttpClient(
        isDebug = false,
        json = json,
        httpClientEngine = httpClientEngine,
        tmdbApiKey = appConfig.tmdbApiKey
    )
}