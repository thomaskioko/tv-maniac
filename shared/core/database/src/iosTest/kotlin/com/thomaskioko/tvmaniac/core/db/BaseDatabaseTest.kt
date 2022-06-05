package com.thomaskioko.tvmaniac.core.db

import co.touchlab.sqliter.DatabaseConfiguration
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.squareup.sqldelight.drivers.native.wrapConnection
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase

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
