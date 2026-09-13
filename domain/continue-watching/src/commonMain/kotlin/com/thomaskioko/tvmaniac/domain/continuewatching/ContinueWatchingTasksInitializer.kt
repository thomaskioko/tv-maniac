package com.thomaskioko.tvmaniac.domain.continuewatching

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.base.interactor.executeSync
import com.thomaskioko.tvmaniac.core.connectivity.api.InternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskScheduler
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.domain.episode.PendingUploadsWorker
import com.thomaskioko.tvmaniac.domain.episode.SyncPendingUploadsInteractor
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
public class ContinueWatchingTasksInitializer(
    private val scheduler: BackgroundTaskScheduler,
    private val logger: Logger,
    private val internetConnectionChecker: InternetConnectionChecker,
    @IoCoroutineScope private val coroutineScope: CoroutineScope,
    syncContinueWatchingInteractor: Lazy<SyncContinueWatchingInteractor>,
    syncPendingUploadsInteractor: Lazy<SyncPendingUploadsInteractor>,
    datastoreRepo: Lazy<DatastoreRepository>,
    accountManagerLazy: Lazy<AccountManager>,
) {

    private val syncInteractor by syncContinueWatchingInteractor
    private val pendingUploadsInteractor by syncPendingUploadsInteractor
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
        observeDataSync()
        observeContinueWatchingSync()
        observeReconnectSync()
    }

    private fun observeDataSync() {
        coroutineScope.launch {
            accountManager.connectionEvents
                .collect {
                    withContext(NonCancellable) {
                        syncInteractor.executeSync(SyncContinueWatchingInteractor.Param())
                        logger.debug(TAG, "Continue Watching sync completed successfully")
                    }
                }
        }
    }

    private fun observeContinueWatchingSync() {
        coroutineScope.launch {
            syncEnabled
                .collect { shouldSync ->
                    if (shouldSync) {
                        scheduler.schedulePeriodic(ContinueWatchingSyncWorker.REQUEST)
                        scheduler.schedulePeriodic(PendingUploadsWorker.REQUEST)
                    } else {
                        scheduler.cancel(ContinueWatchingSyncWorker.WORKER_NAME)
                        scheduler.cancel(PendingUploadsWorker.WORKER_NAME)
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
            pendingUploadsInteractor.executeSync()
            syncInteractor.executeSync(SyncContinueWatchingInteractor.Param())
            logger.debug(TAG, "Reconnect sync completed successfully")
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            logger.warning(TAG, "Reconnect sync failed", throwable)
        }
    }

    private companion object {
        private const val TAG = "ContinueWatchingTasksInitializer"
    }
}
