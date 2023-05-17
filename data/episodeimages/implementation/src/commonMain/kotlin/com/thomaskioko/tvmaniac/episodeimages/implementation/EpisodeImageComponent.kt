package com.thomaskioko.tvmaniac.episodeimages.implementation

import com.thomaskioko.tvmaniac.episodeimages.api.EpisodeImageDao
import com.thomaskioko.tvmaniac.episodeimages.api.EpisodeImageRepository
import me.tatarka.inject.annotations.Provides

interface EpisodeImageComponent {

    @Provides
    fun provideEpisodeImageDao(bind: EpisodeImageDaoImpl): EpisodeImageDao = bind

    @Provides
    fun provideEpisodeImageRepository(
        bind: EpisodeImageRepositoryImpl
    ): EpisodeImageRepository = bind
}
