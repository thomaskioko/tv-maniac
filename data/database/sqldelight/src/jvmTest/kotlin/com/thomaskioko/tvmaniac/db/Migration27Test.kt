package com.thomaskioko.tvmaniac.db

import com.thomaskioko.tvmaniac.db.util.WatchProgress
import com.thomaskioko.tvmaniac.db.util.insertEpisode
import com.thomaskioko.tvmaniac.db.util.insertSeason
import com.thomaskioko.tvmaniac.db.util.insertTvshow
import com.thomaskioko.tvmaniac.db.util.migrateToCurrent
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.queryWatchProgress
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration27Test {

    @Test
    fun `should exclude null-aired and future-aired episodes from show_watch_progress total count`() {
        openSnapshot(version = 24).use { driver ->
            migrateToCurrent(driver, oldVersion = 24)

            driver.insertTvshow(traktId = 1104L, tmdbId = 2104L)
            driver.insertSeason(id = 11040L, showTraktId = 1104L, seasonNumber = 1L)
            driver.insertEpisode(
                id = 110401L,
                seasonId = 11040L,
                showTraktId = 1104L,
                episodeNumber = 1L,
                firstAired = OLD_EPOCH_MS,
            )
            driver.insertEpisode(
                id = 110402L,
                seasonId = 11040L,
                showTraktId = 1104L,
                episodeNumber = 2L,
                firstAired = null,
            )
            driver.insertEpisode(
                id = 110403L,
                seasonId = 11040L,
                showTraktId = 1104L,
                episodeNumber = 3L,
                firstAired = FAR_FUTURE_EPOCH_MS,
            )

            driver.queryWatchProgress(showTraktId = 1104L) shouldBe
                WatchProgress(watched = 0L, total = 1L)
        }
    }
}

private const val OLD_EPOCH_MS: Long = 1_577_836_800_000L
private const val FAR_FUTURE_EPOCH_MS: Long = 4_102_444_800_000L
