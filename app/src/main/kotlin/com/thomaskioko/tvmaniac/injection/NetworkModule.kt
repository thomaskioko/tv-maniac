package com.thomaskioko.tvmaniac.injection

import com.thomaskioko.tvmaniac.datasource.network.KtorClientFactory
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return KtorClientFactory().build()
    }

    @Singleton
    @Provides
    fun provideTvShowService(
        httpClient: HttpClient
    ): TvShowsService = TvShowsServiceImpl(httpClient)
}