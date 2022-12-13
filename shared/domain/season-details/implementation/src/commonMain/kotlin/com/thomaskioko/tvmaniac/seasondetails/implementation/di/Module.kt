package com.thomaskioko.tvmaniac.seasondetails.implementation.di

import com.thomaskioko.tvmaniac.seasondetails.api.ObserveSeasonEpisodesInteractor
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonsCache
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.UpdateSeasonEpisodesInteractor
import com.thomaskioko.tvmaniac.seasondetails.implementation.SeasonsCacheImpl
import com.thomaskioko.tvmaniac.seasondetails.implementation.SeasonDetailsRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val seasonDetailsDomainModule: Module = module {
    single<SeasonDetailsRepository> { SeasonDetailsRepositoryImpl(get(), get(), get(), get()) }
    single<SeasonsCache> { SeasonsCacheImpl(get()) }
    single { UpdateSeasonEpisodesInteractor(get(), get(), get()) }
    single { ObserveSeasonEpisodesInteractor(get(), get()) }
}
