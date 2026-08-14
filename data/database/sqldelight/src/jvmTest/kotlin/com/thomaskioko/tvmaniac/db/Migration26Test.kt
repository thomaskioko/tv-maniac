package com.thomaskioko.tvmaniac.db

import com.thomaskioko.tvmaniac.db.util.WatchProgress
import com.thomaskioko.tvmaniac.db.util.insertEpisode
import com.thomaskioko.tvmaniac.db.util.insertSeason
import com.thomaskioko.tvmaniac.db.util.insertTvshow
import com.thomaskioko.tvmaniac.db.util.insertWatchedEpisode
import com.thomaskioko.tvmaniac.db.util.migrateToCurrent
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.queryWatchProgress
import com.thomaskioko.tvmaniac.db.util.viewNames
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration26Test {

    @Test
    fun `should rebuild watch-progress views when migrating past version 25`() {
        openSnapshot(version = 24).use { driver ->
            migrateToCurrent(driver, oldVersion = 24)

            val views = driver.viewNames()
            views shouldContain "shows_last_watched"
            views shouldContain "show_watch_progress"
        }
    }

    @Test
    fun `should exclude SYNCED_DELETE rows from show_watch_progress watched count`() {
        openSnapshot(version = 24).use { driver ->
            migrateToCurrent(driver, oldVersion = 24)

            driver.insertTvshow(traktId = 1001L, tmdbId = 2001L)
            driver.insertSeason(id = 10010L, showTraktId = 1001L, seasonNumber = 1L)
            driver.insertEpisode(
                id = 100101L,
                seasonId = 10010L,
                showTraktId = 1001L,
                episodeNumber = 1L,
                firstAired = OLD_EPOCH_MS,
            )
            driver.insertEpisode(
                id = 100102L,
                seasonId = 10010L,
                showTraktId = 1001L,
                episodeNumber = 2L,
                firstAired = OLD_EPOCH_MS,
            )
            driver.insertWatchedEpisode(
                showTraktId = 1001L,
                episodeId = 100101L,
                seasonNumber = 1L,
                episodeNumber = 1L,
                pendingAction = "NOTHING",
            )
            driver.insertWatchedEpisode(
                showTraktId = 1001L,
                episodeId = 100102L,
                seasonNumber = 1L,
                episodeNumber = 2L,
                pendingAction = "SYNCED_DELETE",
            )

            driver.queryWatchProgress(showTraktId = 1001L) shouldBe
                WatchProgress(watched = 1L, total = 2L)
        }
    }

    @Test
    fun `should exclude DELETE rows from show_watch_progress watched count`() {
        openSnapshot(version = 24).use { driver ->
            migrateToCurrent(driver, oldVersion = 24)

            driver.insertTvshow(traktId = 1002L, tmdbId = 2002L)
            driver.insertSeason(id = 10020L, showTraktId = 1002L, seasonNumber = 1L)
            driver.insertEpisode(
                id = 100201L,
                seasonId = 10020L,
                showTraktId = 1002L,
                episodeNumber = 1L,
                firstAired = OLD_EPOCH_MS,
            )
            driver.insertWatchedEpisode(
                showTraktId = 1002L,
                episodeId = 100201L,
                seasonNumber = 1L,
                episodeNumber = 1L,
                pendingAction = "DELETE",
            )

            driver.queryWatchProgress(showTraktId = 1002L) shouldBe
                WatchProgress(watched = 0L, total = 1L)
        }
    }
}

private const val OLD_EPOCH_MS: Long = 1_577_836_800_000L
