package com.thomaskioko.tvmaniac.traktauth.implementation.task

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
public class TokenRefreshWorker(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val traktAuthRepository: Lazy<TraktAuthRepository>,
    private val logger: Lazy<Logger>,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        logger.value.debug("TokenRefreshWorker", "Token refresh running")
        val authState = traktAuthRepository.value.getAuthState() ?: return Result.success()
        if (!authState.isExpiringSoon()) return Result.success()

        val refreshToken = traktAuthRepository.value.refreshTokens()

        return when {
            refreshToken != null -> Result.success()
            else -> Result.failure()
        }
    }

    public companion object {
        internal const val NAME = "token_refresh_work"
    }
}
