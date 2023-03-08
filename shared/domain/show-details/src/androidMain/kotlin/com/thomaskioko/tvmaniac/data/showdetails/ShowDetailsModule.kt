package com.thomaskioko.tvmaniac.data.showdetails


import org.koin.core.module.Module as KoinModule
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktShowRepository
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.koin.dsl.module


actual fun showDetailsDomainModule(): KoinModule = module { }

@dagger.Module
@InstallIn(SingletonComponent::class)
object ShowDetailsModule {

    @Provides
    fun provideShowDetailsStateMachine(
        traktShowRepository: TraktShowRepository,
        similarShowsRepository: SimilarShowsRepository,
        seasonDetailsRepository: SeasonDetailsRepository,
        trailerRepository: TrailerRepository
    ): ShowDetailsStateMachine = ShowDetailsStateMachine(
        traktShowRepository = traktShowRepository,
        similarShowsRepository = similarShowsRepository,
        seasonDetailsRepository = seasonDetailsRepository,
        trailerRepository = trailerRepository
    )

}