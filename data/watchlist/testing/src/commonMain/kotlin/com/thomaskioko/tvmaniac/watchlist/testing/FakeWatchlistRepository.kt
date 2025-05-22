package com.thomaskioko.tvmaniac.watchlist.testing

import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow

class FakeWatchlistRepository : WatchlistRepository {

  private var watchlistResult: Channel<Either<Failure, List<Watchlists>>> =
    Channel(Channel.UNLIMITED)
  private var searchlistResult: Channel<Either<Failure, List<SearchWatchlist>>> =
    Channel(Channel.UNLIMITED)

  suspend fun setSearchResult(result: Either<Failure, List<SearchWatchlist>>) {
    searchlistResult.send(result)
  }

  suspend fun setObserveResult(result: Either<Failure, List<Watchlists>>) {
    watchlistResult.send(result)
  }

  override fun observeWatchlist(): Flow<Either<Failure, List<Watchlists>>> =
    watchlistResult.receiveAsFlow()

  override fun searchWatchlistByQuery(query: String): Flow<Either<Failure, List<SearchWatchlist>>> =
    searchlistResult.receiveAsFlow()

  override suspend fun updateLibrary(id: Long, addToLibrary: Boolean) {}

  override fun observeUnSyncedItems(): Flow<Unit> = flowOf(Unit)
}
