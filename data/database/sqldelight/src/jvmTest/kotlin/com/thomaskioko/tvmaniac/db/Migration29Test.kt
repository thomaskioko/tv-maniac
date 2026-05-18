package com.thomaskioko.tvmaniac.db

import com.thomaskioko.tvmaniac.db.util.WatchProgress
import com.thomaskioko.tvmaniac.db.util.migrateToCurrent
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.queryNextEpisodesForWatchlist
import com.thomaskioko.tvmaniac.db.util.queryWatchProgress
import com.thomaskioko.tvmaniac.db.util.seedEpisode
import com.thomaskioko.tvmaniac.db.util.seedSeason
import com.thomaskioko.tvmaniac.db.util.seedTraktWatchedShow
import com.thomaskioko.tvmaniac.db.util.seedTvshow
import com.thomaskioko.tvmaniac.db.util.seedWatchedEpisode
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration29Test {

    @Test
    fun `should emit watched show joined with tvshow row`() {
        openSnapshot(version = 28).use { driver ->
            migrateToCurrent(driver, oldVersion = 28)

            driver.seedTvshow(traktId = 1L, tmdbId = 100L, name = "Severance")
            driver.seedSeason(id = 11L, showTraktId = 1L, seasonNumber = 1L)
            driver.seedEpisode(
                id = 111L,
                seasonId = 11L,
                showTraktId = 1L,
                episodeNumber = 1L,
                firstAired = OLD_EPOCH_MS,
            )
            driver.seedTraktWatchedShow(traktId = 1L, tmdbId = 100L)

            val rows = driver.queryNextEpisodesForWatchlist()

            rows.size shouldBe 1
            rows[0].showTraktId shouldBe 1L
            rows[0].showTmdbId shouldBe 100L
            rows[0].showName shouldBe "Severance"
            rows[0].episodeId shouldBe 111L
        }
    }

    @Test
    fun `should emit orphan watched show row with null show fields given no tvshow`() {
        openSnapshot(version = 28).use { driver ->
            migrateToCurrent(driver, oldVersion = 28)

            driver.seedTraktWatchedShow(traktId = 9999L, tmdbId = null)

            val rows = driver.queryNextEpisodesForWatchlist()

            rows.size shouldBe 1
            rows[0].showTraktId shouldBe 9999L
            rows[0].showTmdbId shouldBe null
            rows[0].showName shouldBe null
            rows[0].episodeId shouldBe null
        }
    }

    @Test
    fun `should expose both joined and orphan rows in one query`() {
        openSnapshot(version = 28).use { driver ->
            migrateToCurrent(driver, oldVersion = 28)

            driver.seedTvshow(traktId = 1L, tmdbId = 100L, name = "Joined")
            driver.seedTraktWatchedShow(traktId = 1L, tmdbId = 100L)
            driver.seedTraktWatchedShow(traktId = 2L, tmdbId = null)

            val rows = driver.queryNextEpisodesForWatchlist()

            rows.map { it.showTraktId } shouldContainExactlyInAnyOrder listOf(1L, 2L)
            val orphan = rows.first { it.showTraktId == 2L }
            orphan.showName shouldBe null
        }
    }

    @Test
    fun `should compute show watch progress regardless of trakt_watched_shows presence`() {
        openSnapshot(version = 28).use { driver ->
            migrateToCurrent(driver, oldVersion = 28)

            driver.seedTvshow(traktId = 1L, tmdbId = 100L)
            driver.seedSeason(id = 11L, showTraktId = 1L, seasonNumber = 1L, episodeCount = 2L)
            driver.seedEpisode(
                id = 111L,
                seasonId = 11L,
                showTraktId = 1L,
                episodeNumber = 1L,
                firstAired = OLD_EPOCH_MS,
            )
            driver.seedEpisode(
                id = 112L,
                seasonId = 11L,
                showTraktId = 1L,
                episodeNumber = 2L,
                firstAired = OLD_EPOCH_MS,
            )
            driver.seedTraktWatchedShow(traktId = 1L, tmdbId = 100L)
            driver.seedWatchedEpisode(
                showTraktId = 1L,
                episodeId = 111L,
                seasonNumber = 1L,
                episodeNumber = 1L,
                pendingAction = "NOTHING",
            )

            driver.queryWatchProgress(showTraktId = 1L) shouldBe WatchProgress(watched = 1L, total = 2L)
        }
    }
}

private const val OLD_EPOCH_MS: Long = 1_577_836_800_000L
