package com.thomaskioko.tvmaniac.data.recommendedshows.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsDao
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsRepository
import me.tatarka.inject.annotations.Provides

interface RecommendedShowsComponent {

  @ApplicationScope
  @Provides
  fun provideRecommendedShowsDao(bind: DefaultRecommendedShowsDao): RecommendedShowsDao = bind

  @ApplicationScope
  @Provides
  fun provideRecommendedShowsRepository(
    bind: DefaultRecommendedShowsRepository,
  ): RecommendedShowsRepository = bind
}
