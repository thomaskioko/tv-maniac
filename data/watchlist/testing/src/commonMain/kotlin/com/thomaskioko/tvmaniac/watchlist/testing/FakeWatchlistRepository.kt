package com.thomaskioko.tvmaniac.watchlist.testing

import com.thomaskioko.tvmaniac.core.db.SelectWatchlist
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeWatchlistRepository : WatchlistRepository {

    private var watchlistResult = flowOf<Either<Failure, List<SelectWatchlist>>>()

    suspend fun setFollowedResult(result: Either<Failure, List<SelectWatchlist>>) {
        watchlistResult = flow { emit(result) }
    }

    override fun observeWatchList(): Flow<Either<Failure, List<SelectWatchlist>>> =
        watchlistResult

    override fun getWatchlist(): List<SelectWatchlist> = com.thomaskioko.tvmaniac.watchlist.testing.watchlistResult

    override suspend fun updateFollowedShow(traktId: Long, addToWatchList: Boolean) {}

    override suspend fun syncWatchlist() {}
}
