package com.thomaskioko.tvmaniac.domain.library

import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
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
public class SyncTasksInitializer(
    private val scheduler: BackgroundTaskScheduler,
    private val logger: Logger,
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
    syncLibraryInteractor: Lazy<SyncLibraryInteractor>,
    datastoreRepo: Lazy<DatastoreRepository>,
    traktAuthRepo: Lazy<TraktAuthRepository>,
) {

    private val syncInteractor by syncLibraryInteractor
    private val datastoreRepository by datastoreRepo
    private val traktAuthRepository by traktAuthRepo

    public fun init() {
        observeDataSync()
        observeLibrarySync()
    }

    private fun observeDataSync() {
        coroutineScope.launch {
            traktAuthRepository.state
                .distinctUntilChanged()
                .drop(1)
                .filter { it == TraktAuthState.LOGGED_IN }
                .collect {
                    withContext(NonCancellable) {
                        syncInteractor.executeSync(SyncLibraryInteractor.Param())
                        logger.debug(TAG, "Library sync completed successfully")
                    }
                }
        }
    }

    private fun observeLibrarySync() {
        coroutineScope.launch {
            combine(
                traktAuthRepository.state,
                datastoreRepository.observeBackgroundSyncEnabled(),
            ) { authState, syncEnabled ->
                authState == TraktAuthState.LOGGED_IN && syncEnabled
            }
                .distinctUntilChanged()
                .collect { shouldSync ->
                    when {
                        shouldSync -> scheduler.schedulePeriodic(LibrarySyncWorker.REQUEST)
                        else -> scheduler.cancel(LibrarySyncWorker.WORKER_NAME)
                    }
                }
        }
    }

    private companion object {
        private const val TAG = "SyncTasksInitializer"
    }
}
