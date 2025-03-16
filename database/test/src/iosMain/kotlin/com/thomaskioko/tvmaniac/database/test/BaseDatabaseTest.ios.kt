package com.thomaskioko.tvmaniac.database.test

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import com.thomaskioko.tvmaniac.db.TvManiacDatabase

internal actual fun createTestSqlDriver(name: String): SqlDriver {
  return inMemoryDriver(TvManiacDatabase.Schema, name)
}

private fun inMemoryDriver(
  schema: SqlSchema<QueryResult.Value<Unit>>,
  name: String,
): NativeSqliteDriver = NativeSqliteDriver(
  DatabaseConfiguration(
    name = name,
    inMemory = true,
    version = if (schema.version > Int.MAX_VALUE) {
      error("Schema version is larger than Int.MAX_VALUE: ${schema.version}.")
    } else {
      schema.version.toInt()
    },
    create = { connection ->
      wrapConnection(connection) { schema.create(it) }
    },
    upgrade = { connection, oldVersion, newVersion ->
      wrapConnection(connection) { schema.migrate(it, oldVersion.toLong(), newVersion.toLong()) }
    },
  ),
)
