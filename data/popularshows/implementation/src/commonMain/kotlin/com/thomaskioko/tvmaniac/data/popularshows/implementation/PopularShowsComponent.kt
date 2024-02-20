package com.thomaskioko.tvmaniac.data.popularshows.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsDao
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import me.tatarka.inject.annotations.Provides

interface PopularShowsComponent {

  @ApplicationScope
  @Provides
  fun provideTopRatedShowsDao(bind: DefaultPopularShowsDao): PopularShowsDao = bind

  @ApplicationScope
  @Provides
  fun provideTopRatedShowsRepository(bind: DefaultPopularShowsRepository): PopularShowsRepository =
    bind
}
