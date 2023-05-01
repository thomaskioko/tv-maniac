package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.similar.api.SimilarShowCache
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import me.tatarka.inject.annotations.Provides

interface SimilarShowsComponent {

    @Provides
    fun provideSimilarShowCache(bind: SimilarShowCacheImpl): SimilarShowCache = bind

    @Provides
    fun provideSimilarShowsRepository(bind: SimilarShowsRepositoryImpl): SimilarShowsRepository =
        bind
}
