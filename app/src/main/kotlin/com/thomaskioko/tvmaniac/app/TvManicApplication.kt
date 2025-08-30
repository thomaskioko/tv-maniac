package com.thomaskioko.tvmaniac.app

import android.app.Application
import com.thomaskioko.tvmaniac.core.base.extensions.unsafeLazy
import com.thomaskioko.tvmaniac.inject.ApplicationGraph
import dev.zacsweers.metro.createGraphFactory

class TvManicApplication : Application() {
    private val component: ApplicationGraph by unsafeLazy {
        createGraphFactory<ApplicationGraph.Factory>().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        component.initializers.initialize()
    }

    fun getApplicationComponent() = component
}
