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

    @Provides
    fun provideTmdbInterceptor(): TmdbInterceptor = TmdbInterceptor()

    @Singleton
    @Provides
    @Named("tmdb-http-client")
    fun provideHttpClient(
        tmdbInterceptor: TmdbInterceptor
    ): HttpClient = KtorClientFactory().httpClient(tmdbHttpClient(tmdbInterceptor))

    @Singleton
    @Provides
    fun provideTvShowService(
        @Named("tmdb-http-client") httpClient: HttpClient
    ): TmdbService = TmdbServiceImpl(httpClient)

}
