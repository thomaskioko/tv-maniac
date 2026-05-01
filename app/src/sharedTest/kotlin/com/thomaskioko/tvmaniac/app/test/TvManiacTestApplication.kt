package com.thomaskioko.tvmaniac.app.test

import android.app.Application
import androidx.work.testing.WorkManagerTestInitHelper
import dev.zacsweers.metro.createGraphFactory

class TvManiacTestApplication : Application() {

    private var testAppComponent: TestAppComponent? = null

    val graph: TestAppComponent
        get() = testAppComponent ?: createGraphFactory<TestAppComponent.Factory>().create(this)
            .also { testAppComponent = it }

    override fun onCreate() {
        super.onCreate()
        WorkManagerTestInitHelper.initializeTestWorkManager(this)
    }

    fun resetAppComponent() {
        testAppComponent = null
    }
}
