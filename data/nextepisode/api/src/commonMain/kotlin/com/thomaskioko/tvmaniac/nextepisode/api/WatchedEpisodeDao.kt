package com.thomaskioko.tvmaniac.nextepisode.api

import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.nextepisode.api.model.WatchProgress
import kotlinx.coroutines.flow.Flow

public interface WatchedEpisodeDao {

    public fun observeWatchedEpisodes(showId: Long): Flow<List<Watched_episodes>>

    public fun observeWatchProgress(showId: Long): Flow<WatchProgress>

    public suspend fun markAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    )

    public suspend fun markAsUnwatched(
        showId: Long,
        episodeId: Long,
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

    public suspend fun deleteAllForShow(showId: Long)
}
