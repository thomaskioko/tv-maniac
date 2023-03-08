package com.thomaskioko.tvmaniac.shared.domain.lastairepisodes.implementation.di

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisodeRepository
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastEpisodeAirCache
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
}
