package com.thomaskioko.tvmaniac.watchlist.testing

import com.thomaskioko.tvmaniac.core.db.LibraryShows
import com.thomaskioko.tvmaniac.core.db.library.SearchShows
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.shows.api.LibraryRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeLibraryRepository : LibraryRepository {

  private var watchlistResult: Channel<Either<Failure, List<LibraryShows>>> =
    Channel(Channel.UNLIMITED)
  private var searchlistResult: Channel<Either<Failure, List<SearchShows>>> =
    Channel(Channel.UNLIMITED)

  suspend fun setSearchResult(result: Either<Failure, List<SearchShows>>) {
    searchlistResult.send(result)
  }

  suspend fun setObserveResult(result: Either<Failure, List<LibraryShows>>) {
    watchlistResult.send(result)
  }

  override fun observeLibrary(): Flow<Either<Failure, List<LibraryShows>>> =
    watchlistResult.receiveAsFlow()

  override fun searchWatchlistByQuery(query: String): Flow<Either<Failure, List<SearchShows>>> =
    searchlistResult.receiveAsFlow()

  override suspend fun updateLibrary(traktId: Long, addToLibrary: Boolean) {}
}
