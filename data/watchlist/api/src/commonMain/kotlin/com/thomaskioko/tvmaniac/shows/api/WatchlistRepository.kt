package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.FollowedShows
import com.thomaskioko.tvmaniac.db.SearchFollowedShows
import com.thomaskioko.tvmaniac.shows.api.model.WatchlistSortOption
import kotlinx.coroutines.flow.Flow

public interface WatchlistRepository {

    public fun observeWatchlist(): Flow<List<FollowedShows>>

    public fun searchWatchlistByQuery(query: String): Flow<List<SearchFollowedShows>>

    public fun observeListStyle(): Flow<Boolean>

    public suspend fun saveListStyle(isGridMode: Boolean)

    public fun observeSortOption(): Flow<WatchlistSortOption>

    public suspend fun saveSortOption(sortOption: WatchlistSortOption)
}
