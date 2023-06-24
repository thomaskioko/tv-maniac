package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.SelectWatchlist
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {

    fun observeWatchList(): Flow<Either<Failure, List<SelectWatchlist>>>

    fun getWatchlist(): List<SelectWatchlist>

    suspend fun updateWatchlist(traktId: Long, addToWatchList: Boolean)

    suspend fun syncWatchlist()
}
