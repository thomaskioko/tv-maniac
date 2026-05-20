package com.thomaskioko.tvmaniac.domain.continuewatching

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingRepository
import com.thomaskioko.tvmaniac.core.base.extensions.parallelForEach
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.syncactivity.SyncActivityInteractor
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

@Inject
public class SyncContinueWatchingInteractor(
    private val syncActivityInteractor: SyncActivityInteractor,
    private val continueWatchingRepository: ContinueWatchingRepository,
    private val continueWatchingDao: ContinueWatchingDao,
    private val syncShowMetadataInteractor: SyncShowMetadataInteractor,
    private val syncObserver: SyncObserver,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<SyncContinueWatchingInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            syncActivityInteractor.executeSync(
                SyncActivityInteractor.Param(forceRefresh = params.forceRefresh),
            )
            continueWatchingRepository.sync(
                forceRefresh = params.forceRefresh,
                useNitro = params.useNitro,
            )

            val watchedShows = continueWatchingDao.entries()
            logger.debug(TAG, "Syncing metadata for ${watchedShows.size} watched shows")

            watchedShows.parallelForEach(concurrency = CONTINUE_WATCHING_SYNC_CONCURRENCY) { show ->
                ensureActive()
                runCatching {
                    syncShowMetadataInteractor.executeSync(
                        SyncShowMetadataInteractor.Param(
                            traktId = show.traktId,
                            forceRefresh = params.forceRefresh,
                        ),
                    )
                }.onFailure {
                    logger.warning(TAG, "syncShowMetadata failed for ${show.traktId}: ${it.message}")
                    syncObserver.log(SyncError.BackgroundSyncFailed(TAG, it))
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
