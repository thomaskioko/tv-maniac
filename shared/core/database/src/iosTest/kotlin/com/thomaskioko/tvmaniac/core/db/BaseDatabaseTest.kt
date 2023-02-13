package com.thomaskioko.tvmaniac.core.db

import co.touchlab.sqliter.DatabaseConfiguration
import app.cash.sqldelight.driver.native.wrapConnection
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase

actual fun inMemorySqlDriver(): SqlDriver = NativeSqliteDriver(
    DatabaseConfiguration(
        name = "tvmaniac.test.db",
        version = 1,
        inMemory = true,
        create = { connection ->
            wrapConnection(connection) { TvManiacDatabase.Schema.create(it) }
        },
        upgrade = { connection, oldVersion, newVersion ->
            wrapConnection(connection) { TvManiacDatabase.Schema.migrate(it, oldVersion, newVersion) }
        }
    )
)
