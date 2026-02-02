package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultNextEpisodeDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
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
    private val datastoreRepository: DatastoreRepository,
    private val syncRepository: WatchedEpisodeSyncRepository,
) : EpisodeRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> =
        datastoreRepository.observeIncludeSpecials()
            .flatMapLatest { includeSpecials ->
                nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials)
            }

    override suspend fun markEpisodeAsWatched(
        showTraktId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        val includeSpecials = getIncludeSpecials()
        watchedEpisodeDao.markAsWatched(
            showTraktId = showTraktId,
            episodeId = episodeId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            includeSpecials = includeSpecials,
        )
        syncRepository.syncShowEpisodeWatches(showTraktId)
    }

    override suspend fun markEpisodeAndPreviousEpisodesWatched(
        showTraktId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        val includeSpecials = getIncludeSpecials()
        watchedEpisodeDao.markEpisodeAndPreviousAsWatched(
            showTraktId = showTraktId,
            episodeId = episodeId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            includeSpecials = includeSpecials,
        )
        syncRepository.syncShowEpisodeWatches(showTraktId)
    }

    override suspend fun markEpisodeAsUnwatched(showTraktId: Long, episodeId: Long) {
        val includeSpecials = getIncludeSpecials()
        watchedEpisodeDao.markAsUnwatched(
            showTraktId = showTraktId,
            episodeId = episodeId,
            includeSpecials = includeSpecials,
        )
        syncRepository.syncShowEpisodeWatches(showTraktId)
    }

    override fun observeSeasonWatchProgress(
        showTraktId: Long,
        seasonNumber: Long,
    ): Flow<SeasonWatchProgress> =
        watchedEpisodeDao.observeSeasonWatchProgress(showTraktId, seasonNumber)
            .distinctUntilChanged()

    override fun observeShowWatchProgress(showTraktId: Long): Flow<ShowWatchProgress> =
        watchedEpisodeDao.observeShowWatchProgress(showTraktId)
            .distinctUntilChanged()

    override fun observeAllSeasonsWatchProgress(showTraktId: Long): Flow<List<SeasonWatchProgress>> =
        watchedEpisodeDao.observeAllSeasonsWatchProgress(showTraktId)
            .distinctUntilChanged()

    override suspend fun markSeasonWatched(
        showTraktId: Long,
        seasonNumber: Long,
    ) {
        val includeSpecials = getIncludeSpecials()
        val episodes = watchedEpisodeDao.getEpisodesForSeason(showTraktId, seasonNumber)
        watchedEpisodeDao.markSeasonAsWatched(
            showTraktId = showTraktId,
            seasonNumber = seasonNumber,
            episodes = episodes,
            includeSpecials = includeSpecials,
        )
        syncRepository.syncShowEpisodeWatches(showTraktId)
    }

    override suspend fun markSeasonAndPreviousSeasonsWatched(
        showTraktId: Long,
        seasonNumber: Long,
    ) {
        val includeSpecials = getIncludeSpecials()
        watchedEpisodeDao.markSeasonAndPreviousAsWatched(
            showTraktId = showTraktId,
            seasonNumber = seasonNumber,
            includeSpecials = includeSpecials,
        )
        syncRepository.syncShowEpisodeWatches(showTraktId)
    }

    override suspend fun markSeasonUnwatched(showTraktId: Long, seasonNumber: Long) {
        val includeSpecials = getIncludeSpecials()
        watchedEpisodeDao.markSeasonAsUnwatched(showTraktId, seasonNumber, includeSpecials)
        syncRepository.syncShowEpisodeWatches(showTraktId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeUnwatchedCountInPreviousSeasons(
        showTraktId: Long,
        seasonNumber: Long,
    ): Flow<Long> = datastoreRepository.observeIncludeSpecials()
        .flatMapLatest { includeSpecials ->
            watchedEpisodeDao.observeUnwatchedCountInPreviousSeasons(
                showTraktId,
                seasonNumber,
                includeSpecials,
            )
        }

    private suspend fun getIncludeSpecials(): Boolean = datastoreRepository.getIncludeSpecials()
}
