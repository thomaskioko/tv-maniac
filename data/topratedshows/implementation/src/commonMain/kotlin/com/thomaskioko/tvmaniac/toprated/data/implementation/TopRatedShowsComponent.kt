package com.thomaskioko.tvmaniac.toprated.data.implementation

import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsDao
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
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
