package com.thomaskioko.tvmaniac.domain.continuewatching

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingRepository
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.toSyncError
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.syncactivity.SyncActivityInteractor
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.CONTINUE_WATCHING_SYNC
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncError as NetworkSyncError

@Inject
public class SyncContinueWatchingInteractor(
    private val syncActivityInteractor: SyncActivityInteractor,
    private val continueWatchingRepository: ContinueWatchingRepository,
    private val continueWatchingDao: ContinueWatchingDao,
    private val syncShowMetadataInteractor: SyncShowMetadataInteractor,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val requestManagerRepository: RequestManagerRepository,
    private val syncObserver: SyncObserver,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<SyncContinueWatchingInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        val ttlValid = requestManagerRepository.isRequestValid(
            requestType = CONTINUE_WATCHING_SYNC.name,
            threshold = CONTINUE_WATCHING_SYNC.duration,
        )
        if (!params.forceRefresh && ttlValid) {
            logger.debug(TAG, "Continue Watching sync skipped — cache still valid")
            return
        }

        withContext(dispatchers.io) {
            syncActivityInteractor.executeSync(
                SyncActivityInteractor.Param(forceRefresh = params.forceRefresh),
            )

            continueWatchingRepository.sync(
                forceRefresh = params.forceRefresh,
                useNitro = params.useNitro,
            )

            // Bulk watched-episode sync runs AFTER the continue-watching fetch so the CW table is
            // populated first. The bulk path is gated by its own activity checkpoint so it is a
            // no-op when nothing changed remotely; running it last means the UI surfaces CW data
            // immediately and the per-episode watched-state catches up in the background.
            watchedEpisodeSyncRepository.syncAllWatchedEpisodes(params.forceRefresh)

            val watchedShows = continueWatchingDao.entries()
            logger.debug(TAG, "Syncing metadata for ${watchedShows.size} watched shows")

            for (show in watchedShows) {
                ensureActive()
                val result = runCatching {
                    syncShowMetadataInteractor.executeSync(
                        SyncShowMetadataInteractor.Param(
                            traktId = show.traktId,
                            forceRefresh = params.forceRefresh,
                        ),
                    )
                }
                val failure = result.exceptionOrNull() ?: continue

                logger.warning(TAG, "syncShowMetadata failed for ${show.traktId}: ${failure.message}")
                syncObserver.log(SyncError.BackgroundSyncFailed(TAG, failure))

                if (failure.toSyncError() is NetworkSyncError.Retryable) {
                    logger.warning(TAG, "Backing off metadata fan-out after retryable failure on ${show.traktId}")
                    break
                }
            }

            logger.debug(TAG, "Continue Watching sync complete")
        }
    }

    public data class Param(
        val forceRefresh: Boolean = false,
        val useNitro: Boolean = false,
    )

    private companion object {
        private const val TAG = "SyncContinueWatchingInteractor"
        private const val CONTINUE_WATCHING_SYNC_CONCURRENCY = 10
    }
}
