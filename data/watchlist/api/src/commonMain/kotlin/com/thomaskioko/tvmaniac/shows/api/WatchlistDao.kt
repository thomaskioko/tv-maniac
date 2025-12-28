package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Show_metadata
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.Watchlists
import kotlinx.coroutines.flow.Flow

public interface WatchlistDao {

    public fun upsertWithPendingUpload(id: Long)

    public fun getShowsInWatchlist(): List<Watchlists>

    public fun updateSyncState(id: Id<TmdbId>)

    public fun observeShowsInWatchlist(): Flow<List<Watchlists>>

    public fun observeShowsInWatchlistFiltered(includeSpecials: Boolean): Flow<List<Watchlists>>

    public fun observeWatchlistByQuery(query: String): Flow<List<SearchWatchlist>>

    public fun observeUnSyncedWatchlist(): Flow<List<Id<TmdbId>>>

    public fun delete(id: Long)

    public fun markForDeletion(id: Long)

    public suspend fun isShowInLibrary(showId: Long): Boolean

    public fun observeIsShowInLibrary(showId: Long): Flow<Boolean>
}
