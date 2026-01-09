package com.thomaskioko.tvmaniac.core.tasks.implementation

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.SyncTasks
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import java.util.concurrent.TimeUnit

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidSyncTasks(
    workManager: Lazy<WorkManager>,
    private val logger: Logger,
) : SyncTasks {
    private val workManager by workManager

    override fun scheduleLibrarySync() {
        logger.debug(TAG, "Scheduling library sync work")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val syncWork = PeriodicWorkRequestBuilder<LibrarySyncWorker>(
            repeatInterval = SYNC_INTERVAL_HOURS,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            LibrarySyncWorker.NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            syncWork,
        )
    }

    override fun cancelLibrarySync() {
        logger.debug(TAG, "Cancelling library sync work")
        workManager.cancelUniqueWork(LibrarySyncWorker.NAME)
    }

    private companion object {
        private const val TAG = "AndroidSyncTasks"
        private const val SYNC_INTERVAL_HOURS = 12L
    }
}
