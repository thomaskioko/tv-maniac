package com.thomaskioko.tvmaniac.episodes.implementation.di

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.util.scope.DefaultDispatcher
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
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.dsl.module
import javax.inject.Singleton

actual fun episodeDataModule(): org.koin.core.module.Module = module { }

@Module
@InstallIn(SingletonComponent::class)
object EpisodeModule {

    @Singleton
    @Provides
    fun provideEpisodesCache(
        database: TvManiacDatabase,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): EpisodesCache = EpisodesCacheImpl(database, ioDispatcher)


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
