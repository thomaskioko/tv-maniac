package com.thomaskioko.tvmaniac.search.implementation

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.search.api.SearchRepository
import me.tatarka.inject.annotations.Provides

interface SearchComponent {
  @ApplicationScope
  @Provides
  fun provideTopRatedShowsRepository(
    bind: DefaultSearchRepository,
  ): SearchRepository = bind
}
