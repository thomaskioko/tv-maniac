package com.thomaskioko.tvmaniac.seasonepisodes.implementation.di

import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import com.thomaskioko.tvmaniac.seasonepisodes.api.ObserveSeasonWithEpisodesInteractor
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonWithEpisodesCache
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonWithEpisodesRepository
import com.thomaskioko.tvmaniac.seasonepisodes.implementation.SeasonWithEpisodesCacheImpl
import com.thomaskioko.tvmaniac.seasonepisodes.implementation.SeasonWithEpisodesRepositoryImpl
import com.thomaskioko.tvmaniac.seasons.api.SeasonsCache
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SeasonEpisodesModule {

    @Singleton
    @Provides
    fun provideSeasonWithEpisodesCache(database: TvManiacDatabase): SeasonWithEpisodesCache {
        return SeasonWithEpisodesCacheImpl(database)
    }

    @Singleton
    @Provides
    fun provideSeasonWithEpisodesRepository(
        apiService: TvShowsService,
        episodesCache: EpisodesCache,
        seasonWithEpisodesCache: SeasonWithEpisodesCache,
        seasonsCache: SeasonsCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): SeasonWithEpisodesRepository = SeasonWithEpisodesRepositoryImpl(
        apiService,
        episodesCache,
        seasonsCache,
        seasonWithEpisodesCache,
        ioDispatcher
    )

    @Singleton
    @Provides
    fun provideObserveSeasonWithEpisodesInteractor(
        repository: SeasonWithEpisodesRepository,
        @DefaultDispatcher computationDispatcher: CoroutineDispatcher
    ): ObserveSeasonWithEpisodesInteractor =
        ObserveSeasonWithEpisodesInteractor(repository, computationDispatcher)
}
