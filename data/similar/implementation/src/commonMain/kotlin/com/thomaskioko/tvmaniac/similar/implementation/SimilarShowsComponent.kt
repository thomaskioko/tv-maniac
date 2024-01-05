package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.similar.api.SimilarShowsDao
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface SimilarShowsComponent {

    @ApplicationScope
    @Provides
    fun provideSimilarShowsDao(bind: SimilarShowsDaoImpl): SimilarShowsDao = bind

    @ApplicationScope
    @Provides
    fun provideSimilarShowsRepository(
        bind: DefaultSimilarShowsRepository
    ): SimilarShowsRepository = bind
}
