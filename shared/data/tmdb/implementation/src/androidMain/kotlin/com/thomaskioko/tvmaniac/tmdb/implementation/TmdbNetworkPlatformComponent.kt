package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.base.model.AppConfig
import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import com.thomaskioko.tvmaniac.tmdb.implementation.inject.TmdbHttpClient
import com.thomaskioko.tvmaniac.tmdb.implementation.inject.TmdbHttpClientEngine
import com.thomaskioko.tvmaniac.tmdb.implementation.inject.TmdbJson
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides

@OptIn(ExperimentalSerializationApi::class)
interface TmdbNetworkPlatformComponent {

    @OptIn(ExperimentalSerializationApi::class)
    @ApplicationScope
    @Provides
    fun provideTmdbJson(): TmdbJson = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
        explicitNulls = false
    }

    @ApplicationScope
    @Provides
    fun provideTmdbHttpClientEngine(): TmdbHttpClientEngine = OkHttp.create()

    @ApplicationScope
    @Provides
    fun provideTmdbHttpClient(
        appConfig: AppConfig,
        json: TmdbJson,
        httpClientEngine: TmdbHttpClientEngine
    ): TmdbHttpClient = tmdbHttpClient(
        isDebug = appConfig.isDebug,
        tmdbApiKey = appConfig.tmdbApiKey,
        json = json,
        httpClientEngine = httpClientEngine
    )

}