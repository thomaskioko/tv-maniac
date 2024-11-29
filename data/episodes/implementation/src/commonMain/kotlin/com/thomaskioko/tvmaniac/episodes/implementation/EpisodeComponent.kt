package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface EpisodeComponent {

  @SingleIn(AppScope::class)
  @Provides fun provideEpisodesDao(bind: DefaultEpisodesDao): EpisodesDao = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideEpisodeRepository(bind: DefaultEpisodeRepository): EpisodeRepository = bind
}
