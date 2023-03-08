package com.thomaskioko.tvmaniac.trakt.implementation.injection

import com.thomaskioko.tvmaniac.network.KtorClientFactory
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import com.thomaskioko.tvmaniac.trakt.implementation.TraktAuthInterceptor
import com.thomaskioko.tvmaniac.trakt.implementation.TraktHttpClient.traktHttpClient
import com.thomaskioko.tvmanic.trakt.implementation.TraktServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TraktNetworkModule {

    @Singleton
    @Provides
    @Named("trakt-url")
    fun provideTraktUrl(): String = "https://api.trakt.tv/"

    @Singleton
    @Provides
    @Named("trakt-http-client")
    fun provideHttpClient(
        @Named("trakt-url") httpUrl: String,
        interceptor: TraktAuthInterceptor
    ): HttpClient = KtorClientFactory().httpClient(traktHttpClient(httpUrl, interceptor))

    @Singleton
    @Provides
    fun provideTvShowService(
        @Named("trakt-client-id") clientId: String,
        @Named("trakt-client-secret") clientSecret: String,
        @Named("trakt-auth-redirect-uri") redirectUri: String,
        @Named("trakt-http-client") httpClient: HttpClient
    ): TraktService = TraktServiceImpl(clientId, clientSecret, redirectUri, httpClient)

}
