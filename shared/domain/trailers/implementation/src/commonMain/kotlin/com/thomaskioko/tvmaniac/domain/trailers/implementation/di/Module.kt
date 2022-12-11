package com.thomaskioko.tvmaniac.domain.trailers.implementation.di

import com.thomaskioko.tvmaniac.domain.trailers.api.TrailersStateMachine
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerCache
import com.thomaskioko.tvmaniac.domain.trailers.api.TrailerRepository
import com.thomaskioko.tvmaniac.domain.trailers.implementation.TrailerCacheImpl
import com.thomaskioko.tvmaniac.domain.trailers.implementation.TrailerRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val trailersModule: Module = module {
    single<TrailerRepository> { TrailerRepositoryImpl(get(), get(), get(), get(), get()) }
    single<TrailerCache> { TrailerCacheImpl(get()) }
    factory{ TrailersStateMachine(get()) }
}
