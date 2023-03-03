package com.thomaskioko.tvmaniac.data.trailers.implementation

import org.koin.core.module.Module
import org.koin.dsl.module

actual fun trailersModule(): Module = module {
    single<TrailerRepository> { TrailerRepositoryImpl(get(), get(), get(), get(), get()) }
    single<TrailerCache> { TrailerCacheImpl(get(), get()) }
}

