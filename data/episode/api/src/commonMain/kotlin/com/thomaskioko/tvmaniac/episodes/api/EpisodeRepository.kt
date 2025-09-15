package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.episodes.api.model.LastWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgressContext
import kotlinx.coroutines.flow.Flow

public interface EpisodeRepository {

    /**
     * Observe next episodes for all shows in the watchlist using the shows_next_to_watch view.
     */
    public fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>>

    /**
     * Observe the next episode for a specific show using the shows_next_to_watch view.
     */
    public fun observeNextEpisodeForShow(showId: Long): Flow<NextEpisodeWithShow?>

    // ===== Watched Episode Functions =====

    /**
     * Mark an episode as watched. The SQL view automatically updates next episode calculations.
     */
    public suspend fun markEpisodeAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    )

    /**
     * Mark an episode as unwatched. The SQL view automatically updates next episode calculations.
     */
    public suspend fun markEpisodeAsUnwatched(showId: Long, episodeId: Long)

    /**
     * Observe all watched episodes for a specific show.
     */
    public fun observeWatchedEpisodes(showId: Long): Flow<List<Watched_episodes>>

    /**
     * Observe watch progress for a specific show (watched/total episodes ratio).
     */
    public fun observeWatchProgress(showId: Long): Flow<WatchProgress>

    /**
     * Get the last watched episode for a specific show.
     */
    public suspend fun getLastWatchedEpisode(showId: Long): Watched_episodes?

    /**
     * Check if a specific episode is watched.
     */
    public suspend fun isEpisodeWatched(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): Boolean

    /**
     * Clear all watch history for a specific show.
     */
    public suspend fun clearWatchHistoryForShow(showId: Long)


    /**
     * Get comprehensive watch progress context with out-of-order watching detection.
     */
    public suspend fun getWatchProgressContext(showId: Long): WatchProgressContext

    /**
     * Check if the user has unwatched episodes before their last watched episode.
     * Used to detect out-of-order watching patterns.
     */
    public suspend fun hasUnwatchedEarlierEpisodes(showId: Long): Boolean

    /**
     * Find the earliest unwatched episode for a show.
     * Used when user is watching out of order to show the next logical episode.
     */
    public suspend fun findEarliestUnwatchedEpisode(showId: Long): NextEpisodeWithShow?

    /**
     * Detect if the user is watching episodes out of chronological order.
     * Compares watch timestamps with episode air order.
     */
    public suspend fun isWatchingOutOfOrder(showId: Long): Boolean

    /**
     * Observe the last watched episode using the shows_last_watched view.
     * Provides absolute episode numbering context for progression tracking.
     */
    public fun observeLastWatchedEpisode(showId: Long): Flow<LastWatchedEpisode?>
}
