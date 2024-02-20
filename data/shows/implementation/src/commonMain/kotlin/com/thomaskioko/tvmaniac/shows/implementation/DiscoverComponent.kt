package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import me.tatarka.inject.annotations.Provides

interface DiscoverComponent {

  @ApplicationScope @Provides fun provideTvShowsDao(bind: DefaultTvShowsDao): TvShowsDao = bind
}
