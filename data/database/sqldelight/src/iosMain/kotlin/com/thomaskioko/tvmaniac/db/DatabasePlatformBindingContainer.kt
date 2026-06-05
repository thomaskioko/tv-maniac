package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import co.touchlab.sqliter.DatabaseFileContext
import co.touchlab.sqliter.JournalMode
import com.thomaskioko.tvmaniac.core.logger.Logger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
@ContributesTo(AppScope::class)
public object DatabasePlatformBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideSqlDriver(logger: Logger): SqlDriver = MigrationDriverFactory(
        expectedVersion = TvManiacDatabase.Schema.version,
        buildDriver = { createNativeSqliteDriver(schema = TvManiacDatabase.Schema, name = DATABASE_NAME) },
        deleteDatabaseFile = { DatabaseFileContext.deleteDatabase(DATABASE_NAME) },
        logger = logger,
    ).create()
}

public fun createNativeSqliteDriver(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    name: String,
    inMemory: Boolean = false,
): NativeSqliteDriver {
    if (!inMemory) {
        val migrationDriver = NativeSqliteDriver(
            databaseConfiguration(schema, name, inMemory = false, foreignKeys = false),
        )
        // Force a connection so SQLiter runs the upgrade callback (Schema.migrate) now, then release
        // it. SQLiter opens lazily, so without a statement the migration would not run before close.
        try {
            migrationDriver.executeQuery(
                identifier = null,
                sql = "SELECT 1",
                mapper = { QueryResult.Value(Unit) },
                parameters = 0,
            )
        } finally {
            migrationDriver.close()
        }
    }
    return NativeSqliteDriver(databaseConfiguration(schema, name, inMemory = inMemory, foreignKeys = true))
}

internal fun databaseConfiguration(
    schema: SqlSchema<QueryResult.Value<Unit>>,
    name: String,
    inMemory: Boolean,
    foreignKeys: Boolean,
): DatabaseConfiguration = DatabaseConfiguration(
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
    extendedConfig = DatabaseConfiguration.Extended(foreignKeyConstraints = foreignKeys),
)
