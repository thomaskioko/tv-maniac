package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import me.tatarka.inject.annotations.Provides

interface EpisodeComponent {

    @Provides
    fun provideEpisodesDao(bind: EpisodesDaoImpl): EpisodesDao = bind

    @Provides
    fun provideEpisodeRepository(bind: EpisodeRepositoryImpl): EpisodeRepository = bind
}
