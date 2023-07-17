package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.tmdb.api.TmdbNetworkDataSource
import com.thomaskioko.tvmaniac.util.model.Configs
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides

actual interface TmdbPlatformComponent {

    @ApplicationScope
    @Provides
    fun provideTmdbJson(): TmdbJson = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    @ApplicationScope
    @Provides
    fun provideTmdbHttpClientEngine(): TmdbHttpClientEngine = OkHttp.create()

    @ApplicationScope
    @Provides
    fun provideTmdbHttpClient(
        configs: Configs,
        json: TmdbJson,
        httpClientEngine: TmdbHttpClientEngine,
    ): TmdbHttpClient = tmdbHttpClient(
        tmdbApiKey = configs.tmdbApiKey,
        json = json,
        httpClientEngine = httpClientEngine,
    )

    @Provides
    fun provideTmdbService(bind: TmdbNetworkDataSourceImpl): TmdbNetworkDataSource = bind
}
