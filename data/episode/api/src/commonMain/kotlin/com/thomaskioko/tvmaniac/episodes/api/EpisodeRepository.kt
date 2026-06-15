package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.episodes.api.model.RecentlyWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.UpcomingEpisode
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

public interface EpisodeRepository {

    public fun observeEpisodeById(episodeId: Long): Flow<EpisodeById?>

    /** Observes the most-recently watched episodes across all shows, newest first. */
    public fun observeRecentlyWatched(limit: Long): Flow<List<RecentlyWatchedEpisode>>

    public suspend fun markEpisodeAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    )

    /**
     * Mark an episode as watched along with all previous unwatched episodes.
     * Automatically adds the show to the library if not already there.
     */
    public suspend fun markEpisodeAndPreviousEpisodesWatched(
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
     */
    public suspend fun markSeasonWatched(showId: Long, seasonNumber: Long)

    /**
     * Mark all episodes in a season as watched along with all previous seasons.
     * Automatically adds the show to the library if not already there.
     */
    public suspend fun markSeasonAndPreviousSeasonsWatched(
        showId: Long,
        seasonNumber: Long,
    )

    /**
     * Mark all episodes in a season as unwatched.
     */
    public suspend fun markSeasonUnwatched(showId: Long, seasonNumber: Long)

    /**
     * Observe count of unwatched episodes in seasons before the specified season number.
     * Used for reactive UI to determine if previous seasons dialog should be shown.
     */
    public fun observeUnwatchedCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Flow<Long>

    /**
     * Get upcoming episodes from followed shows within the specified time window.
     * Only returns episodes that have first_aired set and are not yet watched.
     * Time filtering (from current time to now + limit) is handled internally.
     * @param limit Duration from now to search for upcoming episodes
     * @return List of upcoming episodes ordered by air time
     */
    public suspend fun getUpcomingEpisodesFromFollowedShows(limit: Duration): List<UpcomingEpisode>

    public suspend fun syncUpcomingEpisodes(
        startDate: String,
        days: Int,
        forceRefresh: Boolean = false,
    )
}
