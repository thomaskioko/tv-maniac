package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.core.logger.Logger

internal const val DATABASE_NAME: String = "tvShows.db"

internal class MigrationDriverFactory(
    private val expectedVersion: Long,
    private val buildDriver: () -> SqlDriver,
    private val deleteDatabaseFile: () -> Unit,
    private val logger: Logger,
) {

    fun create(): SqlDriver {
        var driver: SqlDriver? = null
        return try {
            driver = buildDriver()
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
        deleteDatabaseFile()
        return buildDriver()
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
