package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.migrateToCurrent
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

class Migration23Test {

    @Test
    fun `should drop parent FK from similar_shows when migrating past version 23`() {
        openSnapshot(version = 23).use { driver ->
            driver.enableForeignKeys()
            driver.insertTvshow(traktId = 1L, tmdbId = 1001L)

            shouldThrowFkViolation {
                driver.insertSimilarShowV23(traktId = 1L, tmdbId = 1001L, parentTraktId = 999L)
            }

            driver.execute(null, "PRAGMA foreign_keys=OFF", 0)
            migrateToCurrent(driver, oldVersion = 23)
            driver.execute(null, "PRAGMA foreign_keys=ON", 0)

            val showId = driver.showId(traktId = 1L)!!
            shouldNotThrowAny {
                driver.insertSimilarShow(showId = showId, tmdbId = 1001L, parentTraktId = 999L)
            }

            shouldThrowFkViolation {
                driver.insertSimilarShow(showId = 8888L, tmdbId = 8888L, parentTraktId = 999L)
            }
        }
    }

    @Test
    fun `should drop parent FK from recommended_shows when migrating past version 23`() {
        openSnapshot(version = 23).use { driver ->
            driver.enableForeignKeys()
            driver.insertTvshow(traktId = 2L, tmdbId = 2002L)

            shouldThrowFkViolation {
                driver.insertRecommendedShowV23(traktId = 2L, tmdbId = 2002L, parentTraktId = 999L)
            }

            driver.execute(null, "PRAGMA foreign_keys=OFF", 0)
            migrateToCurrent(driver, oldVersion = 23)
            driver.execute(null, "PRAGMA foreign_keys=ON", 0)

            val showId = driver.showId(traktId = 2L)!!
            shouldNotThrowAny {
                driver.insertRecommendedShow(showId = showId, tmdbId = 2002L, parentTraktId = 999L)
            }

            shouldThrowFkViolation {
                driver.insertRecommendedShow(showId = 7777L, tmdbId = 7777L, parentTraktId = 999L)
            }
        }
    }

    @Test
    fun `should preserve existing rows across migration 23`() {
        openSnapshot(version = 23).use { driver ->
            driver.insertTvshow(traktId = 10L, tmdbId = 1010L)
            driver.insertTvshow(traktId = 20L, tmdbId = 2020L)
            driver.insertSimilarShowV23(traktId = 20L, tmdbId = 2020L, parentTraktId = 10L, pageOrder = 5L)
            driver.insertRecommendedShowV23(traktId = 20L, tmdbId = 2020L, parentTraktId = 10L)

            migrateToCurrent(driver, oldVersion = 23)

            driver.querySimilarShow(parentTraktId = 10L) shouldBe Triple(20L, 2020L, 5L)
            driver.queryRecommendedShow(parentTraktId = 10L) shouldBe (20L to 2020L)
        }
    }
}

private fun SqlDriver.enableForeignKeys() {
    execute(null, "PRAGMA foreign_keys=ON", 0)
}

private inline fun shouldThrowFkViolation(block: () -> Unit) {
    val error = shouldThrow<Exception>(block)
    val message = generateSequence<Throwable>(error) { it.cause }.joinToString { it.message.orEmpty() }
    message shouldContain "FOREIGN KEY"
}

private fun SqlDriver.insertTvshow(traktId: Long, tmdbId: Long) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO tvshow (trakt_id, tmdb_id, name, overview, ratings, vote_count)
            VALUES ($traktId, $tmdbId, 'show-$traktId', 'overview', 0.0, 0)
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.showId(traktId: Long): Long? = executeQuery(
    identifier = null,
    sql = "SELECT show_id FROM show_trakt WHERE trakt_id = $traktId",
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        QueryResult.Value(if (cursor.next().value) cursor.getLong(0) else null)
    },
).value

private fun SqlDriver.insertSimilarShowV23(
    traktId: Long,
    tmdbId: Long,
    parentTraktId: Long,
    pageOrder: Long = 0L,
) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO similar_shows (trakt_id, tmdb_id, similar_show_trakt_id, page_order)
            VALUES ($traktId, $tmdbId, $parentTraktId, $pageOrder)
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.insertSimilarShow(
    showId: Long,
    tmdbId: Long,
    parentTraktId: Long,
    pageOrder: Long = 0L,
) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO similar_shows (show_id, tmdb_id, similar_show_trakt_id, page_order)
            VALUES ($showId, $tmdbId, $parentTraktId, $pageOrder)
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.insertRecommendedShowV23(
    traktId: Long,
    tmdbId: Long,
    parentTraktId: Long,
) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO recommended_shows (trakt_id, tmdb_id, recommended_show_trakt_id)
            VALUES ($traktId, $tmdbId, $parentTraktId)
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.insertRecommendedShow(
    showId: Long,
    tmdbId: Long,
    parentTraktId: Long,
) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO recommended_shows (show_id, tmdb_id, recommended_show_trakt_id)
            VALUES ($showId, $tmdbId, $parentTraktId)
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.querySimilarShow(parentTraktId: Long): Triple<Long, Long, Long>? =
    executeQuery(
        identifier = null,
        sql = """
            SELECT show_trakt.trakt_id, similar_shows.tmdb_id, similar_shows.page_order
            FROM similar_shows
            JOIN tvshow ON tvshow.id = similar_shows.show_id
            JOIN show_trakt ON show_trakt.show_id = tvshow.id
            WHERE similar_shows.similar_show_trakt_id = $parentTraktId
        """.trimIndent(),
        parameters = 0,
        binders = null,
        mapper = { cursor ->
            QueryResult.Value(
                if (cursor.next().value) {
                    Triple(cursor.getLong(0)!!, cursor.getLong(1)!!, cursor.getLong(2)!!)
                } else {
                    null
                },
            )
        },
    ).value

private fun SqlDriver.queryRecommendedShow(parentTraktId: Long): Pair<Long, Long>? =
    executeQuery(
        identifier = null,
        sql = """
            SELECT show_trakt.trakt_id, recommended_shows.tmdb_id
            FROM recommended_shows
            JOIN tvshow ON tvshow.id = recommended_shows.show_id
            JOIN show_trakt ON show_trakt.show_id = tvshow.id
            WHERE recommended_shows.recommended_show_trakt_id = $parentTraktId
        """.trimIndent(),
        parameters = 0,
        binders = null,
        mapper = { cursor ->
            QueryResult.Value(
                if (cursor.next().value) {
                    cursor.getLong(0)!! to cursor.getLong(1)!!
                } else {
                    null
                },
            )
        },
    ).value
