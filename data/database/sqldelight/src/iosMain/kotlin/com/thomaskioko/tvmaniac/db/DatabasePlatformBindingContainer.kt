package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.SqlDriver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import platform.Foundation.NSProcessInfo

private const val CLEAR_STATE_ENV = "TVMANIAC_CLEAR_STATE"

@BindingContainer
@ContributesTo(AppScope::class)
public object DatabasePlatformBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideSqlDriver(factory: MigrationDriverFactory): SqlDriver {
        if (shouldStartFromAnEmptyDatabase()) IosDatabaseDriverBuilder().deleteDatabase()
        return factory.create()
    }

    private fun shouldStartFromAnEmptyDatabase(): Boolean =
        NSProcessInfo.processInfo.environment[CLEAR_STATE_ENV] as String? == "1"
}
