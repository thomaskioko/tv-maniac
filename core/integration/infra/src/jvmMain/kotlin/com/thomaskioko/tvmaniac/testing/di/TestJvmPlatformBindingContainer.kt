package com.thomaskioko.tvmaniac.testing.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.datastore.implementation.DATA_STORE_FILE_NAME
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import okio.Path.Companion.toPath
import java.nio.file.Files

@BindingContainer
@ContributesTo(AppScope::class)
public object TestJvmPlatformBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideSqlDriver(): SqlDriver {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        TvManiacDatabase.Schema.create(driver)
        return driver
    }

    @Provides
    @SingleIn(AppScope::class)
    public fun provideDataStore(
        @IoCoroutineScope scope: CoroutineScope,
    ): DataStore<Preferences> {
        val tempDir = Files.createTempDirectory("tvmaniac-test-datastore")
        tempDir.toFile().deleteOnExit()
        return PreferenceDataStoreFactory.createWithPath(
            corruptionHandler = null,
            migrations = emptyList(),
            scope = scope,
            produceFile = { tempDir.resolve(DATA_STORE_FILE_NAME).toString().toPath() },
        )
    }
}
