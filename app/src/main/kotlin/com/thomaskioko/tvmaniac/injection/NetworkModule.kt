package com.thomaskioko.tvmaniac.injection

import com.thomaskioko.tvmaniac.datasource.TvShowsRepository
import com.thomaskioko.tvmaniac.datasource.TvShowsRepositoryImpl
import com.thomaskioko.tvmaniac.datasource.cache.db.TvShowCache
import com.thomaskioko.tvmaniac.datasource.network.KtorClientFactory
import com.thomaskioko.tvmaniac.datasource.network.TvShowsService
import com.thomaskioko.tvmaniac.datasource.network.TvShowsServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
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

    @Singleton
    @Provides
    fun provideTvShowsRepository(
        tvShowsService: TvShowsService,
        cache: TvShowCache
    ): TvShowsRepository = TvShowsRepositoryImpl(tvShowsService, cache)
}