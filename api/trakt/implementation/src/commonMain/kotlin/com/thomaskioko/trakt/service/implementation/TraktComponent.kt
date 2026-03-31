package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.util.api.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

public typealias TraktHttpClient = HttpClient
public typealias TraktHttpClientEngine = HttpClientEngine
public typealias TraktJson = Json

@ContributesTo(AppScope::class)
public interface TraktComponent {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideJson(): TraktJson = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    @Provides
    @SingleIn(AppScope::class)
    public fun provideHttpClient(
        json: TraktJson,
        httpClientEngine: TraktHttpClientEngine,
        logger: Logger,
        traktAuthRepository: TraktAuthRepository,
        internetConnectionChecker: InternetConnectionChecker,
    ): TraktHttpClient = traktHttpClient(
        isDebug = BuildConfig.IS_DEBUG,
        traktClientId = BuildConfig.TRAKT_CLIENT_ID,
        json = json,
        httpClientEngine = httpClientEngine,
        kermitLogger = logger,
        traktAuthRepository = traktAuthRepository,
        internetConnectionChecker = internetConnectionChecker,
    )
}
