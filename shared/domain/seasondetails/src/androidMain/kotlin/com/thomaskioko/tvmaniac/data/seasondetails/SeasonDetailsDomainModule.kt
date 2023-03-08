package com.thomaskioko.tvmaniac.data.seasondetails

import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun seasonDetailsDomainModule(): Module = module { }

@InstallIn(SingletonComponent::class)
@dagger.Module
object SeasonDetailsDomainModule {

    @Provides
    fun provideSeasonDetailsStateMachine(
        seasonDetailsRepository: SeasonDetailsRepository,
        episodeRepository: EpisodeRepository,
    ): SeasonDetailsStateMachine = SeasonDetailsStateMachine(
        seasonDetailsRepository = seasonDetailsRepository,
        episodeRepository = episodeRepository
    )
}
