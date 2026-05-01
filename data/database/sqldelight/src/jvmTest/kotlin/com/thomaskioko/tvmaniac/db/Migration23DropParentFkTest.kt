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

class Migration23DropParentFkTest {

    @Test
    fun `should drop parent FK from similar_shows when migrating past version 23`() {
        openSnapshot(version = 23).use { driver ->
            driver.enableForeignKeys()
            driver.seedTvshow(traktId = 1L, tmdbId = 1001L)

            shouldThrowFkViolation {
                driver.insertSimilarShow(traktId = 1L, tmdbId = 1001L, parentTraktId = 999L)
            }

            migrateToCurrent(driver, oldVersion = 23)

            shouldNotThrowAny {
                driver.insertSimilarShow(traktId = 1L, tmdbId = 1001L, parentTraktId = 999L)
            }

            shouldThrowFkViolation {
                driver.insertSimilarShow(traktId = 8888L, tmdbId = 8888L, parentTraktId = 999L)
            }
        }
    }

    @Test
    fun `should drop parent FK from recommended_shows when migrating past version 23`() {
        openSnapshot(version = 23).use { driver ->
            driver.enableForeignKeys()
            driver.seedTvshow(traktId = 2L, tmdbId = 2002L)

            shouldThrowFkViolation {
                driver.insertRecommendedShow(traktId = 2L, tmdbId = 2002L, parentTraktId = 999L)
            }

            migrateToCurrent(driver, oldVersion = 23)

            shouldNotThrowAny {
                driver.insertRecommendedShow(traktId = 2L, tmdbId = 2002L, parentTraktId = 999L)
            }

            shouldThrowFkViolation {
                driver.insertRecommendedShow(traktId = 7777L, tmdbId = 7777L, parentTraktId = 999L)
            }
        }
    }

    @Test
    fun `should preserve existing rows across migration 23`() {
        openSnapshot(version = 23).use { driver ->
            driver.seedTvshow(traktId = 10L, tmdbId = 1010L)
            driver.seedTvshow(traktId = 20L, tmdbId = 2020L)
            driver.insertSimilarShow(traktId = 20L, tmdbId = 2020L, parentTraktId = 10L, pageOrder = 5L)
            driver.insertRecommendedShow(traktId = 20L, tmdbId = 2020L, parentTraktId = 10L)

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

private fun SqlDriver.seedTvshow(traktId: Long, tmdbId: Long) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO tvshow (trakt_id, tmdb_id, name, overview, ratings, vote_count)
            VALUES ($traktId, $tmdbId, 'show-$traktId', 'overview', 0.0, 0)
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.insertSimilarShow(
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

private fun SqlDriver.insertRecommendedShow(
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

private fun SqlDriver.querySimilarShow(parentTraktId: Long): Triple<Long, Long, Long>? =
    executeQuery(
        identifier = null,
        sql = "SELECT trakt_id, tmdb_id, page_order FROM similar_shows WHERE similar_show_trakt_id = $parentTraktId",
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
        sql = "SELECT trakt_id, tmdb_id FROM recommended_shows WHERE recommended_show_trakt_id = $parentTraktId",
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
