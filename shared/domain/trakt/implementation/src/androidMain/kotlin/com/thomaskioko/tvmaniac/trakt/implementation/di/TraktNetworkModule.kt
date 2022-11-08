package com.thomaskioko.tvmaniac.trakt.implementation.di

import com.thomaskioko.tvmaniac.network.KtorClientFactory
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.implementation.TraktAuthInterceptor
import com.thomaskioko.tvmaniac.trakt.implementation.TraktServiceImpl
import com.thomaskioko.tvmaniac.trakt.implementation.traktHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TraktNetworkModule {


    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    @Provides
    @Named("trakt-json")
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
        explicitNulls = false
    }

    @Singleton
    @Provides
    @Named("trakt-http-engine")
    fun providehttpClientEngine(
        interceptor: TraktAuthInterceptor
    ): HttpClientEngine = OkHttp.create {
        addInterceptor(interceptor)
    }

    @Singleton
    @Provides
    @Named("trakt-http-client")
    fun provideHttpClient(
        @Named("trakt-json") json: Json,
        @Named("trakt-http-engine") httpClientEngine: HttpClientEngine
    ): HttpClient = KtorClientFactory().httpClient(
        traktHttpClient(
            json = json,
            httpClientEngine = httpClientEngine
        )
    )

    @Singleton
    @Provides
    fun provideTvShowService(
        @Named("trakt-client-id") clientId: String,
        @Named("trakt-client-secret") clientSecret: String,
        @Named("trakt-auth-redirect-uri") redirectUri: String,
        @Named("trakt-http-client") httpClient: HttpClient
    ): TraktService = TraktServiceImpl(clientId, clientSecret, redirectUri, httpClient)

}
