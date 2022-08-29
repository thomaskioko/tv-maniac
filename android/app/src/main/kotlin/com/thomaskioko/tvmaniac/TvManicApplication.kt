package com.thomaskioko.tvmaniac

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.thomaskioko.tvmaniac.initializers.AppInitializers
import com.thomaskioko.tvmaniac.workmanager.ShowTasks
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TvManicApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject lateinit var initializers: AppInitializers

    @Inject lateinit var showTasks: ShowTasks

    override fun onCreate() {
        super.onCreate()
        initializers.init()

        showTasks.syncTmdbArtWorkWhenIdle()
        showTasks.syncTraktFollowedShowsWhenIdle()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }

}
