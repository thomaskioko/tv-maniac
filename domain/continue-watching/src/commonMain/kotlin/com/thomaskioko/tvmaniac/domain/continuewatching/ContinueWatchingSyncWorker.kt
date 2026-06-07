package com.thomaskioko.tvmaniac.domain.continuewatching

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.core.tasks.api.TaskConstraints
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.flags.ContinueWatchingNitroFlagQualifier
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class ContinueWatchingSyncWorker(
    private val syncContinueWatchingInteractor: Lazy<SyncContinueWatchingInteractor>,
    private val accountManager: Lazy<AccountManager>,
    @ContinueWatchingNitroFlagQualifier
    private val nitroFlag: FeatureFlag<Boolean>,
    private val logger: Logger,
) : BackgroundWorker {

    override val workerName: String = WORKER_NAME

    override suspend fun doWork(): WorkerResult {
        logger.debug(TAG, "Continue Watching sync worker starting")

        if (accountManager.value.getActiveProvider() == null) {
            logger.debug(TAG, "User not logged in, skipping sync")
            return WorkerResult.Success
        }

        return try {
            // TODO:: Move this to the repository layer
            val useNitro = nitroFlag.observe().first()
            syncContinueWatchingInteractor.value.executeSync(
                SyncContinueWatchingInteractor.Param(forceRefresh = true, useNitro = useNitro),
            )
            logger.debug(TAG, "Continue Watching sync completed successfully")
            WorkerResult.Success
        } catch (e: CancellationException) {
            logger.debug(TAG, "Continue Watching sync cancelled: ${e.message}")
            WorkerResult.Retry("Cancelled, will retry")
        } catch (e: Exception) {
            logger.error(TAG, "Continue Watching sync failed: ${e.message}")
            WorkerResult.Failure(e.message)
        }
    }

    internal companion object {
        internal const val WORKER_NAME = "com.thomaskioko.tvmaniac.watchlistsync"
        private const val TAG = "ContinueWatchingSyncWorker"
        private const val TWELVE_HOURS_MS = 12L * 60 * 60 * 1000

        internal val REQUEST = PeriodicTaskRequest(
            id = WORKER_NAME,
            intervalMs = TWELVE_HOURS_MS,
            constraints = TaskConstraints(requiresNetwork = true),
            longRunning = true,
        )
    }
}
