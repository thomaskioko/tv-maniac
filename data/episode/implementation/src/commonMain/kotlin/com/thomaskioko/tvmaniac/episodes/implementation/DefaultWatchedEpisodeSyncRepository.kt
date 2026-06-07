package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.getActiveProvider
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
import com.thomaskioko.tvmaniac.watchstatus.api.ShowWatchStatusRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Instant.Companion.fromEpochMilliseconds

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWatchedEpisodeSyncRepository(
    private val dao: WatchedEpisodeDao,
    private val episodesDao: EpisodesDao,
    private val sources: Set<EpisodeWatchesDataSource>,
    private val accountManager: AccountManager,
    private val datastoreRepository: DatastoreRepository,
    private val lastRequestStore: EpisodeWatchesLastRequestStore,
    private val syncRepository: ActivitySyncRepository,
    private val logger: Logger,
    private val watchStatusRepository: ShowWatchStatusRepository,
) : WatchedEpisodeSyncRepository {

    private val syncMutex = Mutex()

    private fun activeSource(): EpisodeWatchesDataSource? =
        sources.getActiveProvider(accountManager)

    override suspend fun syncPendingEpisodes() {
        if (accountManager.getActiveProvider() == null) return

        processPendingEpisodesToUploads()
        processPendingEpisodesDeletes()
    }

    override suspend fun syncAllWatchedEpisodes(forceRefresh: Boolean) {
        if (accountManager.getActiveProvider() == null) return

        syncMutex.withLock {
            val pendingUploads = dao.entriesByPendingAction(PendingAction.UPLOAD)
            val pendingDeletes = dao.entriesByPendingAction(PendingAction.DELETE)
            if (pendingUploads.isNotEmpty()) {
                uploadPending(pendingUploads)
            }
            if (pendingDeletes.isNotEmpty()) {
                deletePending(pendingDeletes)
            }

            if (pendingDeletes.isNotEmpty()) {
                logger.debug(TAG, "Deferring bulk pull this cycle after pushing ${pendingDeletes.size} delete(s)")
                return
            }

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

    override suspend fun syncShowEpisodeWatches(showId: Long, forceRefresh: Boolean) {
        if (accountManager.getActiveProvider() == null) return

        val perShowExpired = lastRequestStore.isShowRequestExpired(showId)
        if (!forceRefresh && !perShowExpired) {
            logger.debug(TAG, "Per-show sync skipped for $showId — per-show TTL fresh")
            return
        }

        syncShowWatches(showId)
        lastRequestStore.updateShowLastRequest(showId)
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
        val source = activeSource() ?: return
        logger.debug(TAG, "Processing ${pending.size} pending uploads")

        val entries = pending.map { episode ->
            WatchedEpisodeEntry(
                id = episode.watched_id,
                showId = episode.show_trakt_id,
                episodeId = episode.episode_id?.id,
                seasonNumber = episode.season_number,
                episodeNumber = episode.episode_number,
                watchedAt = fromEpochMilliseconds(episode.watched_at),
                traktId = episode.trakt_id,
                pendingAction = PendingAction.UPLOAD,
            )
        }

        source.addEpisodeEntries(entries)

        pending.forEach { episode ->
            dao.updatePendingAction(episode.watched_id, PendingAction.NOTHING)
        }

        logger.debug(TAG, "Successfully uploaded ${pending.size} episodes")
    }

    private suspend fun deletePending(
        pending: List<com.thomaskioko.tvmaniac.db.GetEntriesByPendingAction>,
    ) {
        val source = activeSource() ?: return
        logger.debug(TAG, "Processing ${pending.size} pending deletes")

        val entries = pending.map { episode ->
            WatchedEpisodeEntry(
                id = episode.watched_id,
                showId = episode.show_trakt_id,
                episodeId = episode.episode_id?.id,
                seasonNumber = episode.season_number,
                episodeNumber = episode.episode_number,
                watchedAt = fromEpochMilliseconds(episode.watched_at),
                traktId = episode.trakt_id,
                pendingAction = PendingAction.DELETE,
            )
        }
        source.removeEpisodeEntries(entries)

        pending.forEach { episode ->
            dao.deleteById(episode.watched_id)
        }

        logger.debug(TAG, "Successfully deleted ${pending.size} episodes")
    }

    private suspend fun fetchAllWatchedShows() {
        val source = activeSource() ?: return
        val includeSpecials = datastoreRepository.getIncludeSpecials()
        var page = 1
        var totalShows = 0

        while (true) {
            currentCoroutineContext().ensureActive()
            val batches = source.getAllWatchedShows(page = page, limit = PAGE_LIMIT)
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

        currentCoroutineContext().ensureActive()
        val resolved = batch.episodes.map { entry ->
            val episode = episodesDao.getEpisodeByShowSeasonEpisodeNumber(
                showId = entry.showId,
                seasonNumber = entry.seasonNumber,
                episodeNumber = entry.episodeNumber,
            )
            entry.copy(episodeId = episode?.episode_id?.id)
        }
        dao.upsertBatchFromTrakt(
            showId = batch.showId,
            entries = resolved,
            includeSpecials = includeSpecials,
        )

        watchStatusRepository.refresh(batch.showId)
    }

    private suspend fun syncShowWatches(showId: Long) {
        val source = activeSource() ?: return
        val remoteWatches = source.getShowEpisodeWatches(showId)

        if (remoteWatches.isEmpty()) {
            logger.debug(TAG, "No remote watches for show $showId")
            return
        }

        logger.debug(TAG, "Found ${remoteWatches.size} remote watches for show $showId")

        val includeSpecials = datastoreRepository.getIncludeSpecials()

        currentCoroutineContext().ensureActive()
        val entriesWithEpisodeIds = remoteWatches.map { remoteEntry ->
            val episode = episodesDao.getEpisodeByShowSeasonEpisodeNumber(
                showId = showId,
                seasonNumber = remoteEntry.seasonNumber,
                episodeNumber = remoteEntry.episodeNumber,
            )
            remoteEntry.copy(episodeId = episode?.episode_id?.id)
        }

        dao.upsertBatchFromTrakt(
            showId = showId,
            entries = entriesWithEpisodeIds,
            includeSpecials = includeSpecials,
        )

        watchStatusRepository.refresh(showId)

        logger.debug(TAG, "Synced ${remoteWatches.size} episode watches for show $showId")
    }

    private companion object {
        private const val TAG = "WatchedEpisodeSyncRepository"
        private const val PAGE_LIMIT = 100
    }
}
