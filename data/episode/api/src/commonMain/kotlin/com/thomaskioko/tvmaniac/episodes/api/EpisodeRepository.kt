package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.episodes.api.model.ContinueTrackingResult
import com.thomaskioko.tvmaniac.episodes.api.model.LastWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
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
        showTraktId: Long,
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
        showTraktId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Instant? = null,
    )

    /**
     * Mark an episode as unwatched. The SQL view automatically updates next episode calculations.
     */
    public suspend fun markEpisodeAsUnwatched(showTraktId: Long, episodeId: Long)

    /**
     * Observe the last watched episode using the shows_last_watched view.
     * Provides absolute episode numbering context for progression tracking.
     */
    public fun observeLastWatchedEpisode(showTraktId: Long): Flow<LastWatchedEpisode?>

    /**
     * Observe watch progress for a specific season.
     */
    public fun observeSeasonWatchProgress(showTraktId: Long, seasonNumber: Long): Flow<SeasonWatchProgress>

    /**
     * Observe watch progress for an entire show (across all seasons).
     */
    public fun observeShowWatchProgress(showTraktId: Long): Flow<ShowWatchProgress>

    /**
     * Observe watch progress for all seasons of a show.
     * Returns a list of SeasonWatchProgress, one per season.
     */
    public fun observeAllSeasonsWatchProgress(showTraktId: Long): Flow<List<SeasonWatchProgress>>

    /**
     * Mark all episodes in a season as watched.
     * Automatically adds the show to the library if not already there.
     * @param watchedAt Optional timestamp for when the episodes were watched. Defaults to current time.
     */
    public suspend fun markSeasonWatched(showTraktId: Long, seasonNumber: Long, watchedAt: Instant? = null)

    /**
     * Mark all episodes in a season as watched along with all previous seasons.
     * Automatically adds the show to the library if not already there.
     * @param watchedAt Optional timestamp for when the episodes were watched. Defaults to current time.
     */
    public suspend fun markSeasonAndPreviousSeasonsWatched(
        showTraktId: Long,
        seasonNumber: Long,
        watchedAt: Instant? = null,
    )

    /**
     * Mark all episodes in a season as unwatched.
     */
    public suspend fun markSeasonUnwatched(showTraktId: Long, seasonNumber: Long)

    /**
     * Get count of unwatched episodes in seasons before the specified season number,
     * ensuring that previous seasons' episode data is fetched first.
     * Use this when the previous seasons may not have been loaded yet.
     */
    public suspend fun getUnwatchedCountAfterFetchingPreviousSeasons(
        showTraktId: Long,
        seasonNumber: Long,
    ): Long

    /**
     * Observe count of unwatched episodes in seasons before the specified season number.
     * Used for reactive UI to determine if previous seasons dialog should be shown.
     */
    public fun observeUnwatchedCountInPreviousSeasons(
        showTraktId: Long,
        seasonNumber: Long,
    ): Flow<Long>

    /**
     * Observe episodes for continue tracking feature.
     * Automatically selects the appropriate season based on watch progress:
     * - Starts from the last watched season, or Season 1 if no history
     * - Auto-progresses to next season when all episodes in current season are watched
     * - Returns null when no unwatched episodes remain across all seasons
     */
    public fun observeContinueTrackingEpisodes(showTraktId: Long): Flow<ContinueTrackingResult?>
}
