package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.base.coroutines.AppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.episodes.api.model.RecentlyWatchedEpisode
import com.thomaskioko.tvmaniac.episodes.api.model.SeasonWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.ShowWatchProgress
import com.thomaskioko.tvmaniac.episodes.api.model.UpcomingEpisode
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext
import kotlin.time.Duration

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultEpisodeRepository(
    private val watchedEpisodeDao: WatchedEpisodeDao,
    private val datastoreRepository: DatastoreRepository,
    private val syncRepository: WatchedEpisodeSyncRepository,
    private val episodesDao: EpisodesDao,
    private val dispatchers: AppCoroutineDispatchers,
    private val upcomingEpisodesStore: UpcomingEpisodesStore,
    private val appScopeLauncher: AppScopeLauncher,
    private val syncObserver: SyncObserver,
) : EpisodeRepository {

    override fun observeEpisodeById(episodeId: Long): Flow<EpisodeById?> =
        episodesDao.observeEpisodeById(episodeId)

    override fun observeRecentlyWatched(limit: Long): Flow<List<RecentlyWatchedEpisode>> =
        watchedEpisodeDao.observeRecentlyWatched(limit)
            .distinctUntilChanged()

    override suspend fun markEpisodeAsWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        val includeSpecials = getIncludeSpecials()
        watchedEpisodeDao.markAsWatched(
            showId = showId,
            episodeId = episodeId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            includeSpecials = includeSpecials,
        )

        launchSyncReporting { SyncError.MarkWatchedFailed(showId, it) }
    }

    override suspend fun markEpisodeAndPreviousEpisodesWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        val includeSpecials = getIncludeSpecials()
        watchedEpisodeDao.markEpisodeAndPreviousAsWatched(
            showId = showId,
            episodeId = episodeId,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            includeSpecials = includeSpecials,
        )

        launchSyncReporting { SyncError.BatchMarkFailed(showId, it) }
    }

    override suspend fun markEpisodeAsUnwatched(showId: Long, episodeId: Long) {
        val includeSpecials = getIncludeSpecials()
        watchedEpisodeDao.markAsUnwatched(
            showId = showId,
            episodeId = episodeId,
            includeSpecials = includeSpecials,
        )

        launchSyncReporting { SyncError.MarkUnwatchedFailed(showId, it) }
    }

    override fun observeSeasonWatchProgress(
        showId: Long,
        seasonNumber: Long,
    ): Flow<SeasonWatchProgress> =
        watchedEpisodeDao.observeSeasonWatchProgress(showId, seasonNumber)
            .distinctUntilChanged()

    override fun observeShowWatchProgress(showId: Long): Flow<ShowWatchProgress> =
        watchedEpisodeDao.observeShowWatchProgress(showId)
            .distinctUntilChanged()

    override fun observeAllSeasonsWatchProgress(showId: Long): Flow<List<SeasonWatchProgress>> =
        watchedEpisodeDao.observeAllSeasonsWatchProgress(showId)
            .distinctUntilChanged()

    override suspend fun markSeasonWatched(
        showId: Long,
        seasonNumber: Long,
    ) {
        val includeSpecials = getIncludeSpecials()
        val episodes = watchedEpisodeDao.getEpisodesForSeason(showId, seasonNumber)
        watchedEpisodeDao.markSeasonAsWatched(
            showId = showId,
            seasonNumber = seasonNumber,
            episodes = episodes,
            includeSpecials = includeSpecials,
        )

        launchSyncReporting { SyncError.BatchMarkFailed(showId, it) }
    }

    override suspend fun markSeasonAndPreviousSeasonsWatched(
        showId: Long,
        seasonNumber: Long,
    ) {
        val includeSpecials = getIncludeSpecials()
        watchedEpisodeDao.markSeasonAndPreviousAsWatched(
            showId = showId,
            seasonNumber = seasonNumber,
            includeSpecials = includeSpecials,
        )

        launchSyncReporting { SyncError.BatchMarkFailed(showId, it) }
    }

    override suspend fun markSeasonUnwatched(showId: Long, seasonNumber: Long) {
        val includeSpecials = getIncludeSpecials()
        watchedEpisodeDao.markSeasonAsUnwatched(showId, seasonNumber, includeSpecials)

        launchSyncReporting { SyncError.BatchMarkFailed(showId, it) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeUnwatchedCountInPreviousSeasons(
        showId: Long,
        seasonNumber: Long,
    ): Flow<Long> = datastoreRepository.observeIncludeSpecials()
        .flatMapLatest { includeSpecials ->
            watchedEpisodeDao.observeUnwatchedCountInPreviousSeasons(
                showId,
                seasonNumber,
                includeSpecials,
            )
        }

    override suspend fun getUpcomingEpisodesFromFollowedShows(
        limit: Duration,
    ): List<UpcomingEpisode> =
        withContext(dispatchers.io) {
            episodesDao.getUpcomingEpisodesFromFollowedShows(limit)
                .map { episode ->
                    UpcomingEpisode(
                        episodeId = episode.episode_id.id,
                        seasonId = episode.season_id.id,
                        showId = episode.show_trakt_id.id,
                        episodeNumber = episode.episode_number,
                        seasonNumber = episode.season_number,
                        title = episode.title,
                        overview = episode.overview,
                        runtime = episode.runtime,
                        imageUrl = episode.image_url,
                        firstAired = episode.first_aired,
                        showName = episode.show_name,
                        showPoster = episode.show_poster,
                    )
                }
        }

    override suspend fun syncUpcomingEpisodesFromTrakt(
        startDate: String,
        days: Int,
        forceRefresh: Boolean,
    ) {
        val params = UpcomingEpisodesParams(startDate, days)
        when {
            forceRefresh -> upcomingEpisodesStore.fresh(params)
            else -> upcomingEpisodesStore.get(params)
        }
    }

    private suspend fun getIncludeSpecials(): Boolean = datastoreRepository.getIncludeSpecials()

    /**
     * Push pending watched-episodes changes via the sync repository on a background scope. On
     * failure, publish the appropriate [SyncError] variant for subscribed presenters and rethrow
     * so [AppScopeLauncher]'s catch logs the cause. The rethrow is intentional: the launcher's
     * existing swallow-and-log keeps the background scope alive.
     */
    private fun launchSyncReporting(errorFor: (Throwable) -> SyncError) {
        appScopeLauncher.launch(TAG) {
            try {
                syncRepository.syncPendingEpisodes()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                syncObserver.log(errorFor(throwable))
                throw throwable
            }
        }
    }

    private companion object {
        private const val TAG = "EpisodeRepository"
    }
}
