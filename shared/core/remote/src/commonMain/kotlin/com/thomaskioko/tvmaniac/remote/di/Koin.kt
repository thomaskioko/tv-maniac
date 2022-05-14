package com.thomaskioko.tvmaniac.remote.di

import com.thomaskioko.tvmaniac.remote.api.TvShowsService
import com.thomaskioko.tvmaniac.remote.api.TvShowsServiceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val serviceModule: Module = module {
    single<TvShowsService> { TvShowsServiceImpl(get()) }
}
