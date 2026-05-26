package com.thomaskioko.tvmaniac.startwatching.api

import kotlinx.coroutines.flow.Flow

public interface StartWatchingRepository {

    /**
     * Observes followed shows that are released but not yet started, excluding any show already
     * present in continue-watching.
     */
    public fun observeStartWatching(): Flow<List<StartWatchingShow>>

    /**
     * Re-syncs the Trakt watchlist that backs the Start Watching lane, writing show metadata and
     * posters into the shared followed-shows tables. Pull-only: preserves pending follow/unfollow
     * actions and never uploads local changes.
     *
     * @param forceRefresh bypasses the cache freshness window when true.
     */
    public suspend fun syncWatchlist(forceRefresh: Boolean = false)
}
