package com.thomaskioko.tvmaniac.episodes.implementation.di

import com.thomaskioko.tvmaniac.episodes.api.EpisodeImageCache
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesCache
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodeImageCacheImpl
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodeRepositoryImpl
import com.thomaskioko.tvmaniac.episodes.implementation.EpisodesCacheImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val episodeDomainModule: Module = module {
    single<EpisodeRepository> { EpisodeRepositoryImpl(get(), get(), get(), get(), get()) }
    single<EpisodesCache> { EpisodesCacheImpl(get()) }
    single<EpisodeImageCache> { EpisodeImageCacheImpl(get()) }
}
