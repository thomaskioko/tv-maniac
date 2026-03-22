package com.thomaskioko.tvmaniac.domain.upnext

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.core.tasks.api.TaskConstraints
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import kotlinx.coroutines.CancellationException
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, boundType = BackgroundWorker::class, multibinding = true)
public class UpNextSyncWorker(
    private val refreshUpNextInteractor: Lazy<RefreshUpNextInteractor>,
    private val traktAuthRepository: Lazy<TraktAuthRepository>,
    private val logger: Logger,
) : BackgroundWorker {

    override val workerName: String = WORKER_NAME

    override suspend fun doWork(): WorkerResult {
        logger.debug(TAG, "Up next sync worker starting")

        if (!traktAuthRepository.value.isLoggedIn()) {
            logger.debug(TAG, "User not logged in, skipping sync")
            return WorkerResult.Success
        }

        return try {
            refreshUpNextInteractor.value.executeSync(true)
            logger.debug(TAG, "Up next sync completed successfully")
            WorkerResult.Success
        } catch (e: CancellationException) {
            logger.debug(TAG, "Up next sync cancelled: ${e.message}")
            WorkerResult.Retry("Cancelled, will retry")
        } catch (e: Exception) {
            logger.error(TAG, "Up next sync failed: ${e.message}")
            WorkerResult.Failure(e.message)
        }
    }

    internal companion object {
        internal const val WORKER_NAME = "com.thomaskioko.tvmaniac.upnextsync"
        private const val TAG = "UpNextSyncWorker"
        private const val SIX_HOURS_MS = 6L * 60 * 60 * 1000

        internal val REQUEST = PeriodicTaskRequest(
            id = WORKER_NAME,
            intervalMs = SIX_HOURS_MS,
            constraints = TaskConstraints(requiresNetwork = true),
        )
    }
}
