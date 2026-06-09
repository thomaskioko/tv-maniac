package com.thomaskioko.tvmaniac.domain.library

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.toSyncError
import com.thomaskioko.tvmaniac.data.library.LibraryRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.syncactivity.SyncActivityInteractor
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.LIBRARY_SYNC
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncTypes
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncError as NetworkSyncError

@Inject
public class SyncLibraryInteractor(
    private val libraryRepository: LibraryRepository,
    private val followedShowsRepository: FollowedShowsRepository,
    private val syncActivityInteractor: SyncActivityInteractor,
    private val syncShowMetadataInteractor: SyncShowMetadataInteractor,
    private val watchedEpisodeSyncRepository: WatchedEpisodeSyncRepository,
    private val syncRepository: ActivitySyncRepository,
    private val datastoreRepository: DatastoreRepository,
    private val dateTimeProvider: DateTimeProvider,
    private val dispatchers: AppCoroutineDispatchers,
    private val syncObserver: SyncObserver,
    private val logger: Logger,
) : Interactor<SyncLibraryInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        val needsSync = params.forceRefresh || libraryRepository.needsSync(LIBRARY_SYNC.duration)
        if (!needsSync) {
            logger.debug(TAG, "Library sync skipped - cache still valid")
            return
        }

        withContext(dispatchers.io) {
            syncActivityInteractor.executeSync(
                SyncActivityInteractor.Param(forceRefresh = params.forceRefresh),
            )

            logger.debug(TAG, "Syncing library watchlist")
            libraryRepository.syncLibrary(params.forceRefresh)

            watchedEpisodeSyncRepository.syncAllWatchedEpisodes(params.forceRefresh)

            val watchlistChanged = params.forceRefresh ||
                syncRepository.isAheadOf(
                    consumerId = ActivitySyncTypes.LIBRARY_WATCHLIST,
                    activityType = ActivityType.SHOWS_WATCHLISTED,
                )
            if (!watchlistChanged) {
                logger.debug(TAG, "Metadata fan-out skipped — watchlist activity unchanged")
                datastoreRepository.setLastSyncTimestamp(dateTimeProvider.nowMillis())
                return@withContext
            }

            val followedShows = followedShowsRepository.getFollowedShows()
            logger.debug(TAG, "Syncing ${followedShows.size} followed shows")

            for (show in followedShows) {
                ensureActive()
                val result = runCatching {
                    syncShowMetadataInteractor.executeSync(
                        SyncShowMetadataInteractor.Param(
                            showId = show.showId,
                            forceRefresh = params.forceRefresh,
                        ),
                    )
                }
                val failure = result.exceptionOrNull() ?: continue

                logger.warning(TAG, "syncShowMetadata failed for ${show.showId}: ${failure.message}")
                syncObserver.log(SyncError.BackgroundSyncFailed(TAG, failure))

                if (failure.toSyncError() is NetworkSyncError.Retryable) {
                    logger.warning(TAG, "Backing off metadata fan-out after retryable failure on ${show.showId}")
                    break
                }
            }

            logger.debug(TAG, "Library sync complete")
        }

        datastoreRepository.setLastSyncTimestamp(dateTimeProvider.nowMillis())
    }

    public data class Param(
        val forceRefresh: Boolean = false,
    )

    private companion object {
        private const val TAG = "SyncLibraryInteractor"
        private const val LIBRARY_SYNC_CONCURRENCY = 2
    }
}
