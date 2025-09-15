package com.thomaskioko.tvmaniac.database.test

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.thomaskioko.tvmaniac.db.TvManiacDatabase

internal actual fun createTestSqlDriver(name: String): SqlDriver {
    return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { db ->
        TvManiacDatabase.Schema.create(db)
        db.execute(null, "PRAGMA foreign_keys=ON", 0)
    }
}
