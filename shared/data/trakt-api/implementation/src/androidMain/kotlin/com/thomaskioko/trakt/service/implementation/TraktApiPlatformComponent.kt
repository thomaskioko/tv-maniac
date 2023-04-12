package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.base.model.AppConfig
import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides

interface TraktApiPlatformComponent {


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
    fun provideTraktHttpClientEngine(
        interceptor: TraktAuthInterceptor
    ): TraktHttpClientEngine = OkHttp.create {
        addInterceptor(interceptor)
    }

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