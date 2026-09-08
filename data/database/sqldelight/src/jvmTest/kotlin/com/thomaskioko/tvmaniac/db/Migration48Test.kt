package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.enableForeignKeys
import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration48Test {

    @Test
    fun `should reference lists given list_shows is migrated`() {
        openSnapshot(version = 48).use { driver ->
            migrateToVersion(driver, oldVersion = 48, newVersion = 49)

            driver.foreignKeyReferencedTable("list_shows") shouldBe "lists"
        }
    }

    @Test
    fun `should keep a row and its columns given its list exists`() {
        openSnapshot(version = 48).use { driver ->
            driver.addList(id = 1L, name = "Watchlist")
            driver.addListShow(listId = 1L, tmdbId = 100L, listedAt = "2024-01-01T00:00:00Z", pendingAction = "UPLOAD")

            migrateToVersion(driver, oldVersion = 48, newVersion = 49)

            driver.selectListShow(listId = 1L, tmdbId = 100L) shouldBe ListShowRow(
                listId = 1L,
                tmdbId = 100L,
                listedAt = "2024-01-01T00:00:00Z",
                pendingAction = "UPLOAD",
            )
        }
    }

    @Test
    fun `should drop a row given its list no longer exists`() {
        openSnapshot(version = 48).use { driver ->
            driver.addListShow(listId = 999L, tmdbId = 100L, listedAt = "2024-01-01T00:00:00Z", pendingAction = "NOTHING")

            migrateToVersion(driver, oldVersion = 48, newVersion = 49)

            driver.listShowRowCount() shouldBe 0L
        }
    }

    @Test
    fun `should cascade delete list_shows rows given a list is deleted after migration`() {
        openSnapshot(version = 48).use { driver ->
            driver.addList(id = 1L, name = "Watchlist")
            driver.addListShow(listId = 1L, tmdbId = 100L, listedAt = "2024-01-01T00:00:00Z", pendingAction = "NOTHING")

            migrateToVersion(driver, oldVersion = 48, newVersion = 49)
            driver.enableForeignKeys()
            driver.deleteList(id = 1L)

            driver.listShowRowCount() shouldBe 0L
        }
    }
}

private fun SqlDriver.addList(id: Long, name: String) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO lists (id, name, created_at)
            VALUES ($id, '$name', '2024-01-01T00:00:00Z')
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.deleteList(id: Long) {
    execute(
        identifier = null,
        sql = "DELETE FROM lists WHERE id = $id",
        parameters = 0,
    )
}

private fun SqlDriver.addListShow(listId: Long, tmdbId: Long, listedAt: String, pendingAction: String) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO list_shows (list_id, tmdb_id, listed_at, pending_action)
            VALUES ($listId, $tmdbId, '$listedAt', '$pendingAction')
        """.trimIndent(),
        parameters = 0,
    )
}

private data class ListShowRow(
    val listId: Long,
    val tmdbId: Long,
    val listedAt: String,
    val pendingAction: String,
)

private fun SqlDriver.selectListShow(listId: Long, tmdbId: Long): ListShowRow? = executeQuery(
    identifier = null,
    sql = """
        SELECT list_id, tmdb_id, listed_at, pending_action
        FROM list_shows
        WHERE list_id = $listId AND tmdb_id = $tmdbId
    """.trimIndent(),
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        QueryResult.Value(
            if (cursor.next().value) {
                ListShowRow(
                    listId = cursor.getLong(0)!!,
                    tmdbId = cursor.getLong(1)!!,
                    listedAt = cursor.getString(2)!!,
                    pendingAction = cursor.getString(3)!!,
                )
            } else {
                null
            },
        )
    },
).value

private fun SqlDriver.listShowRowCount(): Long = executeQuery(
    identifier = null,
    sql = "SELECT COUNT(*) FROM list_shows",
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        cursor.next()
        QueryResult.Value(cursor.getLong(0) ?: 0L)
    },
).value

private fun SqlDriver.foreignKeyReferencedTable(table: String): String? = executeQuery(
    identifier = null,
    sql = "PRAGMA foreign_key_list($table)",
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        QueryResult.Value(if (cursor.next().value) cursor.getString(2) else null)
    },
).value
