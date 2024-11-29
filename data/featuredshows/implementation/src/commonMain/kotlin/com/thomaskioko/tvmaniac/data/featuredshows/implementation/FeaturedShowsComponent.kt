package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsDao
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface FeaturedShowsComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideFeaturedShowsDao(bind: DefaultFeaturedShowsDao): FeaturedShowsDao = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideFeaturedShowsRepository(
    bind: DefaultFeaturedShowsRepository,
  ): FeaturedShowsRepository = bind
}
