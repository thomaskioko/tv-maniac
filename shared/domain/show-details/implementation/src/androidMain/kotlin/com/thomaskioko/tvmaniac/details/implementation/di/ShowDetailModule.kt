package com.thomaskioko.tvmaniac.details.implementation.di

import com.thomaskioko.tvmaniac.details.api.interactor.ObserveFollowingInteractor
import com.thomaskioko.tvmaniac.details.api.interactor.ObserveShowInteractor
import com.thomaskioko.tvmaniac.details.api.interactor.ObserveShowsByCategoryInteractor
import com.thomaskioko.tvmaniac.details.api.interactor.UpdateFollowingInteractor
import com.thomaskioko.tvmaniac.genre.api.GenreRepository
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisodeRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerRepository
import com.thomaskioko.tvmaniac.shows.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShowDetailModule {

    @Singleton
    @Provides
    fun provideObserveShowInteractor(
        traktRepository: TraktRepository,
        similarShowsRepository: SimilarShowsRepository,
        seasonsRepository: SeasonsRepository,
        genreRepository: GenreRepository,
        lastAirRepository: LastAirEpisodeRepository,
        trailerRepository: TrailerRepository
    ): ObserveShowInteractor = ObserveShowInteractor(
        traktRepository,
        similarShowsRepository,
        seasonsRepository,
        genreRepository,
        lastAirRepository,
        trailerRepository
    )

    @Singleton
    @Provides
    fun provideUpdateFollowingInteractor(
        traktRepository: TraktRepository
    ): UpdateFollowingInteractor = UpdateFollowingInteractor(traktRepository)

    @Singleton
    @Provides
    fun provideObserveWatchListInteractor(
        repository: TraktRepository
    ): ObserveFollowingInteractor = ObserveFollowingInteractor(repository)

    @Singleton
    @Provides
    fun provideGetShowsByTypeInteractor(
        repository: TmdbRepository
    ): ObserveShowsByCategoryInteractor = ObserveShowsByCategoryInteractor(repository)
}
