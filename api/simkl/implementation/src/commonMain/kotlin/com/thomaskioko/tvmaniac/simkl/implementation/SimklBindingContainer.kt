package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.appconfig.DebugConfig
import com.thomaskioko.tvmaniac.appconfig.SimklConfig
import com.thomaskioko.tvmaniac.core.base.SimklApi
import com.thomaskioko.tvmaniac.core.base.SimklDataApi
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.oauth.api.AuthStateHolder
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
public object SimklBindingContainer {

    private val json: Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    @Provides
    @SingleIn(AppScope::class)
    @SimklApi
    public fun provideHttpClient(
        @SimklApi httpClientEngine: HttpClientEngine,
        debugConfig: DebugConfig,
        simklConfig: SimklConfig,
        logger: Logger,
        authStateHolder: AuthStateHolder,
    ): HttpClient = simklHttpClient(
        isDebug = debugConfig.isDebug,
        simklClientId = simklConfig.clientId,
        json = json,
        httpClientEngine = httpClientEngine,
        kermitLogger = logger,
        authStateHolder = authStateHolder,
    )

    @Provides
    @SingleIn(AppScope::class)
    @SimklDataApi
    public fun provideDataHttpClient(
        @SimklApi httpClientEngine: HttpClientEngine,
        debugConfig: DebugConfig,
        logger: Logger,
    ): HttpClient = simklDataHttpClient(
        isDebug = debugConfig.isDebug,
        json = json,
        httpClientEngine = httpClientEngine,
        kermitLogger = logger,
    )
}
