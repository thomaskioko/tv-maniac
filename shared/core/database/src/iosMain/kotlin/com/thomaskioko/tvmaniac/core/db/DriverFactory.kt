package com.thomaskioko.tvmaniac.core.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase

actual class DriverFactory {

    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(TvManiacDatabase.Schema, "tvShows.db")
    }
}
