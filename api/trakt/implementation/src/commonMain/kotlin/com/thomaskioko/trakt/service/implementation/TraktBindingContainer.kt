package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.appconfig.DebugConfig
import com.thomaskioko.tvmaniac.core.base.TraktApi
import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.oauth.api.OAuthRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktConfig
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
        debugConfig: DebugConfig,
        traktConfig: TraktConfig,
        logger: Logger,
        oAuthRepository: OAuthRepository,
        internetConnectionChecker: InternetConnectionChecker,
    ): HttpClient = traktHttpClient(
        isDebug = debugConfig.isDebug,
        traktClientId = traktConfig.clientId,
        json = json,
        httpClientEngine = httpClientEngine,
        kermitLogger = logger,
        oAuthRepository = oAuthRepository,
        internetConnectionChecker = internetConnectionChecker,
    )
}
