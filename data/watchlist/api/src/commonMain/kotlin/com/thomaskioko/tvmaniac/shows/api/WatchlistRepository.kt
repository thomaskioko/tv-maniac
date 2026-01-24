package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.FollowedShows
import com.thomaskioko.tvmaniac.db.SearchFollowedShows
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

public interface WatchlistRepository {

    public fun observeWatchlist(): Flow<List<FollowedShows>>

    public fun searchWatchlistByQuery(query: String): Flow<List<SearchFollowedShows>>

    public fun observeListStyle(): Flow<Boolean>

    public suspend fun saveListStyle(isGridMode: Boolean)

    public suspend fun syncWatchlist(forceRefresh: Boolean = false)

    public suspend fun needsSync(expiry: Duration = 3.hours): Boolean
}
