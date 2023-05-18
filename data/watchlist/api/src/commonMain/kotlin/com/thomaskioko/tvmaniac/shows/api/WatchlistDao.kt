package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.SelectWatchlist
import com.thomaskioko.tvmaniac.core.db.Watchlist
import kotlinx.coroutines.flow.Flow

interface WatchlistDao {

    fun insert(followedShow: Watchlist)

    fun insert(followedShows: List<Watchlist>)

    fun getWatchlist(): List<SelectWatchlist>

    fun getUnSyncedShows(): List<Watchlist>

    fun observeWatchlist(): Flow<List<SelectWatchlist>>

    fun updateShowSyncState(traktId: Long)

    fun removeShow(traktId: Long)
}
