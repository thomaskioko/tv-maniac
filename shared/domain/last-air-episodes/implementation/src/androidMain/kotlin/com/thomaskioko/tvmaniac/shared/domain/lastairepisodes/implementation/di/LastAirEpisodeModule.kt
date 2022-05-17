package com.thomaskioko.tvmaniac.shared.domain.lastairepisodes.implementation.di

import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisodeRepository
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastEpisodeAirCache
import com.thomaskioko.tvmaniac.lastairepisodes.api.ObserveAirEpisodesInteractor
import com.thomaskioko.tvmaniac.lastairepisodes.implementation.LastAirEpisodeRepositoryImpl
import com.thomaskioko.tvmaniac.lastairepisodes.implementation.LastEpisodeAirCacheImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LastAirEpisodeModule {

    @Singleton
    @Provides
    fun provideEpisodeAirCache(database: TvManiacDatabase): LastEpisodeAirCache {
        return LastEpisodeAirCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideLastAirEpisodeRepository(
        cache: LastEpisodeAirCache,
    ): LastAirEpisodeRepository = LastAirEpisodeRepositoryImpl(cache)

    @Singleton
    @Provides
    fun provideObserveAirEpisodesInteractor(
        repository: LastAirEpisodeRepository
    ): ObserveAirEpisodesInteractor = ObserveAirEpisodesInteractor(repository)
}
