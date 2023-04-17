package com.thomaskioko.tvmaniac

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkerFactory
import com.thomaskioko.tvmaniac.util.extensions.unsafeLazy
import com.thomaskioko.tvmaniac.inject.ApplicationComponent
import com.thomaskioko.tvmaniac.inject.create

class TvManicApplication : Application(), Configuration.Provider {

    val component: ApplicationComponent by unsafeLazy { ApplicationComponent::class.create(this) }

    private lateinit var workerFactory: WorkerFactory

    override fun onCreate() {
        super.onCreate()

        workerFactory = component.workerFactory

        component.initializers.init()

    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }

}
