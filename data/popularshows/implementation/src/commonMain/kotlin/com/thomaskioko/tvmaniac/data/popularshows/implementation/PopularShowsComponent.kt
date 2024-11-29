package com.thomaskioko.tvmaniac.data.popularshows.implementation

import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsDao
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface PopularShowsComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideTopRatedShowsDao(bind: DefaultPopularShowsDao): PopularShowsDao = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideTopRatedShowsRepository(bind: DefaultPopularShowsRepository): PopularShowsRepository =
    bind
}
