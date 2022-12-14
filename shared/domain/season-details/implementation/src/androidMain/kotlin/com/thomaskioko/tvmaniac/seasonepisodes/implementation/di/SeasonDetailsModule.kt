package com.thomaskioko.tvmaniac.seasonepisodes.implementation.di

import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsStateMachine
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonsCache
import com.thomaskioko.tvmaniac.seasondetails.implementation.SeasonDetailsRepositoryImpl
import com.thomaskioko.tvmaniac.seasondetails.implementation.SeasonsCacheImpl
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.trakt.api.TraktService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SeasonDetailsModule {

    @Singleton
    @Provides
    fun provideSeasonDetailsRepository(
        traktService: TraktService,
        seasonCache: SeasonsCache,
        episodesCache: EpisodesCache,
        @DefaultDispatcher ioDispatcher: CoroutineDispatcher
    ): SeasonDetailsRepository = SeasonDetailsRepositoryImpl(
        traktService,
        seasonCache,
        episodesCache,
        ioDispatcher
    )

    @Singleton
    @Provides
    fun provideUpdateSeasonEpisodesInteractor(
        detailsRepository: SeasonDetailsRepository,
        episodeRepository: EpisodeRepository
    ): SeasonDetailsStateMachine = SeasonDetailsStateMachine(detailsRepository, episodeRepository)

    @Singleton
    @Provides
    fun provideTvShowSeasonCache(database: TvManiacDatabase): SeasonsCache {
        return SeasonsCacheImpl(database)
    }

}
