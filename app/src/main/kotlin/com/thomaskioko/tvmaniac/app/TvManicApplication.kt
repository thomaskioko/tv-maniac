package com.thomaskioko.tvmaniac.app

import android.app.Application
import android.os.Build
import android.os.StrictMode
import androidx.work.Configuration
import androidx.work.WorkerFactory
import com.thomaskioko.tvmaniac.app.di.ApplicationGraph
import com.thomaskioko.tvmaniac.core.deeplink.api.DeepLinkUrls
import com.thomaskioko.tvmaniac.domain.widget.ObserveWidgetShowsInteractor
import com.thomaskioko.tvmaniac.domain.widget.ObserveWidgetThemeInteractor
import com.thomaskioko.tvmaniac.domain.widget.WidgetTasksInitializer
import com.thomaskioko.tvmaniac.ui.widget.di.WidgetGraph
import dev.zacsweers.metro.createGraphFactory

public class TvManicApplication : Application(), Configuration.Provider, WidgetGraph {
    private val graph: ApplicationGraph by lazy(LazyThreadSafetyMode.NONE) {
        createGraphFactory<ApplicationGraph.Factory>()
            .create(this, BuildConfig.DEBUG)
    }

    private lateinit var workerFactory: WorkerFactory

    override val observeWidgetShowsInteractor: ObserveWidgetShowsInteractor
        get() = graph.observeWidgetShowsInteractor

    override val observeWidgetThemeInteractor: ObserveWidgetThemeInteractor
        get() = graph.observeWidgetThemeInteractor

    override val widgetTasksInitializer: WidgetTasksInitializer
        get() = graph.widgetTasksInitializer

    override val deepLinkUrls: DeepLinkUrls
        get() = graph.deepLinkUrls

    override fun onCreate() {
        super.onCreate()
        workerFactory = graph.workerFactory

        if (graph.debugConfig.isDebug) {
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
