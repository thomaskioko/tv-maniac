package com.thomaskioko.tvmaniac.app.test

import android.app.Application
import androidx.work.testing.WorkManagerTestInitHelper
import dev.zacsweers.metro.createGraphFactory

class TvManiacTestApplication : Application() {

    val graph: TestAppComponent by lazy {
        createGraphFactory<TestAppComponent.Factory>().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        WorkManagerTestInitHelper.initializeTestWorkManager(this)
    }
}
