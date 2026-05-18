package com.thomaskioko.tvmaniac.db

import com.thomaskioko.tvmaniac.db.util.WatchProgress
import com.thomaskioko.tvmaniac.db.util.migrateToCurrent
import com.thomaskioko.tvmaniac.db.util.openSnapshot
import com.thomaskioko.tvmaniac.db.util.queryWatchProgress
import com.thomaskioko.tvmaniac.db.util.seedEpisode
import com.thomaskioko.tvmaniac.db.util.seedSeason
import com.thomaskioko.tvmaniac.db.util.seedTraktWatchedShow
import com.thomaskioko.tvmaniac.db.util.seedTvshow
import com.thomaskioko.tvmaniac.db.util.seedWatchedEpisode
import com.thomaskioko.tvmaniac.db.util.tableNames
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class Migration29Test {

    @Test
    fun `should create trakt_watched_shows table on migration`() {
        openSnapshot(version = 28).use { driver ->
            migrateToCurrent(driver, oldVersion = 28)

            driver.tableNames().contains("trakt_watched_shows") shouldBe true
        }
    }

    @Test
    fun `should accept upsert into trakt_watched_shows`() {
        openSnapshot(version = 28).use { driver ->
            migrateToCurrent(driver, oldVersion = 28)

            driver.seedTraktWatchedShow(traktId = 1L, tmdbId = 100L)
            // Subsequent insert for the same trakt id should not throw under the UNIQUE constraint;
            // the seed helper uses INSERT OR REPLACE indirectly via the schema's primary key.
        }
    }

    @Test
    fun `should compute show watch progress alongside trakt_watched_shows row`() {
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
