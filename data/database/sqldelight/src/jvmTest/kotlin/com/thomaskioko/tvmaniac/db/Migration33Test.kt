package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.migrateToCurrent
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration33Test {

    @Test
    fun `should drop synced_remote_timestamp column from trakt_last_activity`() {
        openSnapshot(version = 31).use { driver ->
            migrateToCurrent(driver, oldVersion = 31)

            val cols = driver.columnNames("trakt_last_activity")
            cols shouldContain "id"
            cols shouldContain "activity_type"
            cols shouldContain "remote_timestamp"
            cols shouldContain "fetched_at"
            cols shouldNotContain "synced_remote_timestamp"
        }
    }

    @Test
    fun `should preserve trakt_last_activity rows across the column drop`() {
        openSnapshot(version = 31).use { driver ->
            driver.execute(
                identifier = null,
                sql = """
                    INSERT INTO trakt_last_activity (activity_type, remote_timestamp, synced_remote_timestamp, fetched_at)
                    VALUES ('episodes_watched', 1700000000000, 1699000000000, 1700500000000),
                           ('shows_watchlisted', 1700100000000, NULL, 1700600000000)
                """.trimIndent(),
                parameters = 0,
            )

            migrateToCurrent(driver, oldVersion = 31)

            val rows = driver.traktLastActivityRows()
            rows shouldBe listOf(
                PostMigration33Row(
                    activityType = "episodes_watched",
                    remoteTimestamp = 1_700_000_000_000L,
                    fetchedAt = 1_700_500_000_000L,
                ),
                PostMigration33Row(
                    activityType = "shows_watchlisted",
                    remoteTimestamp = 1_700_100_000_000L,
                    fetchedAt = 1_700_600_000_000L,
                ),
            )
        }
    }

    @Test
    fun `should preserve activity_checkpoint rows backfilled by migration 32`() {
        openSnapshot(version = 31).use { driver ->
            driver.execute(
                identifier = null,
                sql = """
                    INSERT INTO trakt_last_activity (activity_type, remote_timestamp, synced_remote_timestamp, fetched_at)
                    VALUES ('episodes_watched', 1700000000000, 1699000000000, 1700500000000)
                """.trimIndent(),
                parameters = 0,
            )

            migrateToCurrent(driver, oldVersion = 31)

            val checkpoints = driver.activityPostMigration33CheckpointRows()
            checkpoints shouldBe listOf(
                PostMigration33CheckpointRow(
                    consumerId = "progress_continue_watching",
                    activityType = "episodes_watched",
                    syncedUntil = 1_699_000_000_000L,
                    updatedAt = 1_700_500_000_000L,
                ),
            )
        }
    }
}

private data class PostMigration33Row(
    val activityType: String,
    val remoteTimestamp: Long,
    val fetchedAt: Long,
)

private data class PostMigration33CheckpointRow(
    val consumerId: String,
    val activityType: String,
    val syncedUntil: Long,
    val updatedAt: Long,
)

private fun SqlDriver.traktLastActivityRows(): List<PostMigration33Row> = executeQuery(
    identifier = null,
    sql = """
        SELECT activity_type, remote_timestamp, fetched_at
        FROM trakt_last_activity
        ORDER BY activity_type
    """.trimIndent(),
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        val rows = mutableListOf<PostMigration33Row>()
        while (cursor.next().value) {
            rows.add(
                PostMigration33Row(
                    activityType = cursor.getString(0)!!,
                    remoteTimestamp = cursor.getLong(1)!!,
                    fetchedAt = cursor.getLong(2)!!,
                ),
            )
        }
        QueryResult.Value(rows.toList())
    },
).value

private fun SqlDriver.activityPostMigration33CheckpointRows(): List<PostMigration33CheckpointRow> = executeQuery(
    identifier = null,
    sql = """
        SELECT consumer_id, activity_type, synced_until, updated_at
        FROM activity_checkpoint
        ORDER BY consumer_id, activity_type
    """.trimIndent(),
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        val rows = mutableListOf<PostMigration33CheckpointRow>()
        while (cursor.next().value) {
            rows.add(
                PostMigration33CheckpointRow(
                    consumerId = cursor.getString(0)!!,
                    activityType = cursor.getString(1)!!,
                    syncedUntil = cursor.getLong(2)!!,
                    updatedAt = cursor.getLong(3)!!,
                ),
            )
        }
        QueryResult.Value(rows.toList())
    },
).value
