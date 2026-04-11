package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.appconfig.ApplicationInfo
import com.thomaskioko.tvmaniac.core.base.TmdbApi
import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.tmdb.api.TmdbConfig
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
        applicationInfo: ApplicationInfo,
        tmdbConfig: TmdbConfig,
        logger: Logger,
        internetConnectionChecker: InternetConnectionChecker,
    ): HttpClient = tmdbHttpClient(
        tmdbApiKey = tmdbConfig.apiKey,
        json = json,
        httpClientEngine = httpClientEngine,
        kermitLogger = logger,
        isDebug = applicationInfo.debugBuild,
        internetConnectionChecker = internetConnectionChecker,
    )
}
