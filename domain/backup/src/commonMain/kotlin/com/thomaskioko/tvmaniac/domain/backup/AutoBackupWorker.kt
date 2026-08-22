package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.core.tasks.api.TaskConstraints
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.datastore.api.AutoBackupInterval
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class AutoBackupWorker(
    private val runAutoBackupInteractor: Lazy<RunAutoBackupInteractor>,
) : BackgroundWorker {

    override val workerName: String = WORKER_NAME

    override suspend fun doWork(): WorkerResult =
        when (val outcome = runAutoBackupInteractor.value.executeSync(Unit)) {
            is AutoBackupResult.Success -> WorkerResult.Success
            is AutoBackupResult.NoLocation -> WorkerResult.Success
            is AutoBackupResult.LocationLost -> WorkerResult.Failure(LOCATION_LOST_REASON)
            is AutoBackupResult.Failed -> WorkerResult.Retry(outcome.message)
        }

    public companion object {
        public const val WORKER_NAME: String = "com.thomaskioko.tvmaniac.autobackup"
        private const val LOCATION_LOST_REASON = "LocationUnavailable"
        private const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000

        public fun request(interval: AutoBackupInterval): PeriodicTaskRequest = PeriodicTaskRequest(
            id = WORKER_NAME,
            intervalMs = interval.days * MILLIS_PER_DAY,
            constraints = TaskConstraints(),
            longRunning = true,
        )
    }
}
