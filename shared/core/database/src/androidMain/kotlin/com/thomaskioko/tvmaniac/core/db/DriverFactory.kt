package com.thomaskioko.tvmaniac.core.db

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase

actual class DriverFactory(private val context: Context) {

    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(TvManiacDatabase.Schema, context, "tvShows.db")
    }
}
