package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration51Test {

    @Test
    fun `should add a page column to genre_shows given migrating from 51 to 52`() {
        openSnapshot(version = 51).use { driver ->
            migrateToVersion(driver, oldVersion = 51, newVersion = 52)

            val columns = driver.columnNames("genre_shows")
            columns shouldContain "page"
        }
    }

    @Test
    fun `should migrate an existing row to page 1 given migrating from 51 to 52`() {
        openSnapshot(version = 51).use { driver ->
            driver.addTvshow(internalId = 1L, tmdbId = 900L)
            driver.addGenre(slug = "drama", name = "Drama")
            driver.addGenreShow(genreSlug = "drama", showId = 1L, pageOrder = 0L, category = "POPULAR")

            migrateToVersion(driver, oldVersion = 51, newVersion = 52)

            driver.genreShowPages() shouldBe listOf(1L)
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

private fun SqlDriver.addGenre(slug: String, name: String) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO trakt_genres (slug, name)
            VALUES ('$slug', '$name')
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.addGenreShow(genreSlug: String, showId: Long, pageOrder: Long, category: String) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO genre_shows (genre_slug, show_id, page_order, category)
            VALUES ('$genreSlug', $showId, $pageOrder, '$category')
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.genreShowPages(): List<Long> = executeQuery(
    identifier = null,
    sql = "SELECT page FROM genre_shows ORDER BY genre_slug",
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
