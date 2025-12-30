package com.thomaskioko.tvmaniac.db

import android.app.Application
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
public interface DatabasePlatformComponent {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideSqlDriver(application: Application): SqlDriver =
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
