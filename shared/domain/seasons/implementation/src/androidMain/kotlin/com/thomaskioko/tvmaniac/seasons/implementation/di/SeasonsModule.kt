package com.thomaskioko.tvmaniac.seasons.implementation.di

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.seasons.api.SeasonsCache
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.seasons.implementation.SeasonsCacheImpl
import com.thomaskioko.tvmaniac.seasons.implementation.SeasonsRepositoryImpl
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
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
        tmdbService: TmdbService,
        seasonCache: SeasonsCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): SeasonsRepository = SeasonsRepositoryImpl(
        tmdbService,
        seasonCache,
        ioDispatcher
    )
}
