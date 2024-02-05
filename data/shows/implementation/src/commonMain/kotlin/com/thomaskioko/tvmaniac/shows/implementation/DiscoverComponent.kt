package com.thomaskioko.tvmaniac.shows.implementation

import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface DiscoverComponent {

  @ApplicationScope @Provides fun provideTvShowsDao(bind: DefaultTvShowsDao): TvShowsDao = bind
}
