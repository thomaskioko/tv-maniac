package com.thomaskioko.trakt.service.implementation

import org.koin.core.module.Module as KoinModule
import com.thomaskioko.tvmaniac.core.util.model.AppConfig
import com.thomaskioko.tvmaniac.core.util.model.TraktOAuthInfo
import com.thomaskioko.tvmaniac.core.util.scope.Singleton
import com.thomaskioko.tvmaniac.network.KtorClientFactory
import com.thomaskioko.tvmaniac.trakt.service.api.TraktService
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides
import org.koin.dsl.module


actual fun traktServiceModule(): KoinModule = module { }

object TraktServiceModule {


    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
        explicitNulls = false
    }

    @Singleton
    @Provides
    fun provideHttpClientEngine(
        interceptor: TraktAuthInterceptor
    ): HttpClientEngine = OkHttp.create {
        addInterceptor(interceptor)
    }

    @Singleton
    @Provides
    fun provideHttpClient(
        appConfig: AppConfig,
        json: Json,
        httpClientEngine: HttpClientEngine
    ): HttpClient = KtorClientFactory().httpClient(
        traktHttpClient(
            isDebug = appConfig.isDebug,
            json = json,
            httpClientEngine = httpClientEngine
        )
    )

    @Singleton
    @Provides
    fun provideTvShowService(
        traktOAuthInfo: TraktOAuthInfo,
        httpClient: HttpClient
    ): TraktService = TraktServiceImpl(
        clientId = traktOAuthInfo.clientId,
        clientSecret = traktOAuthInfo.clientSecret,
        redirectUri = traktOAuthInfo.redirectUri,
        httpClient
    )

}
