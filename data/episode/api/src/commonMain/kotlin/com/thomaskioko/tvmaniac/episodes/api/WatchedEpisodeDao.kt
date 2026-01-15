package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.db.GetEntriesByPendingAction
import com.thomaskioko.tvmaniac.db.GetWatchedEpisodes
import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeWatchParams
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import kotlinx.coroutines.flow.Flow

public interface WatchedEpisodeDao {

    public fun observeWatchedEpisodes(showTraktId: Long): Flow<List<GetWatchedEpisodes>>

    public fun observeSeasonWatchProgress(showTraktId: Long, seasonNumber: Long): Flow<SeasonWatchProgress>

    public fun observeShowWatchProgress(showTraktId: Long): Flow<ShowWatchProgress>

    public fun observeAllSeasonsWatchProgress(showTraktId: Long): Flow<List<SeasonWatchProgress>>

    public suspend fun markAsWatched(
        showTraktId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markAsUnwatched(
        showTraktId: Long,
        episodeId: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markSeasonAsWatched(
        showTraktId: Long,
        seasonNumber: Long,
        episodes: List<EpisodeWatchParams>,
        timestamp: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markSeasonAsUnwatched(
        showTraktId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markPreviousSeasonsAsWatched(
        showTraktId: Long,
        seasonNumber: Long,
        timestamp: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markSeasonAndPreviousAsWatched(
        showTraktId: Long,
        seasonNumber: Long,
        timestamp: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markPreviousEpisodesAsWatched(
        showTraktId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        timestamp: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markEpisodeAndPreviousAsWatched(
        showTraktId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        timestamp: Long,
        includeSpecials: Boolean,
    )

    public suspend fun getEpisodesForSeason(
        showTraktId: Long,
        seasonNumber: Long,
    ): List<EpisodeWatchParams>

    public suspend fun deleteAllForShow(showTraktId: Long)

    public suspend fun getUnwatchedEpisodeCountInPreviousSeasons(
        showTraktId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    ): Long

    public fun observeUnwatchedCountInPreviousSeasons(
        showTraktId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    ): Flow<Long>

    public suspend fun entriesByPendingAction(action: PendingAction): List<GetEntriesByPendingAction>

    public suspend fun updatePendingAction(id: Long, action: PendingAction)

    public suspend fun deleteById(id: Long)

    public suspend fun upsertFromTrakt(
        showTraktId: Long,
        episodeId: Long?,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long,
        traktId: Long,
        syncedAt: Long,
        pendingAction: String,
        includeSpecials: Boolean,
    )

    public suspend fun upsertBatchFromTrakt(
        showTraktId: Long,
        entries: List<WatchedEpisodeEntry>,
        includeSpecials: Boolean,
    )

    public suspend fun upsert(entry: Watched_episodes, includeSpecials: Boolean)
}
