package com.thomaskioko.tvmaniac.injection

import android.content.Context
import com.thomaskioko.tvmaniac.datasource.cache.DriverFactory
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.datasource.cache.TvShowsDatabaseFactory
import com.thomaskioko.tvmaniac.datasource.cache.db.TvShowCache
import com.thomaskioko.tvmaniac.datasource.cache.db.TvShowCacheImpl
import com.thomaskioko.tvmaniac.datasource.cache.db.episode.EpisodesCache
import com.thomaskioko.tvmaniac.datasource.cache.db.episode.EpisodesCacheImpl
import com.thomaskioko.tvmaniac.datasource.cache.db.seasons.SeasonsCache
import com.thomaskioko.tvmaniac.datasource.cache.db.seasons.SeasonsCacheImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDriverFactory(@ApplicationContext context: Context): DriverFactory {
        return DriverFactory(context = context)
    }

    @Singleton
    @Provides
    fun provideTvShowDatabase(driverFactory: DriverFactory): TvManiacDatabase {
        return TvShowsDatabaseFactory(driverFactory).createDatabase()
    }

    @Singleton
    @Provides
    fun provideTvShowCache(database: TvManiacDatabase): TvShowCache {
        return TvShowCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideTvShowSeasonCache(database: TvManiacDatabase): SeasonsCache {
        return SeasonsCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideEpisodesCache(database: TvManiacDatabase): EpisodesCache {
        return EpisodesCacheImpl(database)
    }
}