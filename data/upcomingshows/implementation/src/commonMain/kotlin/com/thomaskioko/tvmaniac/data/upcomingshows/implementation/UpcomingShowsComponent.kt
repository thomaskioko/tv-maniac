package com.thomaskioko.tvmaniac.data.upcomingshows.implementation

import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsDao
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface UpcomingShowsComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideUpcomingShowsDao(bind: DefaultUpcomingShowsDao): UpcomingShowsDao = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideUpcomingShowsRepository(
    bind: DefaultUpcomingShowsRepository,
  ): UpcomingShowsRepository = bind
}
