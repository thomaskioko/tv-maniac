package com.thomaskioko.tvmaniac.datasource.cache

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DriverFactory {

    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(TvManiacDatabase.Schema, "tvShows.db")
    }
}