package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.network.KtorClientFactory
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.tmdb.implementation.TmdbHttpClient.tmdbHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    @Named("tmdb-url")
    fun provideTmdbUrl(): String = "https://api.themoviedb.org/"

    @Singleton
    @Provides
    @Named("tmdb-http-client")
    fun provideHttpClient(
        @Named("tmdb-url") httpUrl: String,
        tmdbInterceptor: TmdbInterceptor
    ): HttpClient = KtorClientFactory().httpClient(tmdbHttpClient(httpUrl, tmdbInterceptor))

    @Singleton
    @Provides
    fun provideTvShowService(
        @Named("tmdb-http-client") httpClient: HttpClient
    ): TmdbService = TmdbServiceImpl(httpClient)

}
