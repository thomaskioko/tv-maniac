package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.core.base.model.Configs
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

typealias TraktHttpClient = HttpClient
typealias TraktHttpClientEngine = HttpClientEngine
typealias TraktJson = Json

@ContributesTo(AppScope::class)
interface TraktComponent {

    @Provides
    fun provideJson(): TraktJson = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    @Provides
    fun provideHttpClient(
        configs: Configs,
        json: TraktJson,
        httpClientEngine: TraktHttpClientEngine,
        logger: Logger,
        traktAuthRepository: TraktAuthRepository,
    ): TraktHttpClient =
        traktHttpClient(
            isDebug = configs.isDebug,
            traktClientId = configs.traktClientId,
            json = json,
            httpClientEngine = httpClientEngine,
            kermitLogger = logger,
            traktAuthRepository = { traktAuthRepository },
        )
}
