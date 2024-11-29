package com.thomaskioko.tvmaniac.search.implementation

import com.thomaskioko.tvmaniac.search.api.SearchRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface SearchComponent {

  @SingleIn(AppScope::class)
  @Provides
  fun provideTopRatedShowsRepository(
    bind: DefaultSearchRepository,
  ): SearchRepository = bind
}
