package com.thomaskioko.tvmaniac.datasource.cache.db

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase

actual class DriverFactory {

    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(TvManiacDatabase.Schema, "tvShows.db")
    }
}