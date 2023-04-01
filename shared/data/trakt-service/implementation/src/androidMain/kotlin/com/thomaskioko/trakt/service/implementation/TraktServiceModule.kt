package com.thomaskioko.trakt.service.implementation

import org.koin.core.module.Module as KoinModule
import com.thomaskioko.tvmaniac.network.KtorClientFactory
import com.thomaskioko.tvmaniac.trakt.service.api.TraktService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import javax.inject.Named
import javax.inject.Singleton


actual fun traktServiceModule() : KoinModule = module {  }

@Module
@InstallIn(SingletonComponent::class)
object TraktServiceModule {


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
    fun provideHttpClientEngine(
        interceptor: TraktAuthInterceptor
    ): HttpClientEngine = OkHttp.create {
        addInterceptor(interceptor)
    }

    @Singleton
    @Provides
    @Named("trakt-http-client")
    fun provideHttpClient(
        @Named("app-build") isDebug: Boolean,
        @Named("trakt-json") json: Json,
        @Named("trakt-http-engine") httpClientEngine: HttpClientEngine
    ): HttpClient = KtorClientFactory().httpClient(
        traktHttpClient(
            isDebug = isDebug,
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
