package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.similar.api.SimilarShowsDao
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import me.tatarka.inject.annotations.Provides

interface SimilarShowsComponent {

    @Provides
    fun provideSimilarShowsDao(bind: SimilarShowsDaoImpl): SimilarShowsDao = bind

    @Provides
    fun provideSimilarShowsRepository(bind: SimilarShowsRepositoryImpl): SimilarShowsRepository =
        bind
}
