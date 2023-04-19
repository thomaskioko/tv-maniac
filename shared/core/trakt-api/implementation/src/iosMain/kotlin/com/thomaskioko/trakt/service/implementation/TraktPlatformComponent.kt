package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.util.model.Configs
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import io.ktor.client.engine.darwin.Darwin
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides

actual interface TraktPlatformComponent {

    @OptIn(ExperimentalSerializationApi::class)
    @ApplicationScope
    @Provides
    fun provideJson(): TraktJson = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
        explicitNulls = false
    }

    @ApplicationScope
    @Provides
    fun provideTraktHttpClientEngine(): TraktHttpClientEngine = Darwin.create()

    @ApplicationScope
    @Provides
    fun provideHttpClient(
        configs: Configs,
        json: TraktJson,
        httpClientEngine: TraktHttpClientEngine,
    ): TraktHttpClient = traktHttpClient(
        isDebug = configs.isDebug,
        json = json,
        httpClientEngine = httpClientEngine,
    )

    @ApplicationScope
    @Provides
    fun provideTraktService(bind: TraktServiceImpl): TraktService = bind
}
