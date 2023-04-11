package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.episodes.api.EpisodeImageCache
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import me.tatarka.inject.annotations.Provides

interface EpisodeComponent {

    @Provides
    fun provideEpisodesCache(bind: EpisodesCacheImpl): EpisodesCache = bind

    @Provides
    fun provideEpisodeImageCache(bind: EpisodeImageCacheImpl): EpisodeImageCache = bind

    @Provides
    fun provideEpisodeRepository(bind: EpisodeRepositoryImpl): EpisodeRepository = bind

}