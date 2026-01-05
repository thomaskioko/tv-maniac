package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeWatchParams
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import kotlinx.coroutines.flow.Flow

public interface WatchedEpisodeDao {

    public fun observeWatchedEpisodes(showId: Long): Flow<List<Watched_episodes>>

    public fun observeSeasonWatchProgress(showId: Long, seasonNumber: Long): Flow<SeasonWatchProgress>

    public fun observeShowWatchProgress(showId: Long): Flow<ShowWatchProgress>

    public fun observeAllSeasonsWatchProgress(showId: Long): Flow<List<SeasonWatchProgress>>

    public suspend fun markAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markAsUnwatched(
        showId: Long,
        episodeId: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markSeasonAsWatched(
        showId: Long,
        seasonNumber: Long,
        episodes: List<EpisodeWatchParams>,
        timestamp: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markSeasonAsUnwatched(
        showId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markPreviousSeasonsAsWatched(
        showId: Long,
        seasonNumber: Long,
        timestamp: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markSeasonAndPreviousAsWatched(
        showId: Long,
        seasonNumber: Long,
        timestamp: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markPreviousEpisodesAsWatched(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        timestamp: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markEpisodeAndPreviousAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        timestamp: Long,
        includeSpecials: Boolean,
    )

    public suspend fun getEpisodesForSeason(
        showId: Long,
        seasonNumber: Long,
    ): List<EpisodeWatchParams>

    public suspend fun deleteAllForShow(showId: Long)

    public suspend fun getUnwatchedEpisodeCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    ): Long

    public fun observeUnwatchedCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    ): Flow<Long>

    public suspend fun entriesByPendingAction(action: PendingAction): List<Watched_episodes>

    public suspend fun updatePendingAction(id: Long, action: PendingAction)

    public suspend fun deleteById(id: Long)

    public suspend fun upsertFromTrakt(
        showId: Long,
        episodeId: Long?,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long,
        traktId: Long,
        syncedAt: Long,
        pendingAction: String,
        includeSpecials: Boolean,
    )

    public suspend fun upsert(entry: Watched_episodes, includeSpecials: Boolean)
}
