package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.tableNames
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration36Test {

    @Test
    fun `should rename activity_checkpoint to activity_sync with a provider column`() {
        openSnapshot(version = 36).use { driver ->
            migrateToVersion(driver, oldVersion = 36, newVersion = 37)

            val tables = driver.tableNames()
            tables shouldContain "activity_sync"
            tables shouldNotContain "activity_checkpoint"

            val cols = driver.columnNames("activity_sync")
            cols shouldContain "provider"
            cols shouldContain "consumer_id"
            cols shouldContain "activity_type"
            cols shouldContain "synced_until"
            cols shouldContain "updated_at"
        }
    }

    @Test
    fun `should backfill existing sync activity to the trakt provider`() {
        openSnapshot(version = 36).use { driver ->
            driver.execute(
                identifier = null,
                sql = """
                    INSERT INTO activity_checkpoint (consumer_id, activity_type, synced_until, updated_at)
                    VALUES ('progress_continue_watching', 'episodes_watched', 1700000000000, 1700500000000),
                           ('library_watchlist', 'shows_watchlisted', 1700100000000, 1700600000000)
                """.trimIndent(),
                parameters = 0,
            )

            migrateToVersion(driver, oldVersion = 36, newVersion = 37)

            val rows = driver.activitySyncRows()
            rows shouldBe listOf(
                PostMigration36Row(
                    provider = "TRAKT",
                    consumerId = "library_watchlist",
                    activityType = "shows_watchlisted",
                    syncedUntil = 1_700_100_000_000L,
                    updatedAt = 1_700_600_000_000L,
                ),
                PostMigration36Row(
                    provider = "TRAKT",
                    consumerId = "progress_continue_watching",
                    activityType = "episodes_watched",
                    syncedUntil = 1_700_000_000_000L,
                    updatedAt = 1_700_500_000_000L,
                ),
            )
        }
    }
}

private data class PostMigration36Row(
    val provider: String,
    val consumerId: String,
    val activityType: String,
    val syncedUntil: Long,
    val updatedAt: Long,
)

private fun SqlDriver.activitySyncRows(): List<PostMigration36Row> = executeQuery(
    identifier = null,
    sql = """
        SELECT provider, consumer_id, activity_type, synced_until, updated_at
        FROM activity_sync
        ORDER BY consumer_id
    """.trimIndent(),
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        val rows = mutableListOf<PostMigration36Row>()
        while (cursor.next().value) {
            rows.add(
                PostMigration36Row(
                    provider = cursor.getString(0)!!,
                    consumerId = cursor.getString(1)!!,
                    activityType = cursor.getString(2)!!,
                    syncedUntil = cursor.getLong(3)!!,
                    updatedAt = cursor.getLong(4)!!,
                ),
            )
        }
        QueryResult.Value(rows.toList())
    },
).value
