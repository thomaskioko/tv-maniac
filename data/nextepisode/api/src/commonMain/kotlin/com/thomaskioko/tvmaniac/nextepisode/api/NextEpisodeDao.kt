package com.thomaskioko.tvmaniac.nextepisode.api

import com.thomaskioko.tvmaniac.db.GetNextEpisodeForShow
import com.thomaskioko.tvmaniac.nextepisode.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow

public interface NextEpisodeDao {

    public fun observeNextEpisode(showId: Long): Flow<NextEpisodeWithShow?>

    public fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>>

    public suspend fun getNextEpisodeForShow(showId: Long): GetNextEpisodeForShow?

    public fun upsert(
        showId: Long,
        episodeId: Long?,
        episodeName: String,
        episodeNumber: Long,
        seasonNumber: Long,
        airDate: String?,
        runtime: Int?,
        stillPath: String?,
        overview: String,
        isUpcoming: Boolean = false,
    )

    public suspend fun delete(showId: Long)

    public suspend fun deleteAll()

    public suspend fun getNextEpisodesCount(): Long

    public suspend fun getStaleNextEpisodes(thresholdTimestamp: Long): List<Long>
}
