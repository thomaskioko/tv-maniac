package com.thomaskioko.tvmaniac.domain.watchlist

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.domain.watchlist.model.WatchlistSections
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ObserveWatchlistSectionsInteractorTest {
    private val testDispatcher = StandardTestDispatcher()
    private val episodeRepository = FakeEpisodeRepository()
    private val dateTimeProvider = FakeDateTimeProvider()

    private lateinit var interactor: ObserveWatchlistSectionsInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        interactor = ObserveWatchlistSectionsInteractor(
            episodeRepository = episodeRepository,
            dateTimeProvider = dateTimeProvider,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should return empty sections when watchlist is empty`() = runTest {
        episodeRepository.setNextEpisodesForWatchlist(emptyList())

        interactor("")

        interactor.flow.test {
            awaitItem() shouldBe WatchlistSections(
                watchNext = emptyList(),
                stale = emptyList(),
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return all items in watchNext when no lastWatched data`() = runTest {
        episodeRepository.setNextEpisodesForWatchlist(
            listOf(
                createNextEpisode(showTraktId = 84958, showName = "Loki"),
                createNextEpisode(showTraktId = 1232, showName = "The Lazarus Project"),
            ),
        )

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 2
            result.stale.size shouldBe 0
            result.watchNext[0].title shouldBe "Loki"
            result.watchNext[1].title shouldBe "The Lazarus Project"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should group stale items when lastWatched is over 21 days ago`() = runTest {
        val currentTime = 1000000000000L
        val twentyTwoDaysAgo = currentTime - (22 * 24 * 60 * 60 * 1000L)

        dateTimeProvider.setCurrentTimeMillis(currentTime)

        episodeRepository.setNextEpisodesForWatchlist(
            listOf(
                createNextEpisode(showTraktId = 84958, showName = "Loki", lastWatchedAt = twentyTwoDaysAgo),
                createNextEpisode(showTraktId = 1232, showName = "The Lazarus Project"),
            ),
        )

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            result.watchNext[0].title shouldBe "The Lazarus Project"
            result.stale.size shouldBe 1
            result.stale[0].title shouldBe "Loki"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should filter watchlist by query`() = runTest {
        episodeRepository.setNextEpisodesForWatchlist(
            listOf(
                createNextEpisode(showTraktId = 84958, showName = "Loki"),
                createNextEpisode(showTraktId = 1232, showName = "The Lazarus Project"),
            ),
        )

        interactor("Loki")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            result.watchNext[0].title shouldBe "Loki"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should calculate watch progress correctly`() = runTest {
        episodeRepository.setNextEpisodesForWatchlist(
            listOf(
                createNextEpisode(
                    showTraktId = 1,
                    showName = "Show with Progress",
                    watchedCount = 5,
                    totalCount = 10,
                ),
            ),
        )

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            result.watchNext[0].watchProgress shouldBe 0.5f
            result.watchNext[0].episodesWatched shouldBe 5
            result.watchNext[0].totalEpisodesTracked shouldBe 10
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should exclude completed shows from sections`() = runTest {
        episodeRepository.setNextEpisodesForWatchlist(
            listOf(
                createNextEpisode(showTraktId = 1, showName = "In Progress", watchedCount = 5, totalCount = 10),
                createNextEpisode(showTraktId = 2, showName = "Completed", watchedCount = 10, totalCount = 10),
            ),
        )

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            result.watchNext[0].title shouldBe "In Progress"
            result.stale.size shouldBe 0
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun createNextEpisode(
        showTraktId: Long,
        showName: String,
        lastWatchedAt: Long? = null,
        watchedCount: Long = 0,
        totalCount: Long = 10,
    ) = NextEpisodeWithShow(
        showTraktId = showTraktId,
        showTmdbId = showTraktId,
        showName = showName,
        showPoster = "/poster.jpg",
        showStatus = "Ended",
        showYear = "2024",
        episodeId = 1L,
        episodeName = "Episode 1",
        seasonId = 1L,
        seasonNumber = 1L,
        episodeNumber = 1L,
        runtime = 45L,
        stillPath = "/still.jpg",
        overview = "Overview",
        firstAired = null,
        lastWatchedAt = lastWatchedAt,
        seasonCount = 2,
        episodeCount = 12,
        watchedCount = watchedCount,
        totalCount = totalCount,
    )
}
