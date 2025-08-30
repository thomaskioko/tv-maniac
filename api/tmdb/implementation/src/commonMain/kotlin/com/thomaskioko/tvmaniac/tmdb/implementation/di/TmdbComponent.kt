package com.thomaskioko.tvmaniac.tmdb.implementation.di

import com.thomaskioko.tvmaniac.core.base.model.Configs
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.tmdb.implementation.tmdbHttpClient
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json

typealias TmdbHttpClient = HttpClient

typealias TmdbHttpClientEngine = HttpClientEngine

typealias TmdbJson = Json

@ContributesTo(AppScope::class)
interface TmdbComponent {

    @Provides
    @SingleIn(AppScope::class)
    fun provideTmdbJson(): TmdbJson = Json {
        isLenient = true
        ignoreUnknownKeys = true
        useAlternativeNames = false
        explicitNulls = false
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideTmdbHttpClient(
        configs: Configs,
        json: TmdbJson,
        httpClientEngine: TmdbHttpClientEngine,
        logger: Logger,
    ): TmdbHttpClient =
        tmdbHttpClient(
            tmdbApiKey = configs.tmdbApiKey,
            json = json,
            httpClientEngine = httpClientEngine,
            kermitLogger = logger,
            isDebug = configs.isDebug,
        )
}
