package com.thomaskioko.tvmaniac.seasons.implementation.di

import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import com.thomaskioko.tvmaniac.seasons.api.SeasonsCache
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.seasons.implementation.SeasonsCacheImpl
import com.thomaskioko.tvmaniac.seasons.implementation.SeasonsRepositoryImpl
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SeasonsModule {

    @Singleton
    @Provides
    fun provideTvShowSeasonCache(database: TvManiacDatabase): SeasonsCache {
        return SeasonsCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideTvShowSeasonsRepository(
        tvShowsService: TvShowsService,
        seasonCache: SeasonsCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): SeasonsRepository = SeasonsRepositoryImpl(
        tvShowsService,
        seasonCache,
        ioDispatcher
    )
}
