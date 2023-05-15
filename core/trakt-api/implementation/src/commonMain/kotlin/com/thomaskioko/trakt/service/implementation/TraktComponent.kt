package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.Configs
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import io.ktor.client.HttpClient
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides

typealias TraktHttpClient = HttpClient
typealias TraktJson = Json

interface TraktComponent {

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
    fun provideHttpClient(
        configs: Configs,
        json: TraktJson,
        httpClientEngine: TraktHttpClientEngine,
        logger: KermitLogger,
    ): TraktHttpClient = traktHttpClient(
        isDebug = configs.isDebug,
        traktClientId = configs.traktClientId,
        json = json,
        httpClientEngine = httpClientEngine,
        kermitLogger = logger,
    )

    @ApplicationScope
    @Provides
    fun provideTraktService(bind: TraktServiceImpl): TraktService = bind
}
