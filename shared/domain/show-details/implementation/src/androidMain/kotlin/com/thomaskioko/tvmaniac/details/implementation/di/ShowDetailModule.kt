package com.thomaskioko.tvmaniac.details.implementation.di

import com.thomaskioko.tvmaniac.details.api.ShowDetailsStateMachine
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.domain.trailers.api.TrailerRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ShowDetailModule {

    @Provides
    fun provideShowDetailsStateMachine(
        traktRepository: TraktRepository,
        similarShowsRepository: SimilarShowsRepository,
        seasonsRepository: SeasonsRepository,
        trailerRepository: TrailerRepository,
    ): ShowDetailsStateMachine = ShowDetailsStateMachine(
        traktRepository,
        similarShowsRepository,
        seasonsRepository,
        trailerRepository
    )
}
