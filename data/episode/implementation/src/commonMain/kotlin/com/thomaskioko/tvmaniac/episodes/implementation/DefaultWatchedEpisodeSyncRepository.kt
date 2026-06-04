package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodeWatchesDataSource
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedShowBatch
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncTypes
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant.Companion.fromEpochMilliseconds

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWatchedEpisodeSyncRepository(
    private val dao: WatchedEpisodeDao,
    private val episodesDao: EpisodesDao,
    private val dataSource: EpisodeWatchesDataSource,
    private val datastoreRepository: DatastoreRepository,
    private val lastRequestStore: EpisodeWatchesLastRequestStore,
    private val syncRepository: ActivitySyncRepository,
    private val traktAuthRepository: TraktAuthRepository,
    private val dateTimeProvider: DateTimeProvider,
    private val logger: Logger,
) : WatchedEpisodeSyncRepository {

    private val syncMutex = Mutex()

    override suspend fun syncPendingEpisodes() {
        val authState = traktAuthRepository.getAuthState()
        if (authState == null || !authState.isAuthorized) return

        processPendingEpisodesToUploads()
        processPendingEpisodesDeletes()
    }

    override suspend fun syncAllWatchedEpisodes(forceRefresh: Boolean) {
        val authState = traktAuthRepository.getAuthState()
        if (authState == null || !authState.isAuthorized) return

        syncMutex.withLock {
            val pendingUploads = dao.entriesByPendingAction(PendingAction.UPLOAD)
            val pendingDeletes = dao.entriesByPendingAction(PendingAction.DELETE)
            if (pendingUploads.isNotEmpty()) {
                uploadPending(pendingUploads)
            }
            if (pendingDeletes.isNotEmpty()) {
                deletePending(pendingDeletes)
            }

            dao.purgeSyncedDeletesOlderThan(
                thresholdMillis = dateTimeProvider.nowMillis() - SYNCED_DELETE_TTL.inWholeMilliseconds,
            )

            val activityAhead = syncRepository.isAheadOf(
                consumerId = ActivitySyncTypes.BULK_WATCHED_EPISODES,
                activityType = ActivityType.EPISODES_WATCHED,
            )
            val ttlValid = lastRequestStore.isRequestValid()
            if (!forceRefresh && !activityAhead && ttlValid) {
                logger.debug(TAG, "Bulk watched-shows sync skipped — activity unchanged and TTL valid")
                return
            }

            fetchAllWatchedShows()

            syncRepository.markSyncedTo(
                consumerId = ActivitySyncTypes.BULK_WATCHED_EPISODES,
                activityType = ActivityType.EPISODES_WATCHED,
            )
            lastRequestStore.updateLastRequest()
        }
    }

    override suspend fun syncShowEpisodeWatches(showTraktId: Long, forceRefresh: Boolean) {
        val authState = traktAuthRepository.getAuthState()
        if (authState == null || !authState.isAuthorized) return

        val perShowExpired = lastRequestStore.isShowRequestExpired(showTraktId)
        if (!forceRefresh && !perShowExpired) {
            logger.debug(TAG, "Per-show sync skipped for $showTraktId — per-show TTL fresh")
            return
        }

        syncShowWatches(showTraktId)
        lastRequestStore.updateShowLastRequest(showTraktId)
    }

    private suspend fun processPendingEpisodesToUploads() {
        val pending = dao.entriesByPendingAction(PendingAction.UPLOAD)
        if (pending.isEmpty()) return

        uploadPending(pending)
    }

    private suspend fun processPendingEpisodesDeletes() {
        val pending = dao.entriesByPendingAction(PendingAction.DELETE)
        if (pending.isEmpty()) return

        deletePending(pending)
    }

    private suspend fun uploadPending(
        pending: List<com.thomaskioko.tvmaniac.db.GetEntriesByPendingAction>,
    ) {
        logger.debug(TAG, "Processing ${pending.size} pending uploads")

        val entries = pending.map { episode ->
            WatchedEpisodeEntry(
                id = episode.watched_id,
                showTraktId = episode.show_trakt_id.id,
                episodeId = episode.episode_id?.id,
                seasonNumber = episode.season_number,
                episodeNumber = episode.episode_number,
                watchedAt = fromEpochMilliseconds(episode.watched_at),
                traktId = episode.trakt_id,
                pendingAction = PendingAction.UPLOAD,
            )
        }

        dataSource.addEpisodeWatches(entries)

        pending.forEach { episode ->
            dao.updatePendingAction(episode.watched_id, PendingAction.NOTHING)
        }

        logger.debug(TAG, "Successfully uploaded ${pending.size} episodes")
    }

    private suspend fun deletePending(
        pending: List<com.thomaskioko.tvmaniac.db.GetEntriesByPendingAction>,
    ) {
        logger.debug(TAG, "Processing ${pending.size} pending deletes")

        val episodeTraktIds = pending.mapNotNull { episode ->
            episodesDao.getEpisodeByShowSeasonEpisodeNumber(
                showTraktId = episode.show_trakt_id.id,
                seasonNumber = episode.season_number,
                episodeNumber = episode.episode_number,
            )?.trakt_id
        }
        if (episodeTraktIds.isNotEmpty()) {
            dataSource.removeEpisodeWatches(episodeTraktIds)
        }

        pending.forEach { episode ->
            if (episode.trakt_id != null) {
                dao.markAsSyncedDelete(episode.watched_id)
            } else {
                dao.deleteById(episode.watched_id)
            }
        }

        logger.debug(TAG, "Successfully deleted ${pending.size} episodes")
    }

    private suspend fun fetchAllWatchedShows() {
        val includeSpecials = datastoreRepository.getIncludeSpecials()
        var page = 1
        var totalShows = 0

        while (true) {
            currentCoroutineContext().ensureActive()
            val batches = dataSource.getAllWatchedShows(page = page, limit = PAGE_LIMIT)
            if (batches.isEmpty()) break

            batches.forEach { batch ->
                currentCoroutineContext().ensureActive()
                upsertBatch(batch, includeSpecials)
                totalShows++
            }

            if (batches.size < PAGE_LIMIT) break
            page++
        }

        logger.debug(TAG, "Bulk watched-shows sync drained $totalShows shows across $page page(s)")
    }

    private suspend fun upsertBatch(batch: WatchedShowBatch, includeSpecials: Boolean) {
        if (batch.episodes.isEmpty()) return

        batch.episodes.chunked(BATCH_SIZE).forEach { chunk ->
            currentCoroutineContext().ensureActive()
            val resolved = chunk.map { entry ->
                val episode = episodesDao.getEpisodeByShowSeasonEpisodeNumber(
                    showTraktId = entry.showTraktId,
                    seasonNumber = entry.seasonNumber,
                    episodeNumber = entry.episodeNumber,
                )
                entry.copy(episodeId = episode?.episode_id?.id)
            }
            dao.upsertBatchFromTrakt(
                showTraktId = batch.showTraktId,
                entries = resolved,
                includeSpecials = includeSpecials,
            )
        }
    }

    private suspend fun syncShowWatches(showTraktId: Long) {
        val remoteWatches = dataSource.getShowEpisodeWatches(showTraktId)

        if (remoteWatches.isEmpty()) {
            logger.debug(TAG, "No remote watches for show $showTraktId")
            return
        }

        logger.debug(TAG, "Found ${remoteWatches.size} remote watches for show $showTraktId")

        val includeSpecials = datastoreRepository.getIncludeSpecials()

        remoteWatches.chunked(BATCH_SIZE).forEach { batch ->
            currentCoroutineContext().ensureActive()

            val entriesWithEpisodeIds = batch.map { remoteEntry ->
                val episode = episodesDao.getEpisodeByShowSeasonEpisodeNumber(
                    showTraktId = showTraktId,
                    seasonNumber = remoteEntry.seasonNumber,
                    episodeNumber = remoteEntry.episodeNumber,
                )
                remoteEntry.copy(episodeId = episode?.episode_id?.id)
            }

            dao.upsertBatchFromTrakt(
                showTraktId = showTraktId,
                entries = entriesWithEpisodeIds,
                includeSpecials = includeSpecials,
            )
        }

        logger.debug(TAG, "Synced ${remoteWatches.size} episode watches for show $showTraktId")
    }

    private companion object {
        private const val TAG = "WatchedEpisodeSyncRepository"
        private const val BATCH_SIZE = 50
        private const val PAGE_LIMIT = 100
        private val SYNCED_DELETE_TTL = 7.days
    }
}
