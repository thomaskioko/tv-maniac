package com.thomaskioko.tvmaniac

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.thomaskioko.tvmaniac.workmanager.SyncDiscoverShowsWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class TvManicApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var workManager: WorkManager

    override fun onCreate() {
        super.onCreate()
        syncDiscoverShows()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }

    //TODO:: Move this implementation to a separate class.
    private fun syncDiscoverShows() {
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

}
