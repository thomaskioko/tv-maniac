package com.thomaskioko.tvmaniac.shared.domain.episodes.implementation.di

import com.thomaskioko.tvmaniac.core.annotations.DefaultDispatcher
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.episodes.api.ObserveEpisodesInteractor
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodeRepositoryImpl
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodesCacheImpl
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
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
    fun provideEpisodeRepository(
        tvShowsService: TvShowsService,
        episodesCache: EpisodesCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): EpisodeRepository = EpisodeRepositoryImpl(
        tvShowsService,
        episodesCache,
        ioDispatcher
    )

    @Singleton
    @Provides
    fun provideEpisodesInteractor(
        repository: EpisodeRepository
    ): ObserveEpisodesInteractor = ObserveEpisodesInteractor(repository)
}
