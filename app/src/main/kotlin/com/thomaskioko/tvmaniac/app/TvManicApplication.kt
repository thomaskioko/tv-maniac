package com.thomaskioko.tvmaniac.app

import android.app.Application
import android.os.Build
import android.os.StrictMode
import androidx.work.Configuration
import androidx.work.WorkerFactory
import com.thomaskioko.tvmaniac.app.di.ApplicationGraph
import com.thomaskioko.tvmaniac.core.base.extensions.unsafeLazy
import dev.zacsweers.metro.createGraphFactory

public class TvManicApplication : Application(), Configuration.Provider {
    private val graph: ApplicationGraph by unsafeLazy {
        createGraphFactory<ApplicationGraph.Factory>().create(this)
    }

    private lateinit var workerFactory: WorkerFactory

    override fun onCreate() {
        super.onCreate()
        workerFactory = graph.workerFactory

        if (graph.appInfo.debugBuild) {
            setupStrictMode()
        }

        graph.initializers.initialize()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    internal fun getApplicationGraph() = graph
}

private fun setupStrictMode() {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build(),
    )

    StrictMode.setVmPolicy(
        StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectActivityLeaks()
            .detectLeakedClosableObjects()
            .detectLeakedRegistrationObjects()
            .detectFileUriExposure()
            .detectCleartextNetwork()
            .apply {
                if (Build.VERSION.SDK_INT >= 31) {
                    detectIncorrectContextUse()
                    detectUnsafeIntentLaunch()
                }
            }
            .penaltyLog()
            .build(),
    )
}
