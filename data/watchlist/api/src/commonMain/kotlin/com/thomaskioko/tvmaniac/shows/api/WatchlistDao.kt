package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.db.FollowedShows
import com.thomaskioko.tvmaniac.db.SearchFollowedShows
import kotlinx.coroutines.flow.Flow

public interface WatchlistDao {

    public fun observeShowsInWatchlist(): Flow<List<FollowedShows>>

    public fun observeWatchlistByQuery(query: String): Flow<List<SearchFollowedShows>>

    public fun observeIsShowInLibrary(showId: Long): Flow<Boolean>
}
