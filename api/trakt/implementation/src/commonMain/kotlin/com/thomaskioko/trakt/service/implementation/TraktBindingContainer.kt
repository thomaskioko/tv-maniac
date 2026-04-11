package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.core.base.TraktApi
import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
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
public object TraktBindingContainer {

    private val json: Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    @Provides
    @SingleIn(AppScope::class)
    @TraktApi
    public fun provideHttpClient(
        @TraktApi httpClientEngine: HttpClientEngine,
        logger: Logger,
        traktAuthRepository: TraktAuthRepository,
        internetConnectionChecker: InternetConnectionChecker,
    ): HttpClient = traktHttpClient(
        isDebug = BuildConfig.IS_DEBUG,
        traktClientId = BuildConfig.TRAKT_CLIENT_ID,
        json = json,
        httpClientEngine = httpClientEngine,
        kermitLogger = logger,
        traktAuthRepository = traktAuthRepository,
        internetConnectionChecker = internetConnectionChecker,
    )
}
