package com.thomaskioko.tvmaniac.database.test

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase

actual fun inMemorySqlDriver(): SqlDriver =
  JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply { TvManiacDatabase.Schema.create(this) }
