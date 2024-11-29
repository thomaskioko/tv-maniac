package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface DiscoverComponent {

  @SingleIn(AppScope::class)
  @Provides fun provideTvShowsDao(bind: DefaultTvShowsDao): TvShowsDao = bind
}
