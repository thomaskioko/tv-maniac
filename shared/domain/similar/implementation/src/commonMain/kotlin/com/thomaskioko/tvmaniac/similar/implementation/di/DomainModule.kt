package com.thomaskioko.tvmaniac.similar.implementation.di

import com.thomaskioko.tvmaniac.similar.api.ObserveSimilarShowsInteractor
import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.similar.implementation.SimilarShowCacheImpl
import com.thomaskioko.tvmaniac.similar.implementation.SimilarShowsRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val similarDomainModule: Module = module {
    single<SimilarShowsRepository> {
        SimilarShowsRepositoryImpl(get(), get(), get(), get())
    }

    single<SimilarShowCache> { SimilarShowCacheImpl(get()) }

    factory { ObserveSimilarShowsInteractor(get()) }
}
