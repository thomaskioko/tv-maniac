package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun similarDataModule(): Module = module {
    single<SimilarShowsRepository> {
        SimilarShowsRepositoryImpl(get(), get(), get(), get())
    }

    single<SimilarShowCache> { SimilarShowCacheImpl(get(), get()) }

}
