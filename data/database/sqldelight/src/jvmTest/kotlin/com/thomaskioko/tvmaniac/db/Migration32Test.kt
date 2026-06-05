package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.migrateToCurrent
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.tableNames
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration32Test {

    @Test
    fun `should create activity_checkpoint table`() {
        openSnapshot(version = 31).use { driver ->
            migrateToCurrent(driver, oldVersion = 31)

            driver.tableNames() shouldContain "activity_checkpoint"

            val cols = driver.columnNames("activity_checkpoint")
            cols shouldContain "consumer_id"
            cols shouldContain "activity_type"
            cols shouldContain "synced_until"
            cols shouldContain "updated_at"
        }
    }

    @Test
    fun `should backfill progress_continue_watching checkpoint from synced episodes_watched activity`() {
        openSnapshot(version = 31).use { driver ->
            driver.insertActivity(
                activityType = "episodes_watched",
                remoteTimestamp = 1_700_000_000_000L,
                syncedRemoteTimestamp = 1_699_000_000_000L,
                fetchedAt = 1_700_500_000_000L,
            )

            migrateToCurrent(driver, oldVersion = 31)

            driver.activityCheckpointRows() shouldBe listOf(
                CheckpointRow(
                    consumerId = "progress_continue_watching",
                    activityType = "episodes_watched",
                    syncedUntil = 1_699_000_000_000L,
                    updatedAt = 1_700_500_000_000L,
                ),
            )
        }
    }

    @Test
    fun `should backfill library_watchlist checkpoint from synced shows_watchlisted activity`() {
        openSnapshot(version = 31).use { driver ->
            driver.insertActivity(
                activityType = "shows_watchlisted",
                remoteTimestamp = 1_700_000_000_000L,
                syncedRemoteTimestamp = 1_699_500_000_000L,
                fetchedAt = 1_700_700_000_000L,
            )

            migrateToCurrent(driver, oldVersion = 31)

            driver.activityCheckpointRows() shouldBe listOf(
                CheckpointRow(
                    consumerId = "library_watchlist",
                    activityType = "shows_watchlisted",
                    syncedUntil = 1_699_500_000_000L,
                    updatedAt = 1_700_700_000_000L,
                ),
            )
        }
    }

    @Test
    fun `should skip backfill for activities with null synced_remote_timestamp`() {
        openSnapshot(version = 31).use { driver ->
            driver.insertActivity(
                activityType = "episodes_watched",
                remoteTimestamp = 1_700_000_000_000L,
                syncedRemoteTimestamp = null,
                fetchedAt = 1_700_500_000_000L,
            )

            migrateToCurrent(driver, oldVersion = 31)

            driver.activityCheckpointRows() shouldBe emptyList()
        }
    }

    @Test
    fun `should skip backfill for activity types outside the known consumer set`() {
        openSnapshot(version = 31).use { driver ->
            driver.insertActivity(
                activityType = "shows_favorited",
                remoteTimestamp = 1_700_000_000_000L,
                syncedRemoteTimestamp = 1_699_000_000_000L,
                fetchedAt = 1_700_500_000_000L,
            )

            migrateToCurrent(driver, oldVersion = 31)

            driver.activityCheckpointRows() shouldBe emptyList()
        }
    }
}

private data class CheckpointRow(
    val consumerId: String,
    val activityType: String,
    val syncedUntil: Long,
    val updatedAt: Long,
)

private fun SqlDriver.insertActivity(
    activityType: String,
    remoteTimestamp: Long,
    syncedRemoteTimestamp: Long?,
    fetchedAt: Long,
) {
    val syncedSql = syncedRemoteTimestamp?.toString() ?: "NULL"
    execute(
        identifier = null,
        sql = """
            INSERT INTO trakt_last_activity (activity_type, remote_timestamp, synced_remote_timestamp, fetched_at)
            VALUES ('$activityType', $remoteTimestamp, $syncedSql, $fetchedAt)
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.activityCheckpointRows(): List<CheckpointRow> = executeQuery(
    identifier = null,
    sql = """
        SELECT consumer_id, activity_type, synced_until, updated_at
        FROM activity_checkpoint
        ORDER BY consumer_id, activity_type
    """.trimIndent(),
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        val rows = mutableListOf<CheckpointRow>()
        while (cursor.next().value) {
            rows.add(
                CheckpointRow(
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
