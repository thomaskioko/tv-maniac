package com.thomaskioko.tvmaniac.domain.library

import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedAccountRepository
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Schedules [LibrarySyncWorker] to run periodically while an account is connected and background
 * sync is enabled. Only enqueues the periodic worker: the heavy library sync runs inside
 * [LibrarySyncWorker] on its background schedule, never inline at app start.
 */
@Inject
public class SyncTasksInitializer(
    private val scheduler: BackgroundTaskScheduler,
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
    datastoreRepo: Lazy<DatastoreRepository>,
    connectedAccountRepo: Lazy<ConnectedAccountRepository>,
) {

    private val datastoreRepository by datastoreRepo
    private val connectedAccountRepository by connectedAccountRepo

    public fun init() {
        coroutineScope.launch {
            combine(
                connectedAccountRepository.isConnected,
                datastoreRepository.observeBackgroundSyncEnabled(),
            ) { connected, syncEnabled ->
                connected && syncEnabled
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
