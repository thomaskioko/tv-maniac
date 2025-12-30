package com.thomaskioko.tvmaniac.app

import android.app.Application
import android.os.Build
import android.os.StrictMode
import androidx.work.Configuration
import androidx.work.WorkerFactory
import com.thomaskioko.tvmaniac.core.base.extensions.unsafeLazy
import com.thomaskioko.tvmaniac.inject.ApplicationComponent
import com.thomaskioko.tvmaniac.inject.create

public class TvManicApplication : Application(), Configuration.Provider {
    private val component: ApplicationComponent by unsafeLazy {
        ApplicationComponent::class.create(
            this,
        )
    }

    private lateinit var workerFactory: WorkerFactory

    override fun onCreate() {
        super.onCreate()
        setupStrictMode()

        workerFactory = component.workerFactory
        component.initializers.initialize()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    internal fun getApplicationComponent() = component
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
