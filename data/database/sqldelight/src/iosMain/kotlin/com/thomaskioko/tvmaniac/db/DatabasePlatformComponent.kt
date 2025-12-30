package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import co.touchlab.sqliter.JournalMode
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
public interface DatabasePlatformComponent {

    @Provides
    @SingleIn(AppScope::class)
    public fun provideSqlDriver(): SqlDriver = createNativeSqliteDriver(
        schema = TvManiacDatabase.Schema,
        name = "tvShows.db",
    )
}

public fun createNativeSqliteDriver(
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
