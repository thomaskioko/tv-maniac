package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.buildconfig.api.BuildConfig
import com.thomaskioko.tvmaniac.core.logger.Logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

public typealias TmdbHttpClient = HttpClient

public typealias TmdbHttpClientEngine = HttpClientEngine

public typealias TmdbJson = Json

@ContributesTo(AppScope::class)
public interface TmdbComponent {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideTmdbJson(): TmdbJson = Json {
        isLenient = true
        ignoreUnknownKeys = true
        useAlternativeNames = false
        explicitNulls = false
    }

    @Provides
    @SingleIn(AppScope::class)
    public fun provideTmdbHttpClient(
        json: TmdbJson,
        httpClientEngine: TmdbHttpClientEngine,
        logger: Logger,
    ): TmdbHttpClient = tmdbHttpClient(
        tmdbApiKey = BuildConfig.TMDB_API_KEY,
        json = json,
        httpClientEngine = httpClientEngine,
        kermitLogger = logger,
        isDebug = BuildConfig.IS_DEBUG,
    )
}
