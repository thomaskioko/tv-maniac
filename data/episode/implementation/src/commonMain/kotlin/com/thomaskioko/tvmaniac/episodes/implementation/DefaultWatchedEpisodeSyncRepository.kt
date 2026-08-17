package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodeWatchesDataSource
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncOperation
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedShowBatch
import com.thomaskioko.tvmaniac.shows.api.ShowReconciler
import com.thomaskioko.tvmaniac.shows.api.ShowResolveOutcome
import com.thomaskioko.tvmaniac.shows.api.traktGenreName
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncTypes
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.watchstatus.api.ShowWatchStatusRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
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
    private val activeSource: () -> EpisodeWatchesDataSource?,
    private val accountManager: AccountManager,
    private val datastoreRepository: DatastoreRepository,
    private val lastRequestStore: EpisodeWatchesLastRequestStore,
    private val syncRepository: ActivitySyncRepository,
    private val logger: Logger,
    private val watchStatusRepository: ShowWatchStatusRepository,
    private val showReconciler: ShowReconciler,
) : WatchedEpisodeSyncRepository {

    private val syncMutex = Mutex()

    override suspend fun syncPendingEpisodes() {
        if (accountManager.getActiveProvider() == null) return

        syncMutex.withLock {
            processPendingEpisodesToUploads()
            processPendingEpisodeUpdates()
            processPendingEpisodesDeletes()
        }
    }

    override suspend fun syncAllWatchedEpisodes(forceRefresh: Boolean) {
        if (accountManager.getActiveProvider() == null) return

        syncMutex.withLock {
            val pendingUploads = dao.entriesByPendingAction(WatchedEpisodeSyncOperation.UPLOAD)
            val pendingUpdates = dao.entriesByPendingAction(WatchedEpisodeSyncOperation.UPDATE)
            val pendingDeletes = dao.entriesByPendingAction(WatchedEpisodeSyncOperation.DELETE)
            if (pendingUploads.isNotEmpty()) {
                uploadPending(pendingUploads)
            }
            if (pendingUpdates.isNotEmpty()) {
                updatePending(pendingUpdates)
            }
            if (pendingDeletes.isNotEmpty()) {
                deletePending(pendingDeletes)
            }

            if (pendingDeletes.isNotEmpty() || pendingUpdates.isNotEmpty()) {
                val removals = pendingDeletes.size + pendingUpdates.size
                logger.debug(TAG, "Deferring bulk pull this cycle after pushing $removals removal(s)")
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

    override suspend fun countPendingEpisodes(): Long = dao.countPendingActions()

    override suspend fun getWatchedShowsMissingGenres(): List<Long> = dao.getWatchedShowsMissingGenres()

    private suspend fun processPendingEpisodesToUploads() {
        val pending = dao.entriesByPendingAction(WatchedEpisodeSyncOperation.UPLOAD)
        if (pending.isEmpty()) return

        uploadPending(pending)
    }

    private suspend fun processPendingEpisodeUpdates() {
        val pending = dao.entriesByPendingAction(WatchedEpisodeSyncOperation.UPDATE)
        if (pending.isEmpty()) return

        updatePending(pending)
    }

    private suspend fun processPendingEpisodesDeletes() {
        val pending = dao.entriesByPendingAction(WatchedEpisodeSyncOperation.DELETE)
        if (pending.isEmpty()) return

        deletePending(pending)
    }

    private suspend fun updatePending(
        pending: List<com.thomaskioko.tvmaniac.db.GetEntriesByPendingAction>,
    ) {
        val source = activeSource() ?: return
        logger.debug(TAG, "Processing ${pending.size} pending updates")

        val entries = pending.map(::toEntry)

        source.removeEpisodeEntries(entries)
        source.addEpisodeEntries(entries)

        dao.updatePendingActions(pending.map { it.watched_id }, WatchedEpisodeSyncOperation.NOTHING)

        logger.debug(TAG, "Successfully updated ${pending.size} episodes")
    }

    private suspend fun uploadPending(
        pending: List<com.thomaskioko.tvmaniac.db.GetEntriesByPendingAction>,
    ) {
        val source = activeSource() ?: return
        logger.debug(TAG, "Processing ${pending.size} pending uploads")

        source.addEpisodeEntries(pending.map(::toEntry))

        dao.updatePendingActions(pending.map { it.watched_id }, WatchedEpisodeSyncOperation.NOTHING)

        logger.debug(TAG, "Successfully uploaded ${pending.size} episodes")
    }

    private suspend fun deletePending(
        pending: List<com.thomaskioko.tvmaniac.db.GetEntriesByPendingAction>,
    ) {
        val source = activeSource() ?: return
        logger.debug(TAG, "Processing ${pending.size} pending deletes")

        source.removeEpisodeEntries(pending.map(::toEntry))

        dao.deleteByIds(pending.map { it.watched_id })

        logger.debug(TAG, "Successfully deleted ${pending.size} episodes")
    }

    private fun toEntry(
        episode: com.thomaskioko.tvmaniac.db.GetEntriesByPendingAction,
    ): WatchedEpisodeEntry = WatchedEpisodeEntry(
        id = episode.watched_id,
        showId = episode.trakt_id,
        episodeId = episode.episode_id?.id,
        seasonNumber = episode.season_number,
        episodeNumber = episode.episode_number,
        watchedAt = fromEpochMilliseconds(episode.watched_at),
        traktId = episode.trakt_id,
    )

    private suspend fun fetchAllWatchedShows() {
        val source = activeSource() ?: return
        val includeSpecials = datastoreRepository.getIncludeSpecials()
        var page = 1
        var totalShows = 0
        var failedShows = 0
        var firstFailure: Exception? = null

        while (true) {
            currentCoroutineContext().ensureActive()
            val batches = source.getAllWatchedShows(page = page, limit = PAGE_LIMIT)
            if (batches.isEmpty()) break

            batches.forEach { batch ->
                currentCoroutineContext().ensureActive()
                try {
                    upsertBatch(batch, includeSpecials)
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (exception: Exception) {
                    failedShows++
                    if (firstFailure == null) firstFailure = exception
                    logger.error(TAG, "Skipping watched-shows batch for ${batch.title ?: batch.tmdbId}: ${exception.message}")
                }
                totalShows++
            }

            if (batches.size < PAGE_LIMIT) break
            page++
        }

        logger.debug(TAG, "Bulk watched-shows sync drained $totalShows shows across $page page(s)")

        fetchMissingShowMetadata()

        firstFailure?.let { failure ->
            logger.error(TAG, "Bulk watched-shows sync failed for $failedShows of $totalShows shows")
            throw failure
        }
    }

    private suspend fun fetchMissingShowMetadata() {
        val source = activeSource() ?: return
        val missing = dao.countWatchedShowsMissingMetadata()
        if (missing == 0L) return

        logger.debug(TAG, "Fetching metadata for $missing watched show(s)")
        var page = 1
        var updated = 0
        while (true) {
            currentCoroutineContext().ensureActive()
            val metadata = source.getWatchedShowMetadata(page = page, limit = PAGE_LIMIT)
            if (metadata.isEmpty()) break

            metadata.forEach { show ->
                dao.updateShowMetadata(
                    tmdbId = show.tmdbId,
                    runtime = show.runtimeMinutes,
                    year = show.year,
                    genres = show.genres?.map(::traktGenreName),
                )
            }
            updated += metadata.size

            if (metadata.size < PAGE_LIMIT) break
            page++
        }
        logger.debug(TAG, "Stored metadata for $updated show(s)")
    }

    private suspend fun upsertBatch(batch: WatchedShowBatch, includeSpecials: Boolean) {
        if (batch.episodes.isEmpty()) return
        val activeProvider = accountManager.getActiveProvider() ?: return

        val (outcome, _) = showReconciler.reconcile(
            tmdbId = batch.tmdbId,
            imdbId = batch.imdbId,
            title = batch.title,
            providerShowId = batch.providerShowId,
            provider = activeProvider,
        )

        val tmdbId = when (outcome) {
            is ShowResolveOutcome.Resolved -> outcome.tmdbId
            is ShowResolveOutcome.Skipped -> return
        }

        if (batch.runtime != null || batch.year != null) {
            dao.updateShowMetadata(tmdbId = tmdbId, runtime = batch.runtime, year = batch.year)
        }

        val remoteUpdatedAt = batch.lastUpdatedAt?.toEpochMilliseconds()
        if (remoteUpdatedAt != null &&
            remoteUpdatedAt == dao.getShowSyncRemoteUpdatedAt(tmdbId, activeProvider.name)
        ) {
            logger.debug(TAG, "Skipping unchanged watched show $tmdbId")
            return
        }

        currentCoroutineContext().ensureActive()
        val resolved = batch.episodes.map { entry ->
            val episode = episodesDao.getEpisodeByShowSeasonEpisodeNumber(
                showId = tmdbId,
                seasonNumber = entry.seasonNumber,
                episodeNumber = entry.episodeNumber,
            )
            entry.copy(showId = tmdbId, episodeId = episode?.episode_id?.id)
        }
        dao.upsertBatchFromTrakt(
            showId = tmdbId,
            entries = resolved,
            includeSpecials = includeSpecials,
        )

        watchStatusRepository.refresh(tmdbId)

        if (remoteUpdatedAt != null) {
            dao.upsertShowSyncLog(
                showId = tmdbId,
                provider = activeProvider.name,
                remoteUpdatedAt = remoteUpdatedAt,
            )
        }
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
