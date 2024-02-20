package com.thomaskioko.tvmaniac.data.upcomingshows.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsDao
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsRepository
import me.tatarka.inject.annotations.Provides

interface UpcomingShowsComponent {

  @ApplicationScope
  @Provides
  fun provideUpcomingShowsDao(bind: DefaultUpcomingShowsDao): UpcomingShowsDao = bind

  @ApplicationScope
  @Provides
  fun provideUpcomingShowsRepository(
    bind: DefaultUpcomingShowsRepository,
  ): UpcomingShowsRepository = bind
}
