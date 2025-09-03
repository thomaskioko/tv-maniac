package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow

/**
 * Simplified DAO interface using SQL view for next episode queries.
 * Based on Tivi's approach - eliminates cache management complexity.
 */
public interface NextEpisodeDao {

    /**
     * Observe the next episode for a specific show using the shows_next_to_watch view.
     */
    public fun observeNextEpisode(showId: Long): Flow<NextEpisodeWithShow?>

    /**
     * Observe next episodes for all shows in the watchlist using the shows_next_to_watch view.
     */
    public fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>>
}
