package com.thomaskioko.tvmaniac.domain.library

import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Schedules [LibrarySyncWorker] to run periodically while the user is logged in and background
 * sync is enabled. Only enqueues the periodic worker: the heavy library sync runs inside
 * [LibrarySyncWorker] on its background schedule, never inline at app start.
 */
@Inject
public class SyncTasksInitializer(
    private val scheduler: BackgroundTaskScheduler,
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
    datastoreRepo: Lazy<DatastoreRepository>,
    traktAuthRepo: Lazy<TraktAuthRepository>,
) {

    private val datastoreRepository by datastoreRepo
    private val traktAuthRepository by traktAuthRepo

    public fun init() {
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
                        scheduler.schedulePeriodic(LibrarySyncWorker.REQUEST)
                    } else {
                        scheduler.cancel(LibrarySyncWorker.WORKER_NAME)
                    }
                }
        }
    }
}
