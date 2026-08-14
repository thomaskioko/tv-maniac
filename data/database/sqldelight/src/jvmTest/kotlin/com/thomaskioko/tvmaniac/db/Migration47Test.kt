package com.thomaskioko.tvmaniac.db

import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.readSchema
import com.thomaskioko.tvmaniac.db.util.tableNames
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration47Test {

    @Test
    fun `should remove the shows_next_to_watch view given migration to version 47`() {
        openSnapshot(version = 46).use { driver ->
            driver.readSchema().map { it.name } shouldContain "shows_next_to_watch"

            migrateToVersion(driver, oldVersion = 46, newVersion = 47)

            driver.readSchema().map { it.name } shouldNotContain "shows_next_to_watch"
        }
    }

    @Test
    fun `should leave the watched_episodes schema unchanged given migration to version 47`() {
        openSnapshot(version = 46).use { driver ->
            val before = driver.readSchema().first { it.name == "watched_episodes" }

            migrateToVersion(driver, oldVersion = 46, newVersion = 47)

            driver.readSchema().first { it.name == "watched_episodes" } shouldBe before
        }
    }

    @Test
    fun `should keep the rewatch session tables given migration to version 47`() {
        openSnapshot(version = 46).use { driver ->
            migrateToVersion(driver, oldVersion = 46, newVersion = 47)

            driver.tableNames() shouldContain "rewatch_session"
            driver.tableNames() shouldContain "rewatch_session_episode"
        }
    }
}
