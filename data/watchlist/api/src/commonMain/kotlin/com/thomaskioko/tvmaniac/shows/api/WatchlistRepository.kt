package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.WatchedShow
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {

    fun observeWatchList(): Flow<Either<Failure, List<WatchedShow>>>

    suspend fun getWatchlist(): List<WatchedShow>

    suspend fun updateWatchlist(traktId: Long, addToWatchList: Boolean)

    suspend fun syncWatchlist()
}
