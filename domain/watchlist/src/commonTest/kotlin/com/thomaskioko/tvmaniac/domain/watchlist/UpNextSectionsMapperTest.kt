package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.domain.watchlist.model.EpisodeBadge
import com.thomaskioko.tvmaniac.domain.watchlist.model.UpNextSections
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.Test

private fun LocalDate.toEpochMillis(): Long =
    atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

class UpNextSectionsMapperTest {
    private val dateTimeProvider = FakeDateTimeProvider()
    private val mapper = UpNextSectionsMapper(dateTimeProvider)

    @Test
    fun `should return empty sections given empty list`() {
        val result = mapper.map(emptyList())
        result shouldBe UpNextSections(watchNext = emptyList(), stale = emptyList())
    }

    @Test
    fun `should return NEW badge given episode 1 season 1 of show from current year`() {
        val currentTime = LocalDate(2024, 11, 14).toEpochMillis()
        val pastAired = LocalDate(2024, 11, 4).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)
        dateTimeProvider.setCurrentYear(2024)

        val episodes = listOf(
            createNextEpisode(
                showTraktId = 1,
                showName = "New Series",
                seasonNumber = 1,
                episodeNumber = 1,
                firstAired = pastAired,
                showYear = "2024",
            ),
        )

        val result = mapper.map(episodes)

