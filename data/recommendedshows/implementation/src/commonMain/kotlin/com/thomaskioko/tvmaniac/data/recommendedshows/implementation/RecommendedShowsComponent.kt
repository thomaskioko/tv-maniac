package com.thomaskioko.tvmaniac.data.recommendedshows.implementation

import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsDao
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface RecommendedShowsComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideRecommendedShowsDao(bind: DefaultRecommendedShowsDao): RecommendedShowsDao = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideRecommendedShowsRepository(
    bind: DefaultRecommendedShowsRepository,
  ): RecommendedShowsRepository = bind
}
