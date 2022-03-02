package com.thomaskioko.tvmaniac.injection

import com.thomaskioko.tvmaniac.core.annotations.DefaultDispatcher
import com.thomaskioko.tvmaniac.details.api.interactor.ObserveShowInteractor
import com.thomaskioko.tvmaniac.details.api.interactor.UpdateFollowingInteractor
import com.thomaskioko.tvmaniac.details.api.repository.TvShowsRepository
import com.thomaskioko.tvmaniac.discover.api.ObserveDiscoverShowsInteractor
import com.thomaskioko.tvmaniac.discover.api.repository.DiscoverRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.ObserveEpisodesInteractor
import com.thomaskioko.tvmaniac.genre.api.GenreRepository
import com.thomaskioko.tvmaniac.genre.api.GetGenresInteractor
import com.thomaskioko.tvmaniac.interactors.ObserveFollowingInteractor
import com.thomaskioko.tvmaniac.interactors.ObserveShowsByCategoryInteractor
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisodeRepository
import com.thomaskioko.tvmaniac.lastairepisodes.api.ObserveAirEpisodesInteractor
import com.thomaskioko.tvmaniac.seasonepisodes.api.ObserveSeasonWithEpisodesInteractor
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonWithEpisodesRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.seasons.api.interactor.ObserveSeasonsInteractor
import com.thomaskioko.tvmaniac.similar.api.ObserveSimilarShowsInteractor
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InteractorsModule {

    @Singleton
    @Provides
    fun provideTvShowSeasonsInteractor(
        repository: SeasonsRepository
    ): ObserveSeasonsInteractor = ObserveSeasonsInteractor(repository)

    @Singleton
    @Provides
    fun provideEpisodesInteractor(
        repository: EpisodeRepository
    ): ObserveEpisodesInteractor = ObserveEpisodesInteractor(repository)

    @Singleton
    @Provides
    fun provideObserveShowsByCategoryInteractor(
        repository: DiscoverRepository
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
    ): ObserveFollowingInteractor = ObserveFollowingInteractor(repository)

    @Singleton
    @Provides
    fun provideUpdateFollowingInteractor(
        repository: TvShowsRepository
    ): UpdateFollowingInteractor = UpdateFollowingInteractor(repository)

    @Singleton
    @Provides
    fun provideObserveAirEpisodesInteractor(
        repository: LastAirEpisodeRepository
    ): ObserveAirEpisodesInteractor = ObserveAirEpisodesInteractor(repository)

    @Singleton
    @Provides
    fun provideGetShowsByTypeInteractor(
        repository: TvShowsRepository
    ): ObserveShowsByCategoryInteractor = ObserveShowsByCategoryInteractor(repository)

    @Singleton
    @Provides
    fun provideObserveObserveSimilarShowsInteractor(
        repository: SimilarShowsRepository
    ): ObserveSimilarShowsInteractor = ObserveSimilarShowsInteractor(repository)

    @Singleton
    @Provides
    fun provideObserveSeasonWithEpisodesInteractor(
        repository: SeasonWithEpisodesRepository,
        @DefaultDispatcher computationDispatcher: CoroutineDispatcher
    ): ObserveSeasonWithEpisodesInteractor = ObserveSeasonWithEpisodesInteractor(repository, computationDispatcher)
}
