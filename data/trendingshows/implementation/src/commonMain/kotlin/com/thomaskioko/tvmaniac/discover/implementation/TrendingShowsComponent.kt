package com.thomaskioko.tvmaniac.discover.implementation

import com.thomaskioko.tvmaniac.discover.api.TrendingShowsDao
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface TrendingShowsComponent {

  @ApplicationScope
  @Provides
  fun provideTrendingShowsDao(bind: DefaultTrendingShowsDao): TrendingShowsDao = bind

  @ApplicationScope
  @Provides
  fun provideTrendingShowsRepository(
    bind: DefaultTrendingShowsRepository,
  ): TrendingShowsRepository = bind
}
