package com.thomaskioko.tvmaniac.db.util

import app.cash.sqldelight.Query
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.JdbcDriver
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import java.io.File
import java.nio.file.Files
import java.sql.Connection
import java.sql.DriverManager

private val SCHEMAS_DIR: File by lazy {
    val path = System.getProperty("tvmaniac.sqldelight.schemas.dir")
        ?: error(
            "tvmaniac.sqldelight.schemas.dir system property not set. " +
                "Configure it via the jvmTest task in build.gradle.kts.",
        )
    File(path)
}

internal fun openSnapshot(version: Int): SqlDriver {
    val source = File(SCHEMAS_DIR, "$version.db")
    check(source.exists()) { "Schema snapshot not found: ${source.absolutePath}" }
    val temp = Files.createTempFile("tvmaniac-migration-$version-", ".db").toFile()
    temp.deleteOnExit()
    source.copyTo(temp, overwrite = true)

    val connection = DriverManager.getConnection("jdbc:sqlite:${temp.absolutePath}")
    return PinnedJdbcDriver(connection)
}

internal fun migrateToCurrent(driver: SqlDriver, oldVersion: Int) {
    TvManiacDatabase.Schema.migrate(
        driver = driver,
        oldVersion = oldVersion.toLong(),
        newVersion = TvManiacDatabase.Schema.version,
    )
}

internal fun SqlDriver.tableNames(): Set<String> = executeQuery(
    identifier = null,
    sql = "SELECT name FROM sqlite_master WHERE type = 'table' ORDER BY name",
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        val names = mutableSetOf<String>()
        while (cursor.next().value) {
            cursor.getString(0)?.let(names::add)
        }
        QueryResult.Value(names.toSet())
    },
).value

private class PinnedJdbcDriver(private val connection: Connection) : JdbcDriver() {
    override fun getConnection(): Connection = connection

    override fun closeConnection(connection: Connection) = Unit

    override fun close() {
        connection.close()
    }

    override fun addListener(vararg queryKeys: String, listener: Query.Listener) = Unit

    override fun removeListener(vararg queryKeys: String, listener: Query.Listener) = Unit

    override fun notifyListeners(vararg queryKeys: String) = Unit
}
