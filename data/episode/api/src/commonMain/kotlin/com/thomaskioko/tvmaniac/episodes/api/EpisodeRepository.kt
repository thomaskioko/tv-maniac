package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgress
import kotlinx.coroutines.flow.Flow

public interface EpisodeRepository {

    /**
     * Observe next episodes for all shows in the watchlist.
     * Uses SQL view - automatically updates when episodes are marked as watched.
     */
    public fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>>

    /**
     * Observe the next episode for a specific show.
     * Uses SQL view - automatically updates when episodes are marked as watched.
     */
    public fun observeNextEpisodeForShow(showId: Long): Flow<NextEpisodeWithShow?>

    /**
     * Mark an episode as watched for a specific show.
     */
    public suspend fun markEpisodeAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    )

    /**
     * Mark an episode as unwatched for a specific show.
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
}
