package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.PeriodicTaskRequest
import com.thomaskioko.tvmaniac.core.tasks.api.TaskConstraints
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.traktauth.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, boundType = BackgroundWorker::class, multibinding = true)
public class TokenRefreshWorker(
    private val traktAuthRepository: Lazy<TraktAuthRepository>,
    private val logger: Logger,
) : BackgroundWorker {

    override val workerName: String = WORKER_NAME

    override suspend fun doWork(): WorkerResult {
        logger.debug(TAG, "Token refresh running")
        val authState = traktAuthRepository.value.getAuthState() ?: return WorkerResult.Success
        if (!authState.isExpiringSoon()) return WorkerResult.Success

        return when (traktAuthRepository.value.refreshTokens()) {
            is TokenRefreshResult.Success -> WorkerResult.Success
            is TokenRefreshResult.NetworkError -> WorkerResult.Retry("Network error during token refresh")
            else -> WorkerResult.Failure("Token refresh failed")
        }
    }

    internal companion object {
        internal const val WORKER_NAME = "com.thomaskioko.tvmaniac.tokenrefresh"
        private const val TAG = "TokenRefreshWorker"
        private const val FIVE_DAYS_MS = 5L * 24 * 60 * 60 * 1000

        internal val REQUEST = PeriodicTaskRequest(
            id = WORKER_NAME,
            intervalMs = FIVE_DAYS_MS,
            constraints = TaskConstraints(requiresNetwork = true),
        )
    }
}
