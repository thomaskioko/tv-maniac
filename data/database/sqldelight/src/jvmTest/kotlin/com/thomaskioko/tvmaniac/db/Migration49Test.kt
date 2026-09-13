package com.thomaskioko.tvmaniac.db

import com.thomaskioko.tvmaniac.db.util.migrateToVersion
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.viewNames
import io.kotest.matchers.collections.shouldNotContain
import kotlin.test.Test

class Migration49Test {

    @Test
    fun `should drop the watch-progress view given the aggregate moved into its queries`() {
        openSnapshot(version = 49).use { driver ->
            migrateToVersion(driver, oldVersion = 49, newVersion = 50)

            driver.viewNames() shouldNotContain "show_watch_progress"
        }
    }
}
