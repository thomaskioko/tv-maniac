package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.base.model.AppConfig
import com.thomaskioko.tvmaniac.base.model.Configs
import com.thomaskioko.tvmaniac.base.model.TraktOAuthInfo
import io.ktor.client.engine.darwin.Darwin
import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import com.thomaskioko.tvmaniac.trakt.api.TraktService
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
    fun provideTraktOAuthInfo(
        configs: Configs,
    ): TraktOAuthInfo = TraktOAuthInfo(
        clientId = configs.traktClientId,
        clientSecret = configs.traktClientSecret,
        redirectUri = configs.traktRedirectUri,
    )


    @ApplicationScope
    @Provides
    fun provideHttpClient(
        appConfig: AppConfig,
        json: TraktJson,
        httpClientEngine: TraktHttpClientEngine
    ): TraktHttpClient = traktHttpClient(
        isDebug = appConfig.isDebug,
        json = json,
        httpClientEngine = httpClientEngine
    )

    @ApplicationScope
    @Provides
    fun provideTraktService(bind: TraktServiceImpl): TraktService = bind
}