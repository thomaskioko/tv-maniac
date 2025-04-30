package com.thomaskioko.tvmaniac.database.test

import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.createNativeSqliteDriver

internal actual fun createTestSqlDriver(name: String): SqlDriver {
  return createNativeSqliteDriver(schema = TvManiacDatabase.Schema, name = name, inMemory = true)
}
