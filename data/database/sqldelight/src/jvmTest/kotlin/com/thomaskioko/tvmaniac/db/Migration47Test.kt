package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration47Test {

    @Test
    fun `should replace trakt_lists and trakt_list_shows with lists and list_shows`() {
        openSnapshot(version = 47).use { driver ->
            migrateToVersion(driver, oldVersion = 47, newVersion = 48)

            val listColumns = driver.columnNames("lists")
            listColumns shouldContain "trakt_id"
            listColumns shouldContain "pending_action"

            val listShowColumns = driver.columnNames("list_shows")
            listShowColumns shouldContain "tmdb_id"
            listShowColumns shouldNotContain "trakt_id"
        }
    }

    @Test
    fun `should carry an old list into a synced list with its item keyed by tmdb id`() {
        openSnapshot(version = 47).use { driver ->
            driver.addTvshow(internalId = 1L, tmdbId = 900L)
            driver.addExternalId(internalId = 1L, traktId = 500L)
            driver.addTraktList(id = 10L, slug = "watchlist", name = "Watchlist")
            driver.addTraktListShow(listId = 10L, traktId = 500L)

            migrateToVersion(driver, oldVersion = 47, newVersion = 48)

            driver.listTraktIds() shouldBe listOf(10L)
            driver.listPendingActions() shouldBe listOf("NOTHING")
            driver.listShowTmdbIds() shouldBe listOf(900L)
        }
    }

    @Test
    fun `should delete an item given it has no trakt to tmdb mapping`() {
        openSnapshot(version = 47).use { driver ->
            driver.addTraktList(id = 11L, slug = "favorites", name = "Favorites")
            driver.addTraktListShow(listId = 11L, traktId = 999L)

            migrateToVersion(driver, oldVersion = 47, newVersion = 48)

            driver.listShowRowCount() shouldBe 0L
        }
    }

    @Test
    fun `should delete an item given its list no longer exists`() {
        openSnapshot(version = 47).use { driver ->
            driver.addTvshow(internalId = 2L, tmdbId = 700L)
            driver.addExternalId(internalId = 2L, traktId = 300L)
            driver.addTraktListShow(listId = 77L, traktId = 300L)

            migrateToVersion(driver, oldVersion = 47, newVersion = 48)

            driver.listShowRowCount() shouldBe 0L
        }
    }

    @Test
    fun `should keep a list id and its item list id together after migration`() {
        openSnapshot(version = 47).use { driver ->
            driver.addTvshow(internalId = 2L, tmdbId = 700L)
            driver.addExternalId(internalId = 2L, traktId = 300L)
            driver.addTraktList(id = 55L, slug = "custom", name = "Custom")
            driver.addTraktListShow(listId = 55L, traktId = 300L)

            migrateToVersion(driver, oldVersion = 47, newVersion = 48)

            val localId = driver.listLocalIds().single()
            driver.listShowListIds() shouldBe listOf(localId)
        }
    }
}

private fun SqlDriver.addTvshow(internalId: Long, tmdbId: Long) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO tvshow (id, tmdb_id, name, overview, ratings, vote_count)
            VALUES ($internalId, $tmdbId, 'show-$internalId', 'overview', 7.5, 100)
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.addExternalId(internalId: Long, traktId: Long) {
    execute(
        identifier = null,
        sql = """
            INSERT OR IGNORE INTO tvshow_external_id (show_id, provider, external_id)
            VALUES ($internalId, 'TRAKT', '$traktId')
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.addTraktList(id: Long, slug: String, name: String) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO trakt_lists (id, slug, name, description, item_count, created_at)
            VALUES ($id, '$slug', '$name', NULL, 0, '2024-01-01T00:00:00Z')
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.addTraktListShow(listId: Long, traktId: Long) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO trakt_list_shows (list_id, trakt_id, listed_at, pending_action)
            VALUES ($listId, $traktId, '2024-01-01T00:00:00Z', 'NOTHING')
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.longColumn(sql: String): List<Long> = executeQuery(
    identifier = null,
    sql = sql,
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        val values = mutableListOf<Long>()
        while (cursor.next().value) {
            cursor.getLong(0)?.let(values::add)
        }
        QueryResult.Value(values.toList())
    },
).value

private fun SqlDriver.stringColumn(sql: String): List<String> = executeQuery(
    identifier = null,
    sql = sql,
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        val values = mutableListOf<String>()
        while (cursor.next().value) {
            cursor.getString(0)?.let(values::add)
        }
        QueryResult.Value(values.toList())
    },
).value

private fun SqlDriver.listTraktIds(): List<Long> = longColumn("SELECT trakt_id FROM lists ORDER BY id")

private fun SqlDriver.listLocalIds(): List<Long> = longColumn("SELECT id FROM lists ORDER BY id")

private fun SqlDriver.listPendingActions(): List<String> = stringColumn("SELECT pending_action FROM lists ORDER BY id")

private fun SqlDriver.listShowTmdbIds(): List<Long> = longColumn("SELECT tmdb_id FROM list_shows ORDER BY tmdb_id")

private fun SqlDriver.listShowListIds(): List<Long> = longColumn("SELECT list_id FROM list_shows")

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
