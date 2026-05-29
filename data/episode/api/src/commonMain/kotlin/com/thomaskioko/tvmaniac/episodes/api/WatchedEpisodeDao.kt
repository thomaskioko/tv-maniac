package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.db.GetEntriesByPendingAction
import com.thomaskioko.tvmaniac.db.GetWatchedEpisodes
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeWatchParams
import com.thomaskioko.tvmaniac.episodes.api.model.RecentlyWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import kotlinx.coroutines.flow.Flow

public interface WatchedEpisodeDao {

    public fun observeWatchedEpisodes(showTraktId: Long): Flow<List<GetWatchedEpisodes>>

    public fun observeRecentlyWatched(limit: Long): Flow<List<RecentlyWatchedEpisode>>

    public fun observeSeasonWatchProgress(showTraktId: Long, seasonNumber: Long): Flow<SeasonWatchProgress>

    public fun observeShowWatchProgress(showTraktId: Long): Flow<ShowWatchProgress>

    public fun observeAllSeasonsWatchProgress(showTraktId: Long): Flow<List<SeasonWatchProgress>>

    public suspend fun markAsWatched(
        showTraktId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
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
        includeSpecials: Boolean,
    )

    public suspend fun markSeasonAsUnwatched(
        showTraktId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markSeasonAndPreviousAsWatched(
        showTraktId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markEpisodeAndPreviousAsWatched(
        showTraktId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        includeSpecials: Boolean,
    )

    public suspend fun getEpisodesForSeason(
        showTraktId: Long,
        seasonNumber: Long,
    ): List<EpisodeWatchParams>

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

    /**
     * Soft-delete the row after to delete has been pushed to Trakt. Sets pending_action to
     * SYNCED_DELETE so a subsequent Trakt pull cannot resurrect the row before propagation
     * completes. The synced_at column doubles as the soft-delete timestamp, used by
     * [purgeSyncedDeletesOlderThan] for GC.
     */
    public suspend fun markAsSyncedDelete(id: Long)

    /**
     * Garbage collect SYNCED_DELETE rows whose synced_at is older than [thresholdMillis].
     */
    public suspend fun purgeSyncedDeletesOlderThan(thresholdMillis: Long)

    public suspend fun upsertBatchFromTrakt(
        showTraktId: Long,
        entries: List<WatchedEpisodeEntry>,
        includeSpecials: Boolean,
    )
}
