package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.Watchlists
import com.thomaskioko.tvmaniac.core.db.SearchWatchlist
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {

  fun observeWatchlist(): Flow<Either<Failure, List<Watchlists>>>

  fun searchWatchlistByQuery(query: String): Flow<Either<Failure, List<SearchWatchlist>>>

  fun observeUnSyncedItems(): Flow<Unit>

  suspend fun updateLibrary(id: Long, addToLibrary: Boolean)

}
