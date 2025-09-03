package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.db.Watched_episodes
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.api.model.WatchProgress
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultEpisodeRepository(
    private val watchedEpisodeDao: WatchedEpisodeDao,
    private val nextEpisodeDao: DefaultNextEpisodeDao,
) : EpisodeRepository {

    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> =
        nextEpisodeDao.observeNextEpisodesForWatchlist()

    override fun observeNextEpisodeForShow(showId: Long): Flow<NextEpisodeWithShow?> =
        nextEpisodeDao.observeNextEpisode(showId)

    override suspend fun markEpisodeAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        watchedEpisodeDao.markAsWatched(showId, episodeId, seasonNumber, episodeNumber)
    }

    override suspend fun markEpisodeAsUnwatched(showId: Long, episodeId: Long) {
        watchedEpisodeDao.markAsUnwatched(showId, episodeId)
    }

    override fun observeWatchedEpisodes(showId: Long): Flow<List<Watched_episodes>> =
        watchedEpisodeDao.observeWatchedEpisodes(showId)

    override fun observeWatchProgress(showId: Long): Flow<WatchProgress> =
        watchedEpisodeDao.observeWatchProgress(showId)

    override suspend fun getLastWatchedEpisode(showId: Long): Watched_episodes? =
        watchedEpisodeDao.getLastWatchedEpisode(showId)

    public suspend fun getWatchedEpisodesForSeason(showId: Long, seasonNumber: Long): List<Watched_episodes> =
        watchedEpisodeDao.getWatchedEpisodesForSeason(showId, seasonNumber)

    override suspend fun isEpisodeWatched(showId: Long, seasonNumber: Long, episodeNumber: Long): Boolean =
        watchedEpisodeDao.isEpisodeWatched(showId, seasonNumber, episodeNumber)

    override suspend fun clearWatchHistoryForShow(showId: Long) {
        watchedEpisodeDao.deleteAllForShow(showId)
    }
}
