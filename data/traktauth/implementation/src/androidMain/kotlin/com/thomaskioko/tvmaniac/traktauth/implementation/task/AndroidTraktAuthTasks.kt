package com.thomaskioko.tvmaniac.traktauth.implementation.task

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthTasks
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import java.util.concurrent.TimeUnit

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidTraktAuthTasks(
    workManager: Lazy<WorkManager>,
    private val logger: Logger,
) : TraktAuthTasks {
    private val workManager by workManager

    override fun scheduleTokenRefresh() {
        logger.debug(TAG, "Scheduling token refresh work")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val refreshWork = PeriodicWorkRequestBuilder<TokenRefreshWorker>(
            repeatInterval = REFRESH_INTERVAL_HOURS,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            TokenRefreshWorker.NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            refreshWork,
        )
    }

    override fun cancelTokenRefresh() {
        logger.debug(TAG, "Cancelling token refresh work")
        workManager.cancelUniqueWork(TokenRefreshWorker.NAME)
    }

    public companion object {
        private const val TAG = "AndroidTraktAuthTasks"
        private const val REFRESH_INTERVAL_HOURS = 120L // 5 days
    }
}
