package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Show_metadata
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.Watchlists
import kotlinx.coroutines.flow.Flow

interface WatchlistDao {

    fun upsert(id: Long)

    fun getShowsInWatchlist(): List<Watchlists>

    fun updateSyncState(id: Id<TmdbId>)

    fun observeShowsInWatchlist(): Flow<List<Watchlists>>

    fun observeWatchlistByQuery(query: String): Flow<List<SearchWatchlist>>

    fun observeUnSyncedWatchlist(): Flow<List<Id<TmdbId>>>

    fun delete(id: Long)

    fun upsert(entity: Show_metadata)

    suspend fun isShowInLibrary(showId: Long): Boolean

    fun observeIsShowInLibrary(showId: Long): Flow<Boolean>
}
