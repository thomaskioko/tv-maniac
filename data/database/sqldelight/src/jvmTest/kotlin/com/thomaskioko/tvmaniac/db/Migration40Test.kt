package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration40Test {

    @Test
    fun `should clear last_requests on migration`() {
        openSnapshot(version = 39).use { driver ->
            driver.seedLastRequest(entityId = 101L, requestType = "SHOW_DETAILS")
            driver.seedLastRequest(entityId = 202L, requestType = "SHOW_CAST")

            migrateToVersion(driver, oldVersion = 39, newVersion = 40)

            driver.lastRequestsRowCount() shouldBe 0L
        }
    }

    @Test
    fun `should leave last_requests empty when it was already empty`() {
        openSnapshot(version = 39).use { driver ->
            migrateToVersion(driver, oldVersion = 39, newVersion = 40)

            driver.lastRequestsRowCount() shouldBe 0L
        }
    }
}

private fun SqlDriver.seedLastRequest(entityId: Long, requestType: String) {
    execute(
        identifier = null,
        sql = """
            INSERT INTO last_requests (entity_id, request_type, timestamp)
            VALUES ($entityId, '$requestType', 1700000000000)
        """.trimIndent(),
        parameters = 0,
    )
}

private fun SqlDriver.lastRequestsRowCount(): Long = executeQuery(
    identifier = null,
    sql = "SELECT COUNT(*) FROM last_requests",
    parameters = 0,
    binders = null,
    mapper = { cursor ->
        cursor.next()
        QueryResult.Value(cursor.getLong(0) ?: 0L)
    },
).value
