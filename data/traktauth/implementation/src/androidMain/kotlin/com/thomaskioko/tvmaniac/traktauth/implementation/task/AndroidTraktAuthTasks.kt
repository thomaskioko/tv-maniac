package com.thomaskioko.tvmaniac.traktauth.implementation.task

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorkerScheduler
import com.thomaskioko.tvmaniac.core.tasks.api.NetworkRequirement
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerConstraints
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.traktauth.api.TokenRefreshResult.NetworkError
import com.thomaskioko.tvmaniac.traktauth.api.TokenRefreshResult.Success
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthTasks
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, boundType = TraktAuthTasks::class)
public class AndroidTraktAuthTasks(
    private val scheduler: BackgroundWorkerScheduler,
    private val traktAuthRepository: Lazy<TraktAuthRepository>,
    private val logger: Logger,
) : TraktAuthTasks, BackgroundWorker {

    override val workerName: String = WORKER_NAME
    override val interval: Duration = REFRESH_INTERVAL
    override val constraints: WorkerConstraints = WorkerConstraints(NetworkRequirement.CONNECTED)

    override fun setup() {
        scheduler.register(this)
    }

    override fun scheduleTokenRefresh() {
        scheduler.schedulePeriodic(workerName)
    }

    override fun cancelTokenRefresh() {
        scheduler.cancel(workerName)
    }

    override suspend fun execute(): WorkerResult {
        logger.debug(TAG, "Token refresh running")
        val authState = traktAuthRepository.value.getAuthState() ?: return WorkerResult.Success
        if (!authState.isExpiringSoon()) return WorkerResult.Success

        return when (traktAuthRepository.value.refreshTokens()) {
            is Success -> WorkerResult.Success
            is NetworkError -> WorkerResult.Retry
            else -> WorkerResult.Failure
        }
    }

    private companion object {
        private const val TAG = "AndroidTraktAuthTasks"
        private const val WORKER_NAME = "token_refresh_worker"
        private val REFRESH_INTERVAL = 120.hours
    }
}