        result.watchNext.size shouldBe 1
        result.watchNext[0].badge shouldBe EpisodeBadge.NEW
    }

    @Test
    fun `should return NONE badge given non-first episode`() {
        val currentTime = LocalDate(2024, 11, 14).toEpochMillis()
        val pastAired = LocalDate(2024, 11, 4).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)
        dateTimeProvider.setCurrentYear(2024)

        val episodes = listOf(
            createNextEpisode(
                showTraktId = 1,
                showName = "Ongoing Series",
                seasonNumber = 2,
                episodeNumber = 5,
                firstAired = pastAired,
                showYear = "2024",
            ),
        )

        val result = mapper.map(episodes)

        result.watchNext.size shouldBe 1
        result.watchNext[0].badge shouldBe EpisodeBadge.NONE
    }

    @Test
    fun `should return PREMIERE badge given episode 1 of season 5 aired recently`() {
        val currentTime = LocalDate(2024, 11, 14).toEpochMillis()
        val pastAired = LocalDate(2024, 11, 4).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)
        dateTimeProvider.setCurrentYear(2024)

        val episodes = listOf(
            createNextEpisode(
                showTraktId = 1,
                showName = "Returning Show",
                seasonNumber = 5,
                episodeNumber = 1,
                firstAired = pastAired,
                showYear = "2008",
            ),
        )

        val result = mapper.map(episodes)

        result.watchNext.size shouldBe 1
        result.watchNext[0].badge shouldBe EpisodeBadge.PREMIERE
    }

    @Test
    fun `should return NONE badge given episode 1 season 1 of show from past year`() {
        val currentTime = LocalDate(2024, 11, 14).toEpochMillis()
        val pastAired = LocalDate(2024, 10, 25).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)
        dateTimeProvider.setCurrentYear(2024)

        val episodes = listOf(
            createNextEpisode(
                showTraktId = 1,
                showName = "Old Show Season 1",
                seasonNumber = 1,
                episodeNumber = 1,
                firstAired = pastAired,
                showYear = "2008",
            ),
        )

        val result = mapper.map(episodes)

        result.watchNext.size shouldBe 1
        result.watchNext[0].badge shouldBe EpisodeBadge.NONE
    }

    @Test
    fun `should return NONE badge given episode 1 of returning show aired long ago`() {
        val currentTime = LocalDate(2024, 11, 14).toEpochMillis()
        val oldAired = LocalDate(2024, 10, 1).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)
        dateTimeProvider.setCurrentYear(2024)

        val episodes = listOf(
            createNextEpisode(
                showTraktId = 1,
                showName = "Returning Show Old Premiere",
                seasonNumber = 5,
                episodeNumber = 1,
                firstAired = oldAired,
                showYear = "2008",
            ),
        )

        val result = mapper.map(episodes)

        result.watchNext.size shouldBe 1
        result.watchNext[0].badge shouldBe EpisodeBadge.NONE
    }

    @Test
    fun `should return NONE badge given episode 1 season 2 with null firstAired`() {
        val currentTime = LocalDate(2024, 11, 14).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)
        dateTimeProvider.setCurrentYear(2024)

        val episodes = listOf(
            createNextEpisode(
                showTraktId = 1,
                showName = "Unknown Air Date Show",
                seasonNumber = 2,
                episodeNumber = 1,
                firstAired = null,
                showYear = "2020",
            ),
        )

        val result = mapper.map(episodes)

        result.watchNext.size shouldBe 0
    }

    @Test
    fun `should filter out episodes that have not aired yet`() {
        val currentTime = LocalDate(2023, 11, 14).toEpochMillis()
        val futureDate = LocalDate(2023, 11, 20).toEpochMillis()
        val pastDate = LocalDate(2023, 11, 10).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)

        val episodes = listOf(
            createNextEpisode(showTraktId = 1, showName = "Aired", firstAired = pastDate),
            createNextEpisode(showTraktId = 2, showName = "Future", firstAired = futureDate),
        )

        val result = mapper.map(episodes)

        result.watchNext.size shouldBe 1
        result.watchNext[0].showName shouldBe "Aired"
    }

    @Test
    fun `should filter out episodes with null air date`() {
        val currentTime = LocalDate(2023, 11, 14).toEpochMillis()
        val pastDate = LocalDate(2023, 11, 10).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)

        val episodes = listOf(
            createNextEpisode(showTraktId = 1, showName = "Known Date", firstAired = pastDate),
            createNextEpisode(showTraktId = 2, showName = "Unknown Date", firstAired = null),
        )

        val result = mapper.map(episodes)

        result.watchNext.size shouldBe 1
        result.watchNext[0].showName shouldBe "Known Date"
    }

    @Test
    fun `should place episode in stale section given lastWatched over 16 days ago`() {
        val currentTime = LocalDate(2023, 11, 14).toEpochMillis()
        val twentyDaysAgo = LocalDate(2023, 10, 25).toEpochMillis()
        val pastAired = LocalDate(2023, 10, 15).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)

        val episodes = listOf(
            createNextEpisode(
                showTraktId = 1,
                showName = "Stale Show",
                lastWatchedAt = twentyDaysAgo,
                firstAired = pastAired,
            ),
        )

        val result = mapper.map(episodes)

        result.watchNext.size shouldBe 0
        result.stale.size shouldBe 1
        result.stale[0].showName shouldBe "Stale Show"
    }

    @Test
    fun `should place episode in watchNext section given lastWatched within 16 days`() {
        val currentTime = LocalDate(2023, 11, 14).toEpochMillis()
        val fiveDaysAgo = LocalDate(2023, 11, 9).toEpochMillis()
        val pastAired = LocalDate(2023, 10, 15).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)

        val episodes = listOf(
            createNextEpisode(
                showTraktId = 1,
                showName = "Active Show",
                lastWatchedAt = fiveDaysAgo,
                firstAired = pastAired,
            ),
        )

        val result = mapper.map(episodes)

        result.watchNext.size shouldBe 1
        result.watchNext[0].showName shouldBe "Active Show"
        result.stale.size shouldBe 0
    }

    @Test
    fun `should place episode in watchNext section given null lastWatched and recent followedAt`() {
        val currentTime = LocalDate(2023, 11, 14).toEpochMillis()
        val pastAired = LocalDate(2023, 10, 15).toEpochMillis()
        val recentFollowedAt = LocalDate(2023, 11, 10).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)

        val episodes = listOf(
            createNextEpisode(
                showTraktId = 1,
                showName = "Never Watched",
                lastWatchedAt = null,
                followedAt = recentFollowedAt,
                firstAired = pastAired,
            ),
        )

        val result = mapper.map(episodes)

        result.watchNext.size shouldBe 1
        result.watchNext[0].showName shouldBe "Never Watched"
        result.stale.size shouldBe 0
    }

    @Test
    fun `should place episode in stale section given null lastWatched and old followedAt`() {
        val currentTime = LocalDate(2023, 11, 14).toEpochMillis()
        val pastAired = LocalDate(2023, 10, 1).toEpochMillis()
        val oldFollowedAt = LocalDate(2023, 10, 20).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)

        val episodes = listOf(
            createNextEpisode(
                showTraktId = 1,
                showName = "Never Watched Old Follow",
                lastWatchedAt = null,
                followedAt = oldFollowedAt,
                firstAired = pastAired,
            ),
        )

        val result = mapper.map(episodes)

        result.watchNext.size shouldBe 0
        result.stale.size shouldBe 1
        result.stale[0].showName shouldBe "Never Watched Old Follow"
    }

    @Test
    fun `should use lastWatchedAt over followedAt when both present`() {
        val currentTime = LocalDate(2023, 11, 14).toEpochMillis()
        val pastAired = LocalDate(2023, 10, 1).toEpochMillis()
        val recentWatched = LocalDate(2023, 11, 10).toEpochMillis()
        val oldFollowedAt = LocalDate(2023, 10, 1).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)

        val episodes = listOf(
            createNextEpisode(
                showTraktId = 1,
                showName = "Recently Watched",
                lastWatchedAt = recentWatched,
                followedAt = oldFollowedAt,
                firstAired = pastAired,
            ),
        )

        val result = mapper.map(episodes)

        result.watchNext.size shouldBe 1
        result.watchNext[0].showName shouldBe "Recently Watched"
        result.stale.size shouldBe 0
    }

    @Test
    fun `should calculate remaining episodes correctly`() {
        val currentTime = LocalDate(2023, 11, 14).toEpochMillis()
        val pastAired = LocalDate(2023, 10, 15).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)

        val episodes = listOf(
            createNextEpisode(
                showTraktId = 1,
                showName = "Show",
                watchedCount = 3,
                totalCount = 10,
                firstAired = pastAired,
            ),
        )

        val result = mapper.map(episodes)

        result.watchNext[0].remainingEpisodes shouldBe 7
    }

    @Test
    fun `should format episode number with proper padding`() {
        val currentTime = LocalDate(2023, 11, 14).toEpochMillis()
        val pastAired = LocalDate(2023, 10, 15).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)

        val episodes = listOf(
            createNextEpisode(
                showTraktId = 1,
                showName = "Show",
                seasonNumber = 2,
                episodeNumber = 5,
                firstAired = pastAired,
            ),
        )

        val result = mapper.map(episodes)

        result.watchNext[0].episodeNumberFormatted shouldBe "S02 | E05"
    }

    @Test
    fun `should format runtime when present`() {
        val currentTime = LocalDate(2023, 11, 14).toEpochMillis()
        val pastAired = LocalDate(2023, 10, 15).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)

        val episodes = listOf(
            createNextEpisode(
                showTraktId = 1,
                showName = "Show",
                runtime = 45,
                firstAired = pastAired,
            ),
        )

        val result = mapper.map(episodes)

        result.watchNext[0].formattedRuntime shouldBe "45 min"
    }

    @Test
    fun `should return null formatted runtime given null runtime`() {
        val currentTime = LocalDate(2023, 11, 14).toEpochMillis()
        val pastAired = LocalDate(2023, 10, 15).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(currentTime)

        val episodes = listOf(
            createNextEpisode(
                showTraktId = 1,
                showName = "Show",
                runtime = null,
                firstAired = pastAired,
            ),
        )

        val result = mapper.map(episodes)

        result.watchNext[0].formattedRuntime shouldBe null
    }

    private fun createNextEpisode(
        showTraktId: Long,
        showName: String,
        lastWatchedAt: Long? = null,
        followedAt: Long? = null,
        firstAired: Long? = LocalDate(2021, 6, 9).toEpochMillis(),
        watchedCount: Long = 0,
        totalCount: Long = 10,
        seasonNumber: Long = 1L,
        episodeNumber: Long = 2L,
        runtime: Long? = 45L,
        showYear: String? = "2024",
    ) = NextEpisodeWithShow(
        showTraktId = showTraktId,
        showTmdbId = showTraktId,
        showName = showName,
        showPoster = "/poster.jpg",
        showStatus = "Ended",
        showYear = showYear,
        episodeId = showTraktId * 100 + 1,
        episodeName = "Episode Title",
        seasonId = 1L,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        runtime = runtime,
        stillPath = "/still.jpg",
        overview = "Overview",
        followedAt = followedAt,
        firstAired = firstAired,
        lastWatchedAt = lastWatchedAt,
        seasonCount = 2,
        episodeCount = 12,
        watchedCount = watchedCount,
        totalCount = totalCount,
    )
}
