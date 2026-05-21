package com.thomaskioko.tvmaniac.domain.continuewatching

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
public class ContinueWatchingTasksInitializer(
    private val scheduler: BackgroundTaskScheduler,
    private val syncObserver: SyncObserver,
    private val logger: Logger,
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
    syncContinueWatchingInteractor: Lazy<SyncContinueWatchingInteractor>,
    datastoreRepo: Lazy<DatastoreRepository>,
    traktAuthRepo: Lazy<TraktAuthRepository>,
) {

    private val syncInteractor by syncContinueWatchingInteractor
    private val datastoreRepository by datastoreRepo
    private val traktAuthRepository by traktAuthRepo

    public fun init() {
        observeDataSync()
        observeContinueWatchingSync()
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
                            syncInteractor.executeSync(SyncContinueWatchingInteractor.Param())
                            logger.debug(TAG, "Continue Watching sync completed successfully")
                        }
                    }
                }
        }
    }

    private fun observeContinueWatchingSync() {
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
                        scheduler.schedulePeriodic(ContinueWatchingSyncWorker.REQUEST)
                    } else {
                        scheduler.cancel(ContinueWatchingSyncWorker.WORKER_NAME)
                    }
                }
        }
    }

    private companion object {
        private const val TAG = "ContinueWatchingTasksInitializer"
        private const val POST_LOGIN_OPERATION_ID = "PostLoginContinueWatchingSync"
    }
}
