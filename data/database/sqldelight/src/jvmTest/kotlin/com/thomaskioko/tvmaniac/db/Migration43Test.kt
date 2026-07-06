package com.thomaskioko.tvmaniac.db

import com.thomaskioko.tvmaniac.db.util.columnNames
import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.tableNames
import io.kotest.matchers.collections.shouldContain
import kotlin.test.Test

class Migration43Test {

    @Test
    fun `should create watched_show_sync_log table given migration to version 43`() {
        openSnapshot(version = 42).use { driver ->
            migrateToVersion(driver, oldVersion = 42, newVersion = 43)

            driver.tableNames() shouldContain "watched_show_sync_log"
            val cols = driver.columnNames("watched_show_sync_log")
            cols shouldContain "show_id"
            cols shouldContain "provider"
            cols shouldContain "remote_updated_at"
        }
    }
}
