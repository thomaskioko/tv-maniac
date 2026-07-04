package com.thomaskioko.tvmaniac.domain.continuewatching

import com.thomaskioko.tvmaniac.accountmanager.api.ProviderFeatures
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
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncError as NetworkSyncError

@Inject
@SingleIn(AppScope::class)
public class SyncContinueWatchingInteractor(
    private val syncActivityInteractor: SyncActivityInteractor,
    private val continueWatchingRepository: ContinueWatchingRepository,
    private val syncShowMetadataInteractor: SyncShowMetadataInteractor,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val activeProviderFeatures: () -> ProviderFeatures,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<SyncContinueWatchingInteractor.Param>() {

    private val syncMutex = Mutex()

    override suspend fun doWork(params: Param) {
        if (!params.forceRefresh && isCacheValid()) {
            logger.debug(TAG, "Continue Watching sync skipped — cache still valid")
            return
        }

        syncMutex.withLock {
            if (!params.forceRefresh && isCacheValid()) {
                logger.debug(TAG, "Continue Watching sync skipped — refreshed by concurrent sync")
                return@withLock
            }

            withContext(dispatchers.io) {
                val features = activeProviderFeatures()

                syncActivityInteractor.executeSync(
                    SyncActivityInteractor.Param(forceRefresh = params.forceRefresh),
                )

                if (features.supportsContinueWatchingFetch) {
                    continueWatchingRepository.sync(
                        forceRefresh = params.forceRefresh,
                        useNitro = params.useNitro,
                    )
                }

                watchedEpisodeSyncRepository.syncAllWatchedEpisodes(params.forceRefresh)

                if (!features.supportsContinueWatchingFetch) {
                    continueWatchingRepository.deriveMembershipFromWatchedEpisodes()
                }

                syncShowMetadata()

                logger.debug(TAG, "Continue Watching sync complete")
            }
        }
    }

    private fun isCacheValid(): Boolean = requestManagerRepository.isRequestValid(
        requestType = CONTINUE_WATCHING_SYNC.name,
        threshold = CONTINUE_WATCHING_SYNC.duration,
    )

    private suspend fun syncShowMetadata() {
        val watchedShows = continueWatchingRepository.getEntries()
            .sortedByDescending { it.lastWatchedAt }
        logger.debug(TAG, "Syncing metadata for ${watchedShows.size} watched shows")

        val shouldStopMetadataSync = MutableStateFlow(false)
        val semaphore = Semaphore(CONTINUE_WATCHING_SYNC_CONCURRENCY)

        coroutineScope {
            watchedShows.map { show ->
                async {
                    if (shouldStopMetadataSync.value) return@async
                    semaphore.withPermit {
                        if (shouldStopMetadataSync.value) return@withPermit
                        val result = runCatching {
                            syncShowMetadataInteractor.executeSync(
                                params = SyncShowMetadataInteractor.Param(
                                    showId = show.showId,
                                    forceRefresh = false,
                                    includeWatchProviders = false,
                                ),
                            )
                        }
                        val failure = result.exceptionOrNull() ?: return@withPermit

                        logger.warning(
                            TAG,
                            "syncShowMetadata failed for ${show.showId}: ${failure.message}",
                        )

                        if (failure.toSyncError() is NetworkSyncError.Retryable) {
                            logger.warning(
                                TAG,
                                "Backing off metadata fan-out after retryable failure on ${show.showId}",
                            )
                            shouldStopMetadataSync.value = true
                        }
                    }
                }
            }.awaitAll()
        }
    }

    public data class Param(
        val forceRefresh: Boolean = false,
        val useNitro: Boolean = false,
    )

    private companion object {
        private const val TAG = "SyncContinueWatchingInteractor"
        private const val CONTINUE_WATCHING_SYNC_CONCURRENCY = 4
    }
}
