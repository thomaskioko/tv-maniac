package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun tmdbPlatformModule(): Module = module {
    factory { TmdbHttpClient.tmdbHttpClient() }
    single<TmdbService> { TmdbServiceImpl(get()) }
}
