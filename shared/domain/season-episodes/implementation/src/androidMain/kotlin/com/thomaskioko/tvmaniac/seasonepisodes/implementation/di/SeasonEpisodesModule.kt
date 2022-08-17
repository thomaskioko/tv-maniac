package com.thomaskioko.tvmaniac.seasonepisodes.implementation.di

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.seasonepisodes.api.ObserveSeasonWithEpisodesInteractor
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonWithEpisodesCache
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonWithEpisodesRepository
import com.thomaskioko.tvmaniac.seasonepisodes.implementation.SeasonWithEpisodesCacheImpl
import com.thomaskioko.tvmaniac.seasonepisodes.implementation.SeasonWithEpisodesRepositoryImpl
import com.thomaskioko.tvmaniac.seasons.api.SeasonsCache
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.showcommon.api.repository.TvShowsRepository
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
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
        apiService: TmdbService,
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
        tvShowsRepository: TvShowsRepository,
        repository: SeasonWithEpisodesRepository,
        @DefaultDispatcher computationDispatcher: CoroutineDispatcher
    ): ObserveSeasonWithEpisodesInteractor =
        ObserveSeasonWithEpisodesInteractor(tvShowsRepository, repository, computationDispatcher)
}
