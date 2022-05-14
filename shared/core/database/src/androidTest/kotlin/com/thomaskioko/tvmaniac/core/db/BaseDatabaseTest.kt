package com.thomaskioko.tvmaniac.core.db

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase

actual fun inMemorySqlDriver(): SqlDriver =
    JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
        TvManiacDatabase.Schema.create(this)
    }
