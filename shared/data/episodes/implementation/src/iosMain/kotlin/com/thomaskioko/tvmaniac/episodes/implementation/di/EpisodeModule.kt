package com.thomaskioko.tvmaniac.episodes.implementation.di

import com.thomaskioko.tvmaniac.episodes.api.EpisodeImageCache
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodeImageCacheImpl
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodeRepositoryImpl
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodesCacheImpl
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun episodeDataModule(): Module = module {
    single<EpisodeRepository> {
        EpisodeRepositoryImpl(
            tmdbService = get(),
            episodesCache = get(),
            episodeImageCache = get()
        )
    }
    single<EpisodeImageCache> { EpisodeImageCacheImpl(database = get()) }
    single<EpisodesCache> {
        EpisodesCacheImpl(
            database = get(),
            coroutineContext = Dispatchers.Default
        )
    }
}
