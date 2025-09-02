package com.thomaskioko.tvmaniac.nextepisode.testing

import com.thomaskioko.tvmaniac.nextepisode.api.NextEpisodeDao
import com.thomaskioko.tvmaniac.nextepisode.api.NextEpisodeRepository
import com.thomaskioko.tvmaniac.nextepisode.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.nextepisode.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

public class FakeNextEpisodeRepository(
    private val nextEpisodeDao: NextEpisodeDao? = null,
    private val watchedEpisodeDao: WatchedEpisodeDao? = null,
) : NextEpisodeRepository {
    private val refreshedShowIds = mutableListOf<Long>()
    private val removedShowIds = mutableListOf<Long>()

    override suspend fun refreshNextEpisodeData(showId: Long) {
        refreshedShowIds.add(showId)
    }

    override suspend fun fetchNextEpisode(showId: Long): Unit = Unit

    override fun observeNextEpisodeForShow(showId: Long): Flow<NextEpisodeWithShow?> = flowOf(null)

    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> =
        flowOf(emptyList())

    override suspend fun removeShowFromTracking(showId: Long) {
        removedShowIds.add(showId)
        nextEpisodeDao?.delete(showId)
        watchedEpisodeDao?.deleteAllForShow(showId)
    }
}
