package com.thomaskioko.tvmaniac.workmanager

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ShowTasksImpl @Inject constructor(
    private val workManager: WorkManager
) : ShowTasks {

    override fun setupDiscoverDailySyncs() {
        val request = PeriodicWorkRequestBuilder<SyncDiscoverShowsWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()
        ).build()

        workManager.enqueueUniquePeriodicWork(
            SyncDiscoverShowsWorker.DAILY_SYNC_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    override fun syncTraktFollowedShows() {
        val request = OneTimeWorkRequestBuilder<SyncFollowedShows>()
            .addTag(SyncFollowedShows.TAG)
            .build()
        workManager.enqueue(request)
    }

    override fun syncTraktFollowedShowsWhenIdle() {
        val request = OneTimeWorkRequestBuilder<SyncFollowedShows>()
            .addTag(SyncFollowedShows.TAG)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresDeviceIdle(true)
                    .build()
            )
            .build()
        workManager.enqueue(request)
    }
}
