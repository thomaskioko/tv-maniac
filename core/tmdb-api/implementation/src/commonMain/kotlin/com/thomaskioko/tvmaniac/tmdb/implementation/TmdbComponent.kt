package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.tmdb.api.TmdbSeasonDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
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

expect interface TmdbPlatformComponent

interface TmdbComponent : TmdbPlatformComponent {

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
    fun provideTmdbShowsNetworkDataSource(
        bind: DefaultTmdbShowsNetworkDataSource,
    ): TmdbShowsNetworkDataSource = bind

    @Provides
    fun provideTmdbShowDetailsNetworkDataSource(
        bind: DefaultTmdbShowDetailsNetworkDataSource,
    ): TmdbShowDetailsNetworkDataSource = bind

    @Provides
    fun provideTmdbSeasonDetailsNetworkDataSource(
        bind: DefaultTmdbSeasonDetailsNetworkDataSource,
    ): TmdbSeasonDetailsNetworkDataSource = bind
}
