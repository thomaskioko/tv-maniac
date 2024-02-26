package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import me.tatarka.inject.annotations.Provides

interface EpisodeComponent {

  @ApplicationScope @Provides fun provideEpisodesDao(bind: EpisodesDaoImpl): EpisodesDao = bind

  @ApplicationScope
  @Provides
  fun provideEpisodeRepository(bind: EpisodeRepositoryImpl): EpisodeRepository = bind
}
