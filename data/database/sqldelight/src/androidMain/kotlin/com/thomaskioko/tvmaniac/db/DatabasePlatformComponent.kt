package com.thomaskioko.tvmaniac.db

import android.app.Application
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
interface DatabasePlatformComponent {

    @Provides
    @SingleIn(AppScope::class)
    fun provideSqlDriver(application: Application): SqlDriver =
        AndroidSqliteDriver(
            schema = TvManiacDatabase.Schema,
            context = application,
            name = "tvShows.db",
            callback = object : AndroidSqliteDriver.Callback(TvManiacDatabase.Schema) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    super.onConfigure(db)
                    db.enableWriteAheadLogging()
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            },
        )
}
