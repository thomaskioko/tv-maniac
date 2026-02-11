package com.thomaskioko.tvmaniac.domain.library

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTask
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundTaskRegistry
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, boundType = SyncTasks::class)
@ContributesBinding(AppScope::class, boundType = BackgroundTask::class, multibinding = true)
public class IosLibrarySyncTasks(
    private val registry: BackgroundTaskRegistry,
    private val syncLibraryInteractor: Lazy<SyncLibraryInteractor>,
    private val traktAuthRepository: Lazy<TraktAuthRepository>,
    private val datastoreRepository: Lazy<DatastoreRepository>,
    private val dateTimeProvider: Lazy<DateTimeProvider>,
    private val logger: Logger,
) : SyncTasks, BackgroundTask {

    private val syncMutex = Mutex()

    override val taskId: String = TASK_ID
    override val interval: Double = SYNC_INTERVAL_SECONDS

    override fun setup() {
        registry.register(this)
    }

    override fun scheduleLibrarySync() {
        registry.scheduleAndExecute(taskId)
    }

    override fun cancelLibrarySync() {
        registry.cancel(taskId)
    }

    override suspend fun execute() {
        syncMutex.withLock {
            if (!traktAuthRepository.value.isLoggedIn()) {
                logger.debug(TAG, "User not logged in, skipping sync")
                return
            }

            syncLibraryInteractor.value.executeSync(
                SyncLibraryInteractor.Param(forceRefresh = true),
            )
            datastoreRepository.value.setLastSyncTimestamp(dateTimeProvider.value.nowMillis())
        }
    }

    private companion object {
        private const val TAG = "IosLibrarySyncTasks"
        private const val TASK_ID = "com.thomaskioko.tvmaniac.librarysync"
        private const val SYNC_INTERVAL_SECONDS = 24.0 * 60 * 60
    }
}
