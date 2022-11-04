package com.thomaskioko.tvmaniac.tmdb.implementation

import com.thomaskioko.tvmaniac.tmdb.api.ShowImageCache
import com.thomaskioko.tvmaniac.tmdb.api.TmdbService
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun tmdbModule(): Module = module {
    factory { TmdbHttpClient.tmdbHttpClient() }
    single<TmdbService> { TmdbServiceImpl(get()) }
    single<ShowImageCache> { ShowImageCacheImpl(get()) }
}
