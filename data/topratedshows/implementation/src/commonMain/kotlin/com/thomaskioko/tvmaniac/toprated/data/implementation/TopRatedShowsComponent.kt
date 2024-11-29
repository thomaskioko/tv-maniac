package com.thomaskioko.tvmaniac.toprated.data.implementation

import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsDao
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface TopRatedShowsComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideTopRatedShowsDao(bind: DefaultTopRatedShowsDao): TopRatedShowsDao = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideTopRatedShowsRepository(
    bind: DefaultTopRatedShowsRepository,
  ): TopRatedShowsRepository = bind
}
