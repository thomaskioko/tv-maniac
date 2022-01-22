package com.thomaskioko.tvmaniac.seasons.implementation.di

import com.thomaskioko.tvmaniac.seasons.api.SeasonsCache
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.seasons.api.interactor.SeasonsInteractor
import com.thomaskioko.tvmaniac.seasons.implementation.SeasonsCacheImpl
import com.thomaskioko.tvmaniac.seasons.implementation.SeasonsRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val seasonsDomainModule: Module = module {
    single<SeasonsRepository> { SeasonsRepositoryImpl(get(), get(), get()) }
    single<SeasonsCache> { SeasonsCacheImpl(get()) }

    factory { SeasonsInteractor(get()) }
}
