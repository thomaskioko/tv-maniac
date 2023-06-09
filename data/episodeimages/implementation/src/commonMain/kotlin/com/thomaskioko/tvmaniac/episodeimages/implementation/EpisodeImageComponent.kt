package com.thomaskioko.tvmaniac.episodeimages.implementation

import com.thomaskioko.tvmaniac.episodeimages.api.EpisodeImageDao
import com.thomaskioko.tvmaniac.episodeimages.api.EpisodeImageRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface EpisodeImageComponent {

    @ApplicationScope
    @Provides
    fun provideEpisodeImageDao(bind: EpisodeImageDaoImpl): EpisodeImageDao = bind

    @ApplicationScope
    @Provides
    fun provideEpisodeImageRepository(
        bind: EpisodeImageRepositoryImpl,
    ): EpisodeImageRepository = bind
}
