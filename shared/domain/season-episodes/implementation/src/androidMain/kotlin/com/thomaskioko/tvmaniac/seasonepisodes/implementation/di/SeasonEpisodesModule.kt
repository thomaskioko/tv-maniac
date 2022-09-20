package com.thomaskioko.tvmaniac.seasonepisodes.implementation.di

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.seasonepisodes.api.ObserveSeasonEpisodesInteractor
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonWithEpisodesCache
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonWithEpisodesRepository
import com.thomaskioko.tvmaniac.seasonepisodes.implementation.SeasonWithEpisodesCacheImpl
import com.thomaskioko.tvmaniac.seasonepisodes.implementation.SeasonWithEpisodesRepositoryImpl
import com.thomaskioko.tvmaniac.seasons.api.SeasonsCache
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.shows.api.cache.TvShowCache
import com.thomaskioko.tvmaniac.shows.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import com.thomaskioko.tvmaniac.trakt.api.TraktService
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
        tmdbService: TmdbService,
        traktService: TraktService,
        tvShowCache: TvShowCache,
        episodesCache: EpisodesCache,
        seasonWithEpisodesCache: SeasonWithEpisodesCache,
        seasonsCache: SeasonsCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): SeasonWithEpisodesRepository = SeasonWithEpisodesRepositoryImpl(
        tmdbService,
        traktService,
        tvShowCache,
        episodesCache,
        seasonsCache,
        seasonWithEpisodesCache,
        ioDispatcher
    )

    @Singleton
    @Provides
    fun provideObserveSeasonWithEpisodesInteractor(
        tmdbRepository: TmdbRepository,
        repository: SeasonWithEpisodesRepository,
        @DefaultDispatcher computationDispatcher: CoroutineDispatcher
    ): ObserveSeasonEpisodesInteractor =
        ObserveSeasonEpisodesInteractor(tmdbRepository, repository, computationDispatcher)
}
