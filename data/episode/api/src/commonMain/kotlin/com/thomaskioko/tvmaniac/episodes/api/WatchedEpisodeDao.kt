package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.db.GetEntriesByPendingAction
import com.thomaskioko.tvmaniac.db.GetWatchedEpisodes
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeWatchParams
import com.thomaskioko.tvmaniac.episodes.api.model.MostWatchedShow
import com.thomaskioko.tvmaniac.episodes.api.model.RecentlyWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.WatchedEpisodeRuntime
import com.thomaskioko.tvmaniac.episodes.api.model.WatchedShowComposition
import kotlinx.coroutines.flow.Flow

public interface WatchedEpisodeDao {

    public fun observeWatchedEpisodes(showId: Long): Flow<List<GetWatchedEpisodes>>

    public fun observeRecentlyWatched(limit: Long): Flow<List<RecentlyWatchedEpisode>>

    public fun observeSeasonWatchProgress(showId: Long, seasonNumber: Long): Flow<SeasonWatchProgress>

    public fun observeShowWatchProgress(showId: Long): Flow<ShowWatchProgress>

    public fun observeAllSeasonsWatchProgress(showId: Long): Flow<List<SeasonWatchProgress>>

    public fun observeWatchedAtWithRuntime(): Flow<List<WatchedEpisodeRuntime>>

    public fun observeWatchedShowComposition(): Flow<List<WatchedShowComposition>>

    public fun observeMostWatchedShows(limit: Long): Flow<List<MostWatchedShow>>

    public suspend fun updateShowMetadata(
        tmdbId: Long,
        runtime: Long? = null,
        year: String? = null,
        genres: List<String>? = null,
    )

    public suspend fun countWatchedShowsMissingMetadata(): Long

    public suspend fun getWatchedShowsMissingGenres(): List<Long>

    public suspend fun markAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        includeSpecials: Boolean,
        watchedAt: Long? = null,
        useReleaseDate: Boolean = false,
    )

    public suspend fun updateWatchedDate(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        includeSpecials: Boolean,
        watchedAt: Long? = null,
        useReleaseDate: Boolean = false,
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
        includeSpecials: Boolean,
        watchedAt: Long? = null,
    )

    public suspend fun markSeasonAsUnwatched(
        showId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
    )

    public suspend fun markSeasonAndPreviousAsWatched(
        showId: Long,
        seasonNumber: Long,
        includeSpecials: Boolean,
        watchedAt: Long? = null,
        useReleaseDate: Boolean = false,
    )

    public suspend fun markEpisodeAndPreviousAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        includeSpecials: Boolean,
        watchedAt: Long? = null,
        useReleaseDate: Boolean = false,
    )

    public suspend fun getEpisodesForSeason(
        showId: Long,
        seasonNumber: Long,
        watchedAt: Long? = null,
        useReleaseDate: Boolean = false,
    ): List<EpisodeWatchParams>

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

    public suspend fun entriesByPendingAction(action: WatchedEpisodeSyncOperation): List<GetEntriesByPendingAction>

    public suspend fun updatePendingAction(id: Long, action: WatchedEpisodeSyncOperation)

    public suspend fun updatePendingActions(ids: List<Long>, action: WatchedEpisodeSyncOperation)

    public fun deleteAll()

    public suspend fun countPendingActions(): Long

    public suspend fun deleteById(id: Long)

    public suspend fun deleteByIds(ids: List<Long>)

    public suspend fun upsertBatchFromTrakt(
        showId: Long,
        entries: List<WatchedEpisodeEntry>,
        includeSpecials: Boolean,
    )

    public suspend fun getShowSyncRemoteUpdatedAt(showId: Long, provider: String): Long?

    public suspend fun upsertShowSyncLog(showId: Long, provider: String, remoteUpdatedAt: Long)

    public suspend fun deleteAllShowSyncLogs()
}
