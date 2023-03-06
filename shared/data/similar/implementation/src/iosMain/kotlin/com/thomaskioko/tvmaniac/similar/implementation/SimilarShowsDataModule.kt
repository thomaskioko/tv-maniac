package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun similarDataModule(): Module = module {
    single<SimilarShowsRepository> {
        SimilarShowsRepositoryImpl(
            traktService = get(),
            similarShowCache = get(),
            tvShowCache = get(),
            dispatcher = Dispatchers.Default
        )
    }

    single<SimilarShowCache> {
        SimilarShowCacheImpl(
            database = get(),
            coroutineContext = Dispatchers.Default
        )
    }

}
