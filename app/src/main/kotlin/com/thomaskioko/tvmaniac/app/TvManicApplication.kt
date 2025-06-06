package com.thomaskioko.tvmaniac.app

import android.app.Application
import com.thomaskioko.tvmaniac.core.base.extensions.unsafeLazy
import com.thomaskioko.tvmaniac.inject.ApplicationComponent
import com.thomaskioko.tvmaniac.inject.create

class TvManicApplication : Application() {
    private val component: ApplicationComponent by unsafeLazy {
        ApplicationComponent::class.create(
            this,
        )
    }

    override fun onCreate() {
        super.onCreate()
        component.initializers.initialize()
    }

    fun getApplicationComponent() = component
}
