package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import org.koin.core.module.Module
import org.koin.dsl.module

val tmdbServiceModule: Module = module {
    single<TmdbService> { TmdbServiceImpl(get()) }
}
