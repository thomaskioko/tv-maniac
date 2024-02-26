package com.thomaskioko.tvmaniac.data.featuredshows.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsDao
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import me.tatarka.inject.annotations.Provides

interface FeaturedShowsComponent {

  @ApplicationScope
  @Provides
  fun provideFeaturedShowsDao(bind: DefaultFeaturedShowsDao): FeaturedShowsDao = bind

  @ApplicationScope
  @Provides
  fun provideFeaturedShowsRepository(
    bind: DefaultFeaturedShowsRepository,
  ): FeaturedShowsRepository = bind
}
