package com.thomaskioko.trakt.service.implementation

import com.thomaskioko.tvmaniac.base.model.AppConfig
import com.thomaskioko.tvmaniac.base.scope.ApplicationScope
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides

interface TraktApiPlatformComponent {


    @OptIn(ExperimentalSerializationApi::class)
    @ApplicationScope
    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
        explicitNulls = false
    }

    @ApplicationScope
    @Provides
    fun provideHttpClientEngine(
        interceptor: TraktAuthInterceptor
    ): HttpClientEngine = OkHttp.create {
        addInterceptor(interceptor)
    }

    @ApplicationScope
    @Provides
    fun provideHttpClient(
        appConfig: AppConfig,
        json: Json,
        httpClientEngine: HttpClientEngine
    ): HttpClient = traktHttpClient(
        isDebug = appConfig.isDebug,
        json = json,
        httpClientEngine = httpClientEngine
    )

    @ApplicationScope
    @Provides
    fun provideTraktService(bind: TraktServiceImpl): TraktService = bind

}