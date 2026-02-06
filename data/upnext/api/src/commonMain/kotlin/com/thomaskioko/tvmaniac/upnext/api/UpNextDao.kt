package com.thomaskioko.tvmaniac.upnext.api

import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow

public interface UpNextDao {

    public fun observeNextEpisodesFromCache(): Flow<List<NextEpisodeWithShow>>

    public fun observeNextEpisodeForShow(showTraktId: Long): Flow<List<NextEpisodeWithShow>>

    public suspend fun upsert(
        showTraktId: Long,
        episodeTraktId: Long?,
        seasonNumber: Long?,
        episodeNumber: Long?,
        title: String?,
        overview: String?,
        runtime: Long?,
        firstAired: Long?,
        imageUrl: String?,
        isShowComplete: Boolean,
        lastEpisodeSeason: Long?,
        lastEpisodeNumber: Long?,
        traktLastWatchedAt: Long?,
        updatedAt: Long,
    )

    public suspend fun upsertShowProgress(
        showTraktId: Long,
        watchedCount: Long,
        totalCount: Long,
    )

    public suspend fun advanceAfterWatched(
        showTraktId: Long,
        watchedSeason: Long,
        watchedEpisode: Long,
    )

    public suspend fun deleteForShow(showTraktId: Long)

    public suspend fun deleteAll()
}
