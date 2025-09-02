package com.thomaskioko.tvmaniac.nextepisode.implementation

import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.nextepisode.api.NextEpisodeRepository
import com.thomaskioko.tvmaniac.nextepisode.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.nextepisode.api.WatchedEpisodeRepository
import com.thomaskioko.tvmaniac.nextepisode.api.model.WatchProgress
import com.thomaskioko.tvmaniac.nextepisode.api.model.WatchedEpisode
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWatchedEpisodeRepository(
    private val watchedEpisodeDao: WatchedEpisodeDao,
    private val nextEpisodeRepository: NextEpisodeRepository,
) : WatchedEpisodeRepository {

    override suspend fun markEpisodeAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        // Mark episode as watched
        watchedEpisodeDao.markAsWatched(
            showId = showId,
            episodeId = episodeId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
        )

        // Refresh next episode for this show since watch progress changed
        nextEpisodeRepository.refreshNextEpisodeData(showId)
    }

    override suspend fun markEpisodeAsUnwatched(showId: Long, episodeId: Long) {
        watchedEpisodeDao.markAsUnwatched(showId, episodeId)

        // Refresh next episode since watch progress changed
        nextEpisodeRepository.refreshNextEpisodeData(showId)
    }

    override fun observeWatchedEpisodes(showId: Long): Flow<List<Watched_episodes>> {
        return watchedEpisodeDao.observeWatchedEpisodes(showId)
    }

    override fun observeWatchProgress(showId: Long): Flow<WatchProgress> {
        return watchedEpisodeDao.observeWatchProgress(showId)
    }

    override suspend fun getLastWatchedEpisode(showId: Long): Watched_episodes? {
        return watchedEpisodeDao.getLastWatchedEpisode(showId)
    }

    override suspend fun isEpisodeWatched(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): Boolean {
        return watchedEpisodeDao.isEpisodeWatched(showId, seasonNumber, episodeNumber)
    }

    override suspend fun clearWatchHistoryForShow(showId: Long) {
        watchedEpisodeDao.deleteAllForShow(showId)

        // Refresh next episode after clearing history
        nextEpisodeRepository.refreshNextEpisodeData(showId)
    }
}
