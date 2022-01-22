package com.thomaskioko.tvmaniac.injection

import com.thomaskioko.tvmaniac.discover.api.interactor.ObserveDiscoverShowsInteractor
import com.thomaskioko.tvmaniac.discover.api.interactor.ObserveShowInteractor
import com.thomaskioko.tvmaniac.discover.api.interactor.UpdateWatchlistInteractor
import com.thomaskioko.tvmaniac.discover.api.repository.TvShowsRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesInteractor
import com.thomaskioko.tvmaniac.genre.api.GenreRepository
import com.thomaskioko.tvmaniac.genre.api.GetGenresInteractor
import com.thomaskioko.tvmaniac.interactors.GetShowsByCategoryInteractor
import com.thomaskioko.tvmaniac.interactors.ObserveWatchListInteractor
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisodeRepository
import com.thomaskioko.tvmaniac.lastairepisodes.api.ObserveAirEpisodesInteractor
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.seasons.api.interactor.SeasonsInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InteractorsModule {

    @Singleton
    @Provides
    fun provideTvShowSeasonsInteractor(
        repository: SeasonsRepository
    ): SeasonsInteractor = SeasonsInteractor(repository)

    @Singleton
    @Provides
    fun provideEpisodesInteractor(
        repository: EpisodeRepository
    ): EpisodesInteractor = EpisodesInteractor(repository)

    @Singleton
    @Provides
    fun provideObserveShowsByCategoryInteractor(
        repository: TvShowsRepository
    ): ObserveDiscoverShowsInteractor = ObserveDiscoverShowsInteractor(repository)

    @Singleton
    @Provides
    fun provideObserveShowInteractor(
        repository: TvShowsRepository
    ): ObserveShowInteractor = ObserveShowInteractor(repository)

    @Singleton
    @Provides
    fun provideGenreRepository(
        repository: GenreRepository
    ): GetGenresInteractor = GetGenresInteractor(repository)

    @Singleton
    @Provides
    fun provideObserveWatchListInteractor(
        repository: TvShowsRepository
    ): ObserveWatchListInteractor = ObserveWatchListInteractor(repository)

    @Singleton
    @Provides
    fun provideUpdateWatchlistInteractor(
        repository: TvShowsRepository
    ): UpdateWatchlistInteractor = UpdateWatchlistInteractor(repository)

    @Singleton
    @Provides
    fun provideObserveAirEpisodesInteractor(
        repository: LastAirEpisodeRepository
    ): ObserveAirEpisodesInteractor = ObserveAirEpisodesInteractor(repository)

    @Singleton
    @Provides
    fun provideGetShowsByTypeInteractor(
        repository: TvShowsRepository
    ): GetShowsByCategoryInteractor = GetShowsByCategoryInteractor(repository)
}
