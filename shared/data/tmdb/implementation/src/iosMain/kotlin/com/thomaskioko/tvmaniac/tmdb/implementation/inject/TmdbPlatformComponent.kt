package com.thomaskioko.tvmaniac.tmdb.implementation.inject

import com.thomaskioko.tvmaniac.base.model.AppConfig
import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import com.thomaskioko.tvmaniac.tmdb.api.ShowImageCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbRepository
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.tmdb.implementation.ShowImageCacheImpl
import com.thomaskioko.tvmaniac.tmdb.implementation.TmdbRepositoryImpl
import com.thomaskioko.tvmaniac.tmdb.implementation.TmdbServiceImpl
import com.thomaskioko.tvmaniac.tmdb.implementation.inject.TmdbHttpClient
import com.thomaskioko.tvmaniac.tmdb.implementation.inject.TmdbHttpClientEngine
import com.thomaskioko.tvmaniac.tmdb.implementation.inject.TmdbJson
import com.thomaskioko.tvmaniac.tmdb.implementation.tmdbHttpClient
import io.ktor.client.engine.darwin.Darwin
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides

actual interface TmdbPlatformComponent {

    @OptIn(ExperimentalSerializationApi::class)
    @ApplicationScope
    @Provides
    fun provideTmdbJson(): TmdbJson  = Json {
        isLenient = true
        ignoreUnknownKeys = true
        useAlternativeNames = false
        explicitNulls = false
    }

    @ApplicationScope
    @Provides
    fun provideTmdbHttpClientEngine(): TmdbHttpClientEngine = Darwin.create()

    @ApplicationScope
    @Provides
    fun provideTmdbHttpClient(
        appConfig: AppConfig,
        json: TmdbJson,
        httpClientEngine: TmdbHttpClientEngine
    ): TmdbHttpClient = tmdbHttpClient(
        isDebug = false,
        json = json,
        httpClientEngine = httpClientEngine,
        tmdbApiKey = appConfig.tmdbApiKey
    )

    @Provides
    fun provideShowImageCache(bind: ShowImageCacheImpl): ShowImageCache = bind

    @Provides
    fun provideTmdbRepository(bind: TmdbRepositoryImpl): TmdbRepository = bind

    @Provides
    fun provideTmdbService(bind: TmdbServiceImpl): TmdbService = bind
}