package com.thomaskioko.tvmaniac.db

import com.thomaskioko.tvmaniac.db.util.WatchProgress
import com.thomaskioko.tvmaniac.db.util.countNextToWatch
import com.thomaskioko.tvmaniac.db.util.insertEpisode
import com.thomaskioko.tvmaniac.db.util.insertFollowedShow
import com.thomaskioko.tvmaniac.db.util.insertSeason
import com.thomaskioko.tvmaniac.db.util.insertTvshow
import com.thomaskioko.tvmaniac.db.util.migrateToCurrent
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.queryWatchProgress
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration27Test {

    @Test
    fun `should exclude null-aired episodes from shows_next_to_watch`() {
        openSnapshot(version = 24).use { driver ->
            migrateToCurrent(driver, oldVersion = 24)

            driver.insertTvshow(traktId = 1101L, tmdbId = 2101L)
            driver.insertFollowedShow(traktId = 1101L, tmdbId = 2101L)
            driver.insertSeason(id = 11010L, showTraktId = 1101L, seasonNumber = 1L)
            driver.insertEpisode(
                id = 110101L,
                seasonId = 11010L,
                showTraktId = 1101L,
                episodeNumber = 1L,
                firstAired = null,
            )

            driver.countNextToWatch(showTraktId = 1101L) shouldBe 0L
        }
    }

    @Test
    fun `should exclude future-aired episodes from shows_next_to_watch`() {
        openSnapshot(version = 24).use { driver ->
            migrateToCurrent(driver, oldVersion = 24)

            driver.insertTvshow(traktId = 1102L, tmdbId = 2102L)
            driver.insertFollowedShow(traktId = 1102L, tmdbId = 2102L)
            driver.insertSeason(id = 11020L, showTraktId = 1102L, seasonNumber = 1L)
            driver.insertEpisode(
                id = 110201L,
                seasonId = 11020L,
                showTraktId = 1102L,
                episodeNumber = 1L,
                firstAired = FAR_FUTURE_EPOCH_MS,
            )

            driver.countNextToWatch(showTraktId = 1102L) shouldBe 0L
        }
    }

    @Test
    fun `should include aired episodes in shows_next_to_watch`() {
        openSnapshot(version = 24).use { driver ->
            migrateToCurrent(driver, oldVersion = 24)

            driver.insertTvshow(traktId = 1103L, tmdbId = 2103L)
            driver.insertFollowedShow(traktId = 1103L, tmdbId = 2103L)
            driver.insertSeason(id = 11030L, showTraktId = 1103L, seasonNumber = 1L)
            driver.insertEpisode(
                id = 110301L,
                seasonId = 11030L,
                showTraktId = 1103L,
                episodeNumber = 1L,
                firstAired = OLD_EPOCH_MS,
            )

            driver.countNextToWatch(showTraktId = 1103L) shouldBe 1L
        }
    }

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
