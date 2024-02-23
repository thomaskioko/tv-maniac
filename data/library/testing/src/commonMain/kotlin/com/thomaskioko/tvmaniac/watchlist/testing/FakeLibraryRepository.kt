package com.thomaskioko.tvmaniac.watchlist.testing

import com.thomaskioko.tvmaniac.core.db.LibraryShows
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.shows.api.LibraryRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeLibraryRepository : LibraryRepository {

  private var watchlist: Channel<List<LibraryShows>> = Channel(Channel.UNLIMITED)
  private var watchlistResult: Channel<Either<Failure, List<LibraryShows>>> =
    Channel(Channel.UNLIMITED)

  suspend fun setFollowedResult(result: List<LibraryShows>) {
    watchlist.send(result)
  }

  suspend fun setObserveResult(result: Either<Failure, List<LibraryShows>>) {
    watchlistResult.send(result)
  }

  override fun observeLibrary(): Flow<Either<Failure, List<LibraryShows>>> =
    watchlistResult.receiveAsFlow()

  override suspend fun getLibraryShows(): List<LibraryShows> = watchlist.receive()

  override suspend fun updateLibrary(traktId: Long, addToLibrary: Boolean) {}
}
