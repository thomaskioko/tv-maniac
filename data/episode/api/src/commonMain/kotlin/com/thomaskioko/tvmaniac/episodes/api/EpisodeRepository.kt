package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.episodes.api.model.ContinueTrackingResult
import com.thomaskioko.tvmaniac.episodes.api.model.LastWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.UnwatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgressContext
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

public interface EpisodeRepository {

    /**
     * Observe next episodes for all shows in the watchlist using the shows_next_to_watch view.
     */
    public fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>>

    /**
     * Mark an episode as watched. The SQL view automatically updates next episode calculations.
     * Automatically adds the show to the library if not already there.
     * @param watchedAt Optional timestamp for when the episode was watched. Defaults to current time.
     */
    public suspend fun markEpisodeAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Instant? = null,
    )

    /**
     * Mark an episode as watched along with all previous unwatched episodes.
     * Automatically adds the show to the library if not already there.
     * @param watchedAt Optional timestamp for when the episodes were watched. Defaults to current time.
     */
    public suspend fun markEpisodeAndPreviousEpisodesWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Instant? = null,
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
     * Check if a specific episode is watched.
     */
    public suspend fun isEpisodeWatched(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): Boolean

    /**
     * Clear all cached watch history for a specific show.
     */
    public suspend fun clearCachedWatchHistoryForShow(showId: Long)

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
     * Observe the last watched episode using the shows_last_watched view.
     * Provides absolute episode numbering context for progression tracking.
     */
    public fun observeLastWatchedEpisode(showId: Long): Flow<LastWatchedEpisode?>

    /**
     * Observe watch progress for a specific season.
     */
    public fun observeSeasonWatchProgress(showId: Long, seasonNumber: Long): Flow<SeasonWatchProgress>

    /**
     * Observe watch progress for an entire show (across all seasons).
     */
    public fun observeShowWatchProgress(showId: Long): Flow<ShowWatchProgress>

    /**
     * Observe watch progress for all seasons of a show.
     * Returns a list of SeasonWatchProgress, one per season.
     */
    public fun observeAllSeasonsWatchProgress(showId: Long): Flow<List<SeasonWatchProgress>>

    /**
     * Mark all episodes in a season as watched.
     * Automatically adds the show to the library if not already there.
     * @param watchedAt Optional timestamp for when the episodes were watched. Defaults to current time.
     */
    public suspend fun markSeasonWatched(showId: Long, seasonNumber: Long, watchedAt: Instant? = null)

    /**
     * Mark all episodes in a season as watched along with all previous seasons.
     * Automatically adds the show to the library if not already there.
     * @param watchedAt Optional timestamp for when the episodes were watched. Defaults to current time.
     */
    public suspend fun markSeasonAndPreviousSeasonsWatched(
        showId: Long,
        seasonNumber: Long,
        watchedAt: Instant? = null,
    )

    /**
     * Mark all episodes in a season as unwatched.
     */
    public suspend fun markSeasonUnwatched(showId: Long, seasonNumber: Long)

    /**
     * Get unwatched episodes that come before a specific episode.
     * Used to prompt the user when marking episodes out of order.
     */
    public suspend fun getPreviousUnwatchedEpisodes(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): List<UnwatchedEpisode>

    /**
     * Get count of unwatched episodes in seasons before the specified season number,
     * ensuring that previous seasons' episode data is fetched first.
     * Use this when the previous seasons may not have been loaded yet.
     */
    public suspend fun getUnwatchedCountAfterFetchingPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Long

    /**
     * Observe count of unwatched episodes before a specific episode.
     * Used for reactive UI to determine if confirmation dialog should be shown.
     */
    public fun observeUnwatchedCountBefore(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): Flow<Int>

    /**
     * Observe count of unwatched episodes in seasons before the specified season number.
     * Used for reactive UI to determine if previous seasons dialog should be shown.
     */
    public fun observeUnwatchedCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Flow<Long>

    /**
     * Observe episodes for continue tracking feature.
     * Automatically selects the appropriate season based on watch progress:
     * - Starts from the last watched season, or Season 1 if no history
     * - Auto-progresses to next season when all episodes in current season are watched
     * - Returns null when no unwatched episodes remain across all seasons
     */
    public fun observeContinueTrackingEpisodes(showId: Long): Flow<ContinueTrackingResult?>
}
