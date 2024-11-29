package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.shows.api.LibraryDao
import com.thomaskioko.tvmaniac.shows.api.LibraryRepository
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface LibraryComponent {

  @SingleIn(AppScope::class)
  @Provides fun provideWatchlistDao(bind: DefaultLibraryDao): LibraryDao = bind

  @SingleIn(AppScope::class)
  @Provides
  fun provideWatchlist(bind: DefaultLibraryRepository): LibraryRepository = bind
}
