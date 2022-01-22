package com.thomaskioko.tvmaniac.lastairepisodes.implementation.di

import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisodeRepository
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastEpisodeAirCache
import com.thomaskioko.tvmaniac.lastairepisodes.api.ObserveAirEpisodesInteractor
import com.thomaskioko.tvmaniac.lastairepisodes.implementation.LastAirEpisodeRepositoryImpl
import com.thomaskioko.tvmaniac.lastairepisodes.implementation.LastEpisodeAirCacheImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val lastAirEpisodeDomainModule: Module = module {
    single<LastAirEpisodeRepository> {
        LastAirEpisodeRepositoryImpl(get())
    }

    factory { ObserveAirEpisodesInteractor(get()) }

    single<LastEpisodeAirCache> { LastEpisodeAirCacheImpl(get()) }
}
