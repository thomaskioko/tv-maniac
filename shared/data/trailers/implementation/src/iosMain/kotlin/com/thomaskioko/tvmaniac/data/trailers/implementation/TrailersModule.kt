package com.thomaskioko.tvmaniac.data.trailers.implementation

import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun trailersModule(): Module = module {
    single<TrailerRepository> {
        TrailerRepositoryImpl(
            apiService = get(),
            trailerCache = get(),
            tvShowCache = get(),
            appUtils = get(),
            dispatcher = Dispatchers.Default
        )
    }
    single<TrailerCache> {
        TrailerCacheImpl(
            database = get(),
            coroutineContext = Dispatchers.Default
        )
    }
}

