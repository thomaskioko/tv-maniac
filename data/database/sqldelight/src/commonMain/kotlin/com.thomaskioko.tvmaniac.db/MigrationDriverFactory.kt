package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.core.logger.Logger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
public class MigrationDriverFactory(
    private val driverBuilder: DatabaseDriverBuilder,
    private val logger: Logger,
) {

    private val expectedVersion: Long = TvManiacDatabase.Schema.version

    public fun create(): SqlDriver {
        var driver: SqlDriver? = null
        return try {
            driver = driverBuilder.build()
            val version = driver.userVersion()
            if (version == expectedVersion) {
                driver
            } else {
                logger.warning(LOG_TAG, "On-disk schema version $version does not match $expectedVersion; rebuilding database")
                closeQuietly(driver)
                rebuild()
            }
        } catch (throwable: Throwable) {
            logger.warning(LOG_TAG, "Database open or migration failed (${throwable.message}); rebuilding database")
            closeQuietly(driver)
            rebuild()
        }
    }

    private fun rebuild(): SqlDriver {
        driverBuilder.deleteDatabase()
        return driverBuilder.build()
    }

    private fun closeQuietly(driver: SqlDriver?) {
        try {
            driver?.close()
        } catch (throwable: Throwable) {
            logger.warning(LOG_TAG, "Failed to close the database driver before rebuild: ${throwable.message}")
        }
    }

    private companion object {
        const val LOG_TAG: String = "DatabaseMigration"
    }
}

internal fun SqlDriver.userVersion(): Long =
    executeQuery(
        identifier = null,
        sql = "PRAGMA user_version",
        mapper = { cursor ->
            cursor.next()
            QueryResult.Value(cursor.getLong(0) ?: 0L)
        },
        parameters = 0,
    ).value
