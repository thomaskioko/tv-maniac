package com.thomaskioko.tvmaniac.db

import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.tableNames
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import kotlin.test.Test

class Migration22Test {

    @Test
    fun `should add trakt_list_shows junction table when migrating past version 22`() {
        openSnapshot(version = 22).use { driver ->
            driver.tableNames() shouldNotContain "trakt_list_shows"

            migrateToVersion(driver, oldVersion = 22, newVersion = 23)

            driver.tableNames() shouldContain "trakt_list_shows"
        }
    }
}
