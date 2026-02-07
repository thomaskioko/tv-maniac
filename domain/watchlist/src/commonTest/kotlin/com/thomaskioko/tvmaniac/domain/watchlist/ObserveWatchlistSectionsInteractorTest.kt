package com.thomaskioko.tvmaniac.domain.watchlist

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.domain.watchlist.model.WatchlistSections
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
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
    private val upNextRepository = FakeUpNextRepository()
    private val dateTimeProvider = FakeDateTimeProvider()

    private lateinit var interactor: ObserveWatchlistSectionsInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        interactor = ObserveWatchlistSectionsInteractor(
            upNextRepository = upNextRepository,
            dateTimeProvider = dateTimeProvider,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should return empty sections when watchlist is empty`() = runTest {
        upNextRepository.setNextEpisodesForWatchlist(emptyList())

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
    fun `should return items in watchNext given recent followedAt and no lastWatched`() = runTest {
        val currentTime = 1000000000000L
        val recentFollowedAt = currentTime - 1000L

        dateTimeProvider.setCurrentTimeMillis(currentTime)

        upNextRepository.setNextEpisodesForWatchlist(
            listOf(
                createNextEpisode(showTraktId = 84958, showName = "Loki", followedAt = recentFollowedAt),
                createNextEpisode(showTraktId = 1232, showName = "The Lazarus Project", followedAt = recentFollowedAt),
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
        val recentFollowedAt = currentTime - 1000L

        dateTimeProvider.setCurrentTimeMillis(currentTime)

        upNextRepository.setNextEpisodesForWatchlist(
            listOf(
                createNextEpisode(showTraktId = 84958, showName = "Loki", lastWatchedAt = twentyTwoDaysAgo),
                createNextEpisode(showTraktId = 1232, showName = "The Lazarus Project", followedAt = recentFollowedAt),
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
    fun `should group items as stale when followedAt is over 21 days ago and never watched`() = runTest {
        val currentTime = 1000000000000L
        val twentyTwoDaysAgo = currentTime - (22 * 24 * 60 * 60 * 1000L)
        val recentFollowedAt = currentTime - 1000L

        dateTimeProvider.setCurrentTimeMillis(currentTime)

        upNextRepository.setNextEpisodesForWatchlist(
            listOf(
                createNextEpisode(showTraktId = 84958, showName = "Old Follow", followedAt = twentyTwoDaysAgo),
                createNextEpisode(showTraktId = 1232, showName = "Recent Follow", followedAt = recentFollowedAt),
            ),
        )

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            result.watchNext[0].title shouldBe "Recent Follow"
            result.stale.size shouldBe 1
            result.stale[0].title shouldBe "Old Follow"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should use lastWatchedAt over followedAt when both are present`() = runTest {
        val currentTime = 1000000000000L
        val twentyTwoDaysAgo = currentTime - (22 * 24 * 60 * 60 * 1000L)
        val recentWatch = currentTime - 1000L

        dateTimeProvider.setCurrentTimeMillis(currentTime)

        upNextRepository.setNextEpisodesForWatchlist(
            listOf(
                createNextEpisode(
                    showTraktId = 84958,
                    showName = "Recently Watched",
                    lastWatchedAt = recentWatch,
                    followedAt = twentyTwoDaysAgo,
                ),
            ),
        )

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            result.watchNext[0].title shouldBe "Recently Watched"
            result.stale.size shouldBe 0
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should filter watchlist by query`() = runTest {
        upNextRepository.setNextEpisodesForWatchlist(
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
        upNextRepository.setNextEpisodesForWatchlist(
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
        upNextRepository.setNextEpisodesForWatchlist(
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
        followedAt: Long? = null,
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
        followedAt = followedAt,
        firstAired = null,
        lastWatchedAt = lastWatchedAt,
        seasonCount = 2,
        episodeCount = 12,
        watchedCount = watchedCount,
        totalCount = totalCount,
    )
}
