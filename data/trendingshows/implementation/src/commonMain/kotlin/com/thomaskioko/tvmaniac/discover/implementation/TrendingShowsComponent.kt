package com.thomaskioko.tvmaniac.discover.implementation

import com.thomaskioko.tvmaniac.discover.api.TrendingShowsDao
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface TrendingShowsComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideTrendingShowsDao(bind: DefaultTrendingShowsDao): TrendingShowsDao = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideTrendingShowsRepository(
    bind: DefaultTrendingShowsRepository,
  ): TrendingShowsRepository = bind
}
