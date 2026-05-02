package com.thomaskioko.tvmaniac.app.test

import android.app.Application
import androidx.work.testing.WorkManagerTestInitHelper
import com.thomaskioko.tvmaniac.datastore.implementation.DATA_STORE_FILE_NAME
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

    /**
     * Clears the file-backed state owned by the current process so a subsequent test attempt sees
     * a cold database and an empty preferences store. [resetAppComponent] only drops the Metro graph
     * reference, but the underlying SQLite file (`tvShows.db`) and DataStore preferences file persist
     * across attempts and would otherwise replay the watch-state and user-preference mutations the
     * previous attempt wrote.
     */
    fun clearPersistentTestState() {
        deleteDatabase(DATABASE_NAME)
        filesDir.resolve(DATA_STORE_FILE_NAME).delete()
        filesDir.resolve("$DATA_STORE_FILE_NAME.lock").delete()
    }

    private companion object {
        private const val DATABASE_NAME = "tvShows.db"
    }
}
