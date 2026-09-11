package com.thomaskioko.tvmaniac.domain.library

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.base.interactor.executeSync
import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Inject
public class SyncTasksInitializer(
    private val scheduler: BackgroundTaskScheduler,
    private val internetConnectionChecker: InternetConnectionChecker,
    private val logger: Logger,
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
    syncLibraryInteractor: Lazy<SyncLibraryInteractor>,
    datastoreRepo: Lazy<DatastoreRepository>,
    accountManagerLazy: Lazy<AccountManager>,
) {

    private val libraryInteractor by syncLibraryInteractor
    private val datastoreRepository by datastoreRepo
    private val accountManager by accountManagerLazy

    private val syncEnabled: Flow<Boolean>
        get() = combine(
            accountManager.isConnected,
            datastoreRepository.observeBackgroundSyncEnabled(),
        ) { connected, enabled ->
            connected && enabled
        }.distinctUntilChanged()

    public fun init() {
        observeSyncSchedule()
        observeReconnectSync()
    }

    private fun observeSyncSchedule() {
        coroutineScope.launch {
            syncEnabled
                .collect { shouldSync ->
                    if (shouldSync) {
                        scheduler.schedulePeriodic(LibrarySyncWorker.REQUEST)
                    } else {
                        scheduler.cancel(LibrarySyncWorker.WORKER_NAME)
                    }
                }
        }
    }

    private fun observeReconnectSync() {
        coroutineScope.launch {
            syncEnabled
                .flatMapLatest { enabled ->
                    if (enabled) internetConnectionChecker.observeReconnection() else emptyFlow()
                }
                .collect {
                    withContext(NonCancellable) {
                        runReconnectSync()
                    }
                }
        }
    }

    private suspend fun runReconnectSync() {
        try {
            libraryInteractor.executeSync(SyncLibraryInteractor.Param(forceRefresh = false))
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            logger.warning(TAG, "Reconnect library sync failed", throwable)
        }
    }

    private companion object {
        private const val TAG = "SyncTasksInitializer"
    }
}
