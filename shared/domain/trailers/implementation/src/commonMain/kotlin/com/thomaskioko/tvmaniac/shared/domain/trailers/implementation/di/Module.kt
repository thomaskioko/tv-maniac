package com.thomaskioko.tvmaniac.shared.domain.trailers.implementation.di

import com.thomaskioko.tvmaniac.shared.domain.trailers.api.ObserveTrailerInteractor
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerCache
import com.thomaskioko.tvmaniac.shared.domain.trailers.implementation.TrailerCacheImpl
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerRepository
import com.thomaskioko.tvmaniac.shared.domain.trailers.implementation.TrailerRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val trailersModule: Module = module {
    single<TrailerRepository> { TrailerRepositoryImpl(get(), get(), get(), get()) }
    single<TrailerCache> { TrailerCacheImpl(get()) }
    factory{ ObserveTrailerInteractor(get()) }
}
