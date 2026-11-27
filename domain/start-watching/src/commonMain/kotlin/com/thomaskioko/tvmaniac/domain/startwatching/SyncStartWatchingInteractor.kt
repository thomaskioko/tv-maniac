package com.thomaskioko.tvmaniac.domain.startwatching

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.toSyncError
import com.thomaskioko.tvmaniac.domain.showdetails.ShowMetadataSyncHelper
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncError as NetworkSyncError

@Inject
public class SyncStartWatchingInteractor(
    private val startWatchingRepository: StartWatchingRepository,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val syncShowMetadataInteractor: SyncShowMetadataInteractor,
    private val showMetadataSyncHelper: ShowMetadataSyncHelper,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<SyncStartWatchingInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            startWatchingRepository.syncWatchlist(params.forceRefresh)
            if (params.forceRefresh) {
                watchedEpisodeSyncRepository.syncAllWatchedEpisodes(forceRefresh = true)
            }
            syncShowMetadata()
        }
    }

    private suspend fun syncShowMetadata() {
        val shows = startWatchingRepository.observeStartWatching().first()
        logger.debug(TAG, "Syncing metadata for ${shows.size} start watching shows")

        for (show in shows) {
            currentCoroutineContext().ensureActive()
            val result = runCatching {
                if (showMetadataSyncHelper.shouldSync(show.showId)) {
                    syncShowMetadataInteractor.executeSync(
                        SyncShowMetadataInteractor.Param(
                            showId = show.showId,
                            forceRefresh = false,
                            includeWatchProviders = false,
                            refreshLatestSeason = showMetadataSyncHelper.shouldRefreshLatestSeason(show.showId),
                        ),
                    )
                } else {
                    logger.debug(TAG, "Skipping metadata sync for ended show ${show.showId}")
                }
            }
            val failure = result.exceptionOrNull() ?: continue
            if (failure is CancellationException) throw failure

            logger.warning(TAG, "syncShowMetadata failed for ${show.showId}: ${failure.message}")

            if (failure.toSyncError() is NetworkSyncError.Retryable) {
                logger.warning(TAG, "Backing off metadata fan-out after retryable failure on ${show.showId}")
                break
            }
        }
    }

    public data class Param(
        val forceRefresh: Boolean = false,
    )

    private companion object {
        private const val TAG = "SyncStartWatchingInteractor"
    }
}
