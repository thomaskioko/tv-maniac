package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import co.touchlab.sqliter.JournalMode
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
interface DatabasePlatformComponent {

    @Provides
    @SingleIn(AppScope::class)
    fun provideSqlDriver(): SqlDriver = createNativeSqliteDriver(
        schema = TvManiacDatabase.Schema,
        name = "tvShows.db",
    )
}

fun createNativeSqliteDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    name: String,
    inMemory: Boolean = false,
): NativeSqliteDriver = NativeSqliteDriver(
    DatabaseConfiguration(
        name = name,
        inMemory = inMemory,
        journalMode = JournalMode.WAL,
        version = schema.version.toInt(),
        create = { connection ->
            wrapConnection(connection) { schema.create(it) }
        },
        upgrade = { connection, oldVersion, newVersion ->
            wrapConnection(connection) { schema.migrate(it, oldVersion.toLong(), newVersion.toLong()) }
        },
        extendedConfig = DatabaseConfiguration.Extended(foreignKeyConstraints = true),
    ),
)
