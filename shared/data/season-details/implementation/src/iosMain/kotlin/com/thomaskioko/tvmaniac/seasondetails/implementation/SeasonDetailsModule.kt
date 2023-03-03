package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonsCache
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun seasonDetailsDataModule(): Module = module {
    single<SeasonDetailsRepository> { SeasonDetailsRepositoryImpl(get(), get(), get(), get(), get()) }
    single<SeasonsCache> { SeasonsCacheImpl(get(), get()) }
}