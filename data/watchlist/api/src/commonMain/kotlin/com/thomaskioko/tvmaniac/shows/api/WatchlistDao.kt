package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.WatchedShow
import com.thomaskioko.tvmaniac.core.db.Watchlist
import kotlinx.coroutines.flow.Flow

interface WatchlistDao {

    fun upsert(watchlist: Watchlist)

    fun upsert(watchedShowList: List<Watchlist>)

    fun getWatchedShows(): List<WatchedShow>

    fun getUnSyncedShows(): List<Watchlist>

    fun observeWatchedShows(): Flow<List<WatchedShow>>

    fun updateShowSyncState(traktId: Long)

    fun removeShow(traktId: Long)
}
