package com.thomaskioko.tvmaniac.shared.domain.episodes.implementation.di

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodeImageCache
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodeImageCacheImpl
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodeRepositoryImpl
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodesCacheImpl
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EpisodeModule {

    @Singleton
    @Provides
    fun provideEpisodesCache(database: TvManiacDatabase): EpisodesCache {
        return EpisodesCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideEpisodeImageCache(database: TvManiacDatabase): EpisodeImageCache =
        EpisodeImageCacheImpl(database)

    @Singleton
    @Provides
    fun provideEpisodeRepository(
        tmdbService: TmdbService,
        episodesCache: EpisodesCache,
        episodeImageCache: EpisodeImageCache,
    ): EpisodeRepository = EpisodeRepositoryImpl(
        tmdbService,
        episodesCache,
        episodeImageCache,
    )
}
