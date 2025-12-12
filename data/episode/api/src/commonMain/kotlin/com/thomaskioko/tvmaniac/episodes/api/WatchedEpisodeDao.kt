package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeWatchParams
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.UnwatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgress
import kotlinx.coroutines.flow.Flow

public interface WatchedEpisodeDao {

    public fun observeWatchedEpisodes(showId: Long): Flow<List<Watched_episodes>>

    public fun observeWatchProgress(showId: Long): Flow<WatchProgress>

    public fun observeSeasonWatchProgress(showId: Long, seasonNumber: Long): Flow<SeasonWatchProgress>

    public fun observeShowWatchProgress(showId: Long): Flow<ShowWatchProgress>

    public suspend fun markAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long,
    )

    public suspend fun markAsUnwatched(
        showId: Long,
        episodeId: Long,
    )

    public suspend fun markSeasonAsWatched(
        showId: Long,
        seasonNumber: Long,
        episodes: List<EpisodeWatchParams>,
        timestamp: Long,
    )

    public suspend fun markSeasonAsUnwatched(
        showId: Long,
        seasonNumber: Long,
    )

    public suspend fun markPreviousSeasonsAsWatched(
        showId: Long,
        seasonNumber: Long,
        timestamp: Long,
    )

    public suspend fun markSeasonAndPreviousAsWatched(
        showId: Long,
        seasonNumber: Long,
        timestamp: Long,
    )

    public suspend fun markPreviousEpisodesAsWatched(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        timestamp: Long,
    )

    public suspend fun markEpisodeAndPreviousAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        timestamp: Long,
    )

    public suspend fun getLastWatchedEpisode(showId: Long): Watched_episodes?

    public suspend fun getWatchedEpisodesForSeason(
        showId: Long,
        seasonNumber: Long,
    ): List<Watched_episodes>

    public suspend fun isEpisodeWatched(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): Boolean

    public suspend fun getUnwatchedEpisodesBefore(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): List<UnwatchedEpisode>

    public suspend fun getEpisodesForSeason(
        showId: Long,
        seasonNumber: Long,
    ): List<EpisodeWatchParams>

    public suspend fun deleteAllForShow(showId: Long)

    public suspend fun getUnwatchedEpisodeCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Long

    public fun observeUnwatchedCountBefore(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): Flow<Int>

    public fun observeUnwatchedCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Flow<Long>

    public fun observeUnsyncedEpisodes(): Flow<List<Watched_episodes>>

    public suspend fun updateSyncStatus(
        id: Long,
        status: String,
        syncedAt: Long,
    )

    public suspend fun upsertFromTrakt(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long,
        traktId: Long,
        syncedAt: Long,
    )
}
