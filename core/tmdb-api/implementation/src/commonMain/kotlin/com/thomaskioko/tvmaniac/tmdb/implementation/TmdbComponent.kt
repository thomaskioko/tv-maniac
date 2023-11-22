package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.tmdb.api.TmdbNetworkDataSource
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.Configs
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides

typealias TmdbHttpClient = HttpClient
typealias TmdbHttpClientEngine = HttpClientEngine
typealias TmdbJson = Json

interface TmdbComponent {

    @OptIn(ExperimentalSerializationApi::class)
    @ApplicationScope
    @Provides
    fun provideTmdbJson(): TmdbJson = Json {
        isLenient = true
        ignoreUnknownKeys = true
        useAlternativeNames = false
        explicitNulls = false
    }

    @ApplicationScope
    @Provides
    fun provideTmdbHttpClient(
        configs: Configs,
        json: TmdbJson,
        httpClientEngine: TmdbHttpClientEngine,
        logger: KermitLogger,
    ): TmdbHttpClient = tmdbHttpClient(
        tmdbApiKey = configs.tmdbApiKey,
        json = json,
        httpClientEngine = httpClientEngine,
        kermitLogger = logger,
        isDebug = configs.isDebug,
    )

    @Provides
    fun provideTmdbService(bind: TmdbNetworkDataSourceImpl): TmdbNetworkDataSource = bind
}