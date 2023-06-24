package com.thomaskioko.tvmaniac.workmanager

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import me.tatarka.inject.annotations.Inject
import java.util.concurrent.TimeUnit

@Inject
class ShowTasksImpl(
    private val workManager: WorkManager,
) : ShowTasks {

    override fun setupDiscoverDailySyncs() {
        val request = PeriodicWorkRequestBuilder<SyncDiscoverShowsWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build(),
        ).build()

        workManager.enqueueUniquePeriodicWork(
            SyncDiscoverShowsWorker.DAILY_SYNC_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    override fun syncTraktFollowedShows() {
        val request = OneTimeWorkRequestBuilder<SyncWatchlist>()
            .addTag(SyncWatchlist.TAG)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .build(),
            ).build()

        workManager.enqueue(request)
    }
}
