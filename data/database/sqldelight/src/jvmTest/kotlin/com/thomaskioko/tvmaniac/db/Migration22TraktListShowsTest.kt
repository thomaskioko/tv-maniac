package com.thomaskioko.tvmaniac.db

import com.thomaskioko.tvmaniac.db.util.migrateToCurrent
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.tableNames
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration22TraktListShowsTest {

    @Test
    fun `should add trakt_list_shows junction table when migrating past version 22`() {
        openSnapshot(version = 22).use { driver ->
            driver.tableNames() shouldNotContain "trakt_list_shows"

            migrateToCurrent(driver, oldVersion = 22)

            driver.tableNames() shouldContain "trakt_list_shows"

            val database = DatabaseFactory(driver).createDatabase()

            database.traktListShowsQueries.selectByShowTraktId(show_trakt_id = 1L)
                .executeAsList() shouldBe emptyList()
            database.traktListShowsQueries.countActiveByListId()
                .executeAsList() shouldBe emptyList()
        }
    }
}
