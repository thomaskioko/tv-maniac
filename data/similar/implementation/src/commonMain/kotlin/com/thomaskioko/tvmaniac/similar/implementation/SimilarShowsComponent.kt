package com.thomaskioko.tvmaniac.similar.implementation

import com.thomaskioko.tvmaniac.similar.api.SimilarShowsDao
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface SimilarShowsComponent {

  @Provides
  @SingleIn(AppScope::class)
  fun provideSimilarShowsDao(bind: SimilarShowsDaoImpl): SimilarShowsDao = bind

  @Provides
  @SingleIn(AppScope::class)
  fun provideSimilarShowsRepository(bind: DefaultSimilarShowsRepository): SimilarShowsRepository =
    bind
}
