package com.thomaskioko.tvmaniac.testing.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

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
}
