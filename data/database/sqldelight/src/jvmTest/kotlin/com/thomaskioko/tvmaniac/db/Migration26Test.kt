package com.thomaskioko.tvmaniac.db

import com.thomaskioko.tvmaniac.db.util.migrateToCurrent
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.viewNames
import io.kotest.matchers.collections.shouldContain
import kotlin.test.Test

class Migration26Test {

    @Test
    fun `should rebuild the last-watched view when migrating past version 25`() {
        openSnapshot(version = 24).use { driver ->
            migrateToCurrent(driver, oldVersion = 24)

            driver.viewNames() shouldContain "shows_last_watched"
        }
    }
}
