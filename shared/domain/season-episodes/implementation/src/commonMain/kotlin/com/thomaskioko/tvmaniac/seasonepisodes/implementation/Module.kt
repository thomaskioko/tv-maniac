package com.thomaskioko.tvmaniac.seasonepisodes.implementation

import com.thomaskioko.tvmaniac.seasonepisodes.api.ObserveSeasonEpisodesInteractor
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonWithEpisodesCache
import com.thomaskioko.tvmaniac.seasonepisodes.api.SeasonWithEpisodesRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val seasonEpisodesDomainModule: Module = module {
    single<SeasonWithEpisodesRepository> {
        SeasonWithEpisodesRepositoryImpl(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    single<SeasonWithEpisodesCache> { SeasonWithEpisodesCacheImpl(get()) }
    factory { ObserveSeasonEpisodesInteractor(get(), get(), get()) }
}
