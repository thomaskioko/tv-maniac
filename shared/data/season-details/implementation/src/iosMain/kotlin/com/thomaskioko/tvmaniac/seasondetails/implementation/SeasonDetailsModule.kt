package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonsCache
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun seasonDetailsDataModule(): Module = module {
    single<SeasonDetailsRepository> {
        SeasonDetailsRepositoryImpl(
            traktService = get(),
            seasonCache = get(),
            episodesCache = get(),
            datastore = get(),
            dispatcher = Dispatchers.Default
        )
    }
    single<SeasonsCache> {
        SeasonsCacheImpl(
            database = get(),
            coroutineContext = Dispatchers.Default
        )
    }
}