package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.network.KtorClientFactory
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
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
    @Named("tmdb-url")
    fun provideTmdbUrl(): String = "https://api.themoviedb.org"

    @Singleton
    @Provides
    fun provideHttpClient(
        @Named("tmdb-url") url: String
    ): HttpClient {
        return KtorClientFactory().build(url)
    }

    @Singleton
    @Provides
    fun provideTvShowService(
        httpClient: HttpClient
    ): TmdbService = TmdbServiceImpl(httpClient)

}
