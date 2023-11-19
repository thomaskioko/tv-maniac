package com.thomaskioko.tvmaniac.watchlist.testing

import com.thomaskioko.tvmaniac.core.db.WatchedShow
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeWatchlistRepository : WatchlistRepository {

    private var watchlist: Channel<List<WatchedShow>> = Channel(Channel.UNLIMITED)
    private var watchlistResult: Channel<Either<Failure, List<WatchedShow>>> =
        Channel(Channel.UNLIMITED)

    suspend fun setFollowedResult(result: List<WatchedShow>) {
        watchlist.send(result)
    }

    override fun observeWatchList(): Flow<Either<Failure, List<WatchedShow>>> =
        watchlistResult.receiveAsFlow()

    override suspend fun getWatchlist(): List<WatchedShow> = watchlist.receive()

    override suspend fun updateWatchlist(traktId: Long, addToWatchList: Boolean) {
    }

    override suspend fun syncWatchlist() {}
}
