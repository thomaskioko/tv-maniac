package com.thomaskioko.tvmaniac.toprated.data.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsDao
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import me.tatarka.inject.annotations.Provides

interface TopRatedShowsComponent {

  @ApplicationScope
  @Provides
  fun provideTopRatedShowsDao(bind: DefaultTopRatedShowsDao): TopRatedShowsDao = bind

  @ApplicationScope
  @Provides
  fun provideTopRatedShowsRepository(
    bind: DefaultTopRatedShowsRepository,
  ): TopRatedShowsRepository = bind
}
