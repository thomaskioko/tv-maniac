package com.thomaskioko.tvmaniac.db

import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.tableNames
import io.kotest.matchers.collections.shouldContain
import kotlin.test.Test

class Migration50Test {

    @Test
    fun `should create search results and search history tables given migrating from 50 to 51`() {
        openSnapshot(version = 50).use { driver ->
            migrateToVersion(driver, oldVersion = 50, newVersion = 51)

            val tables = driver.tableNames()
            tables shouldContain "search_results"
            tables shouldContain "search_history"
        }
    }
}
