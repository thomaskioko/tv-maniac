package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Inject
public class WatchlistTasksInitializer(
    private val scheduler: BackgroundTaskScheduler,
    private val syncObserver: SyncObserver,
    private val logger: Logger,
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
    watchlistSyncInteractor: Lazy<WatchlistSyncInteractor>,
    datastoreRepo: Lazy<DatastoreRepository>,
    traktAuthRepo: Lazy<TraktAuthRepository>,
) {

    private val syncInteractor by watchlistSyncInteractor
    private val datastoreRepository by datastoreRepo
    private val traktAuthRepository by traktAuthRepo

    public fun init() {
        observeDataSync()
        observeWatchlistSync()
    }

    private fun observeDataSync() {
        coroutineScope.launch {
            traktAuthRepository.state
                .distinctUntilChanged()
                .drop(1)
                .filter { it == TraktAuthState.LOGGED_IN }
                .collect {
                    withContext(NonCancellable) {
                        syncObserver.trackSync(POST_LOGIN_OPERATION_ID) {
                            syncInteractor.executeSync(WatchlistSyncInteractor.Param())
                            logger.debug(TAG, "Watchlist sync completed successfully")
                        }
                    }
                }
        }
    }

    private fun observeWatchlistSync() {
        coroutineScope.launch {
            combine(
                traktAuthRepository.state,
                datastoreRepository.observeBackgroundSyncEnabled(),
            ) { authState, syncEnabled ->
                authState == TraktAuthState.LOGGED_IN && syncEnabled
            }
                .distinctUntilChanged()
                .collect { shouldSync ->
                    if (shouldSync) {
                        scheduler.schedulePeriodic(WatchlistSyncWorker.REQUEST)
                    } else {
                        scheduler.cancel(WatchlistSyncWorker.WORKER_NAME)
                    }
                }
        }
    }

    private companion object {
        private const val TAG = "WatchlistTasksInitializer"
        private const val POST_LOGIN_OPERATION_ID = "PostLoginWatchlistSync"
    }
}
