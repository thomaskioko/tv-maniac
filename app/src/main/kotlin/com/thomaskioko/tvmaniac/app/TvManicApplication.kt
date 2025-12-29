package com.thomaskioko.tvmaniac.app

import android.app.Application
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
        workerFactory = component.workerFactory
        component.initializers.initialize()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    internal fun getApplicationComponent() = component
}
