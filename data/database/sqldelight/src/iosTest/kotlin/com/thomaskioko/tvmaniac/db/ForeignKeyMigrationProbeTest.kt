package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import io.kotest.matchers.shouldBe
import kotlin.random.Random
import kotlin.test.Test

class ForeignKeyMigrationProbeTest {

    @Test
    fun `migration preserves children when run through the fk safe driver`() {
        val name = uniqueName("fk-safe")
        seedVersionOne(name)

        val driver = createNativeSqliteDriver(schema = schemaV2, name = name)
        val childCount = driver.countRows("child")
        driver.close()

        childCount shouldBe 1L
    }

    @Test
    fun `fk safe driver enforces foreign keys at runtime`() {
        val name = uniqueName("fk-runtime")

        val driver = createNativeSqliteDriver(schema = schemaV2, name = name)
        driver.execute(identifier = null, sql = "INSERT INTO parent (id, name, extra) VALUES (1, 'p', NULL)", parameters = 0)

        val rejectedOrphanChild = runCatching {
            driver.execute(identifier = null, sql = "INSERT INTO child (id, parent_id) VALUES (10, 999)", parameters = 0)
        }.isFailure
        driver.close()

        rejectedOrphanChild shouldBe true
    }

    @Test
    fun `naive fk on migration cascade wipes children`() {
        val name = uniqueName("fk-naive")
        seedVersionOne(name)

        // The pre-fix behavior: open with foreign keys enforced while the migration runs.
        val naive = NativeSqliteDriver(
            databaseConfiguration(schema = schemaV2, name = name, inMemory = false, foreignKeys = true),
        )
        val childCount = naive.countRows("child")
        naive.close()

        childCount shouldBe 0L
    }

    private fun seedVersionOne(name: String) {
        val driver = createNativeSqliteDriver(schema = schemaV1, name = name)
        driver.execute(identifier = null, sql = "INSERT INTO parent (id, name) VALUES (1, 'p')", parameters = 0)
        driver.execute(identifier = null, sql = "INSERT INTO child (id, parent_id) VALUES (10, 1)", parameters = 0)
        driver.close()
    }

    private fun uniqueName(prefix: String): String = "$prefix-${Random.nextInt(0, Int.MAX_VALUE)}.db"

    private fun SqlDriver.countRows(table: String): Long = executeQuery(
        identifier = null,
        sql = "SELECT COUNT(*) FROM $table",
        mapper = { cursor ->
            cursor.next()
            QueryResult.Value(cursor.getLong(0) ?: 0L)
        },
        parameters = 0,
    ).value

    private companion object {
        private fun child(driver: SqlDriver) {
            driver.execute(
                identifier = null,
                sql = """
                    CREATE TABLE child (
                        id INTEGER NOT NULL PRIMARY KEY,
                        parent_id INTEGER NOT NULL,
                        FOREIGN KEY(parent_id) REFERENCES parent(id) ON DELETE CASCADE
                    )
                """.trimIndent(),
                parameters = 0,
            )
        }

        val schemaV1: SqlSchema<QueryResult.Value<Unit>> = object : SqlSchema<QueryResult.Value<Unit>> {
            override val version: Long = 1

            override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
                driver.execute(null, "CREATE TABLE parent (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL)", 0)
                child(driver)
                return QueryResult.Value(Unit)
            }

            override fun migrate(
                driver: SqlDriver,
                oldVersion: Long,
                newVersion: Long,
                vararg callbacks: AfterVersion,
            ): QueryResult.Value<Unit> = QueryResult.Value(Unit)
        }

        val schemaV2: SqlSchema<QueryResult.Value<Unit>> = object : SqlSchema<QueryResult.Value<Unit>> {
            override val version: Long = 2

            override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
                driver.execute(null, "CREATE TABLE parent (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL, extra TEXT)", 0)
                child(driver)
                return QueryResult.Value(Unit)
            }

            override fun migrate(
                driver: SqlDriver,
                oldVersion: Long,
                newVersion: Long,
                vararg callbacks: AfterVersion,
            ): QueryResult.Value<Unit> {
                driver.execute(null, "CREATE TABLE parent_new (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL, extra TEXT)", 0)
                driver.execute(null, "INSERT INTO parent_new (id, name) SELECT id, name FROM parent", 0)
                driver.execute(null, "DROP TABLE parent", 0)
                driver.execute(null, "ALTER TABLE parent_new RENAME TO parent", 0)
                return QueryResult.Value(Unit)
            }
        }
    }
}
