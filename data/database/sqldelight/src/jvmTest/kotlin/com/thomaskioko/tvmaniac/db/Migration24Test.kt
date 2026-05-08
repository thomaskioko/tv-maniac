package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.tvmaniac.db.util.migrateToCurrent
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.tableNames
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration24Test {

    @Test
    fun `should drop next_episodes table when migrating past version 23`() {
        openSnapshot(version = 23).use { driver ->
            driver.tableNames() shouldContain "next_episodes"

            migrateToCurrent(driver, oldVersion = 23)

            driver.tableNames() shouldNotContain "next_episodes"
        }
    }

    @Test
    fun `should preserve unrelated tables when dropping next_episodes`() {
        openSnapshot(version = 23).use { driver ->
            val before = driver.tableNames()

            migrateToCurrent(driver, oldVersion = 23)

            val after = driver.tableNames()
            (before - after) shouldBe setOf("next_episodes")
            after shouldContain "watched_episodes"
            after shouldContain "show_metadata"
            after shouldContain "followed_shows"
            after shouldContain "tvshow"
        }
    }

    @Test
    fun `should not error when next_episodes already absent`() {
        openSnapshot(version = 23).use { driver ->
            driver.dropNextEpisodes()
            driver.tableNames() shouldNotContain "next_episodes"

            migrateToCurrent(driver, oldVersion = 23)

            driver.tableNames() shouldNotContain "next_episodes"
        }
    }
}

private fun SqlDriver.dropNextEpisodes() {
    execute(
        identifier = null,
        sql = "DROP TABLE IF EXISTS next_episodes",
        parameters = 0,
    )
}
