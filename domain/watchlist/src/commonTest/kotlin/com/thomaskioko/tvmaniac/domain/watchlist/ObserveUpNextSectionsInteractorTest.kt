package com.thomaskioko.tvmaniac.domain.watchlist

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.domain.watchlist.model.UpNextSections
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

private fun LocalDate.toEpochMillis(): Long =
    atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ObserveUpNextSectionsInteractorTest {
    private val testDispatcher = StandardTestDispatcher()
    private val upNextRepository = FakeUpNextRepository()
    private val dateTimeProvider = FakeDateTimeProvider()

    private lateinit var interactor: ObserveUpNextSectionsInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        interactor = ObserveUpNextSectionsInteractor(
            upNextRepository = upNextRepository,
            mapper = UpNextSectionsMapper(dateTimeProvider),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should return empty sections when no episodes`() = runTest {
        upNextRepository.setNextEpisodesForWatchlist(emptyList())

        interactor("")

        interactor.flow.test {
            awaitItem() shouldBe UpNextSections(
                watchNext = emptyList(),
                stale = emptyList(),
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return episodes in watchNext when no lastWatched data`() = runTest {
        val episodes = listOf(
            createNextEpisode(showTraktId = 1, showName = "Loki"),
            createNextEpisode(showTraktId = 2, showName = "Wednesday"),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 2
            result.stale.size shouldBe 0
            result.watchNext[0].showName shouldBe "Loki"
            result.watchNext[1].showName shouldBe "Wednesday"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should group stale episodes when lastWatched is over 16 days ago`() = runTest {
        val currentTime = LocalDate(2023, 11, 14).toEpochMillis()
        val seventeenDaysAgo = LocalDate(2023, 10, 28).toEpochMillis()
        val oneDayAgo = LocalDate(2023, 11, 13).toEpochMillis()
        val pastAiredDate = LocalDate(2023, 10, 15).toEpochMillis()

        dateTimeProvider.setCurrentTimeMillis(currentTime)

        val episodes = listOf(
            createNextEpisode(showTraktId = 1, showName = "Stale Show", lastWatchedAt = seventeenDaysAgo, firstAired = pastAiredDate),
            createNextEpisode(showTraktId = 2, showName = "Active Show", lastWatchedAt = oneDayAgo, firstAired = pastAiredDate),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            result.watchNext[0].showName shouldBe "Active Show"
            result.stale.size shouldBe 1
            result.stale[0].showName shouldBe "Stale Show"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should filter episodes by show name when query is provided`() = runTest {
        val episodes = listOf(
            createNextEpisode(showTraktId = 1, showName = "Loki"),
            createNextEpisode(showTraktId = 2, showName = "Wednesday"),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)

        interactor("Loki")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            result.watchNext[0].showName shouldBe "Loki"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should calculate remaining episodes from episode data`() = runTest {
        val episodes = listOf(
            createNextEpisode(showTraktId = 1, showName = "Loki", watchedCount = 5, totalCount = 10),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            result.watchNext[0].remainingEpisodes shouldBe 5
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should handle case insensitive query filtering`() = runTest {
        val episodes = listOf(
            createNextEpisode(showTraktId = 1, showName = "Loki"),
            createNextEpisode(showTraktId = 2, showName = "Wednesday"),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)

        interactor("loki")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            result.watchNext[0].showName shouldBe "Loki"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should preserve episode details in output`() = runTest {
        val episodes = listOf(
            NextEpisodeWithShow(
                showTraktId = 1,
                showTmdbId = 1,
                showName = "Loki",
                showPoster = "/poster.jpg",
                showStatus = "Ended",
                showYear = "2021",
                episodeId = 101,
                episodeName = "Glorious Purpose",
                seasonId = 10,
                seasonNumber = 1,
                episodeNumber = 1,
                runtime = 51,
                stillPath = "/still.jpg",
                overview = "Episode overview",
                firstAired = LocalDate(2021, 6, 9).toEpochMillis(),
                lastWatchedAt = null,
                seasonCount = 2,
                episodeCount = 12,
                watchedCount = 0,
                totalCount = 10,
            ),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            val episode = result.watchNext[0]
            episode.showTraktId shouldBe 1
            episode.showName shouldBe "Loki"
            episode.episodeId shouldBe 101
            episode.episodeTitle shouldBe "Glorious Purpose"
            episode.episodeNumberFormatted shouldBe "S01 | E01"
            episode.seasonNumber shouldBe 1
            episode.episodeNumber shouldBe 1
            episode.formattedRuntime shouldBe "51 min"
            episode.overview shouldBe "Episode overview"
            episode.firstAired shouldBe LocalDate(2021, 6, 9).toEpochMillis()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should filter out episodes with unknown air date`() = runTest {
        val episodes = listOf(
            createNextEpisode(showTraktId = 1, showName = "Loki", firstAired = LocalDate(2021, 6, 9).toEpochMillis()),
            createNextEpisode(showTraktId = 2, showName = "Wednesday", firstAired = null),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            result.watchNext[0].showName shouldBe "Loki"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should format episode number with padding`() = runTest {
        val episodes = listOf(
            createNextEpisode(showTraktId = 1, showName = "Show", seasonNumber = 10, episodeNumber = 5),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext[0].episodeNumberFormatted shouldBe "S10 | E05"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return null formatted runtime when runtime is null`() = runTest {
        val episodes = listOf(
            createNextEpisode(showTraktId = 1, showName = "Show", runtime = null),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext[0].formattedRuntime shouldBe null
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should filter out episodes that have not aired yet`() = runTest {
        val pastEpoch = dateTimeProvider.nowMillis() - (14 * 24 * 60 * 60 * 1000L)
        val futureEpoch = dateTimeProvider.nowMillis() + (17 * 24 * 60 * 60 * 1000L)

        val episodes = listOf(
            createNextEpisode(showTraktId = 1, showName = "Aired Show", firstAired = pastEpoch),
            createNextEpisode(showTraktId = 2, showName = "Future Show", firstAired = futureEpoch),
        )
        upNextRepository.setNextEpisodesForWatchlist(episodes)

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            result.watchNext[0].showName shouldBe "Aired Show"
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun createNextEpisode(
        showTraktId: Long,
        showName: String,
        lastWatchedAt: Long? = null,
        firstAired: Long? = LocalDate(2021, 6, 9).toEpochMillis(),
        watchedCount: Long = 0,
        totalCount: Long = 10,
        seasonNumber: Long = 1L,
        episodeNumber: Long = 2L,
        runtime: Long? = 45L,
    ) = NextEpisodeWithShow(
        showTraktId = showTraktId,
        showTmdbId = showTraktId,
        showName = showName,
        showPoster = "/poster.jpg",
        showStatus = "Ended",
        showYear = "2024",
        episodeId = showTraktId * 100 + 1,
        episodeName = "Episode Title",
        seasonId = 1L,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        runtime = runtime,
        stillPath = "/still.jpg",
        overview = "Overview",
        firstAired = firstAired,
        lastWatchedAt = lastWatchedAt,
        seasonCount = 2,
        episodeCount = 12,
        watchedCount = watchedCount,
        totalCount = totalCount,
    )
}
