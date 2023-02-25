package com.thomaskioko.tvmaniac.details.implementation.di

import com.thomaskioko.tvmaniac.details.api.ShowDetailsStateMachine
import com.thomaskioko.tvmaniac.domain.trailers.api.TrailerRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktShowRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ShowDetailModule {

    @Provides
    fun provideShowDetailsStateMachine(
        traktShowRepository: TraktShowRepository,
        similarShowsRepository: SimilarShowsRepository,
        seasonDetailsRepository: SeasonDetailsRepository,
        trailerRepository: TrailerRepository,
    ): ShowDetailsStateMachine = ShowDetailsStateMachine(
        traktShowRepository,
        similarShowsRepository,
        seasonDetailsRepository,
        trailerRepository
    )
}
