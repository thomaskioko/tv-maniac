package com.thomaskioko.tvmaniac.data.seasondetails

import org.koin.core.module.Module as KoinModule
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.koin.dsl.module

actual fun seasonDetailsDomainModule(): KoinModule = module { }

@InstallIn(SingletonComponent::class)
@Module
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
