package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.core.base.TmdbApi
import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.util.api.BuildConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json

@BindingContainer
@ContributesTo(AppScope::class)
public object TmdbBindingContainer {

    private val json: Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        useAlternativeNames = false
        explicitNulls = false
    }

    @Provides
    @SingleIn(AppScope::class)
    @TmdbApi
    public fun provideTmdbHttpClient(
        @TmdbApi httpClientEngine: HttpClientEngine,
        logger: Logger,
        internetConnectionChecker: InternetConnectionChecker,
    ): HttpClient = tmdbHttpClient(
        tmdbApiKey = BuildConfig.TMDB_API_KEY,
        json = json,
        httpClientEngine = httpClientEngine,
        kermitLogger = logger,
        isDebug = BuildConfig.IS_DEBUG,
        internetConnectionChecker = internetConnectionChecker,
    )
}
