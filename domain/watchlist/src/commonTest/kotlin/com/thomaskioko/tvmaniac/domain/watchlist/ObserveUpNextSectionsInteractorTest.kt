package com.thomaskioko.tvmaniac.domain.watchlist

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.db.FollowedShows
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.domain.watchlist.model.UpNextSections
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
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
class ObserveUpNextSectionsInteractorTest {
    private val testDispatcher = StandardTestDispatcher()
    private val watchlistRepository = FakeWatchlistRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val dateTimeProvider = FakeDateTimeProvider()

    private lateinit var interactor: ObserveUpNextSectionsInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        interactor = ObserveUpNextSectionsInteractor(
            watchlistRepository = watchlistRepository,
            episodeRepository = episodeRepository,
            dateTimeProvider = dateTimeProvider,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should return empty sections when no episodes`() = runTest {
        watchlistRepository.setObserveResult(emptyList())
        episodeRepository.setNextEpisodesForWatchlist(emptyList())

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
            createNextEpisode(showId = 1, showName = "Loki"),
            createNextEpisode(showId = 2, showName = "Wednesday"),
        )
        watchlistRepository.setObserveResult(createWatchlist())
        episodeRepository.setNextEpisodesForWatchlist(episodes)

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
    fun `should group stale episodes when lastWatched is over 7 days ago`() = runTest {
        val currentTime = 1000000000000L
        val eightDaysAgo = currentTime - (8 * 24 * 60 * 60 * 1000L)
        val oneDayAgo = currentTime - (1 * 24 * 60 * 60 * 1000L)

        dateTimeProvider.setCurrentTimeMillis(currentTime)

        val episodes = listOf(
            createNextEpisode(showId = 1, showName = "Stale Show", lastWatchedAt = eightDaysAgo),
            createNextEpisode(showId = 2, showName = "Active Show", lastWatchedAt = oneDayAgo),
        )
        watchlistRepository.setObserveResult(createWatchlist())
        episodeRepository.setNextEpisodesForWatchlist(episodes)

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
            createNextEpisode(showId = 1, showName = "Loki"),
            createNextEpisode(showId = 2, showName = "Wednesday"),
        )
        watchlistRepository.setObserveResult(createWatchlist())
        episodeRepository.setNextEpisodesForWatchlist(episodes)

        interactor("Loki")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            result.watchNext[0].showName shouldBe "Loki"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should calculate remaining episodes from watchlist`() = runTest {
        val watchlist = listOf(
            FollowedShows(
                show_id = Id(1),
                name = "Loki",
                poster_path = "/poster.jpg",
                status = "Ongoing",
                first_air_date = "2024",
                created_at = 0,
                season_count = 2,
                episode_count = 20,
                watched_count = 5,
                total_episode_count = 10,
            ),
        )
        val episodes = listOf(
            createNextEpisode(showId = 1, showName = "Loki"),
        )
        watchlistRepository.setObserveResult(watchlist)
        episodeRepository.setNextEpisodesForWatchlist(episodes)

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
            createNextEpisode(showId = 1, showName = "Loki"),
            createNextEpisode(showId = 2, showName = "Wednesday"),
        )
        watchlistRepository.setObserveResult(createWatchlist())
        episodeRepository.setNextEpisodesForWatchlist(episodes)

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
                showId = 1,
                showName = "Loki",
                showPoster = "/poster.jpg",
                episodeId = 101,
                episodeName = "Glorious Purpose",
                seasonId = 10,
                seasonNumber = 1,
                episodeNumber = 1,
                runtime = 51,
                stillPath = "/still.jpg",
                overview = "Episode overview",
                airDate = "2021-06-09",
                lastWatchedAt = null,
            ),
        )
        watchlistRepository.setObserveResult(createWatchlist())
        episodeRepository.setNextEpisodesForWatchlist(episodes)

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            val episode = result.watchNext[0]
            episode.showId shouldBe 1
            episode.showName shouldBe "Loki"
            episode.episodeId shouldBe 101
            episode.episodeTitle shouldBe "Glorious Purpose"
            episode.seasonNumber shouldBe 1
            episode.episodeNumber shouldBe 1
            episode.runtime shouldBe 51
            episode.overview shouldBe "Episode overview"
            episode.airDate shouldBe "2021-06-09"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should filter out episodes with unknown air date`() = runTest {
        val episodes = listOf(
            createNextEpisode(showId = 1, showName = "Loki", airDate = "2021-06-09"),
            createNextEpisode(showId = 2, showName = "Wednesday", airDate = null),
        )
        watchlistRepository.setObserveResult(createWatchlist())
        episodeRepository.setNextEpisodesForWatchlist(episodes)

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            result.watchNext[0].showName shouldBe "Loki"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should filter out episodes that have not aired yet`() = runTest {
        dateTimeProvider.setDaysUntilAir("2024-01-01", -14) // Aired 14 days ago
        dateTimeProvider.setDaysUntilAir("2024-02-01", 17) // Airs in 17 days

        val episodes = listOf(
            createNextEpisode(showId = 1, showName = "Aired Show", airDate = "2024-01-01"),
            createNextEpisode(showId = 2, showName = "Future Show", airDate = "2024-02-01"),
        )
        watchlistRepository.setObserveResult(createWatchlist())
        episodeRepository.setNextEpisodesForWatchlist(episodes)

        interactor("")

        interactor.flow.test {
            val result = awaitItem()
            result.watchNext.size shouldBe 1
            result.watchNext[0].showName shouldBe "Aired Show"
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun createWatchlist() = listOf(
        FollowedShows(
            show_id = Id(1),
            name = "Loki",
            poster_path = "/poster.jpg",
            status = "Ended",
            first_air_date = "2024",
            created_at = 0,
            season_count = 2,
            episode_count = 12,
            watched_count = 0,
            total_episode_count = 10,
        ),
        FollowedShows(
            show_id = Id(2),
            name = "Wednesday",
            poster_path = "/poster2.jpg",
            status = "Ongoing",
            first_air_date = "2023",
            created_at = 0,
            season_count = 1,
            episode_count = 8,
            watched_count = 0,
            total_episode_count = 8,
        ),
    )

    private fun createNextEpisode(
        showId: Long,
        showName: String,
        lastWatchedAt: Long? = null,
        airDate: String? = "2021-06-09", // Default to a past aired date
    ) = NextEpisodeWithShow(
        showId = showId,
        showName = showName,
        showPoster = "/poster.jpg",
        episodeId = showId * 100 + 1,
        episodeName = "Episode Title",
        seasonId = 1L,
        seasonNumber = 1L,
        episodeNumber = 2L,
        runtime = 45L,
        stillPath = "/still.jpg",
        overview = "Overview",
        airDate = airDate,
        lastWatchedAt = lastWatchedAt,
    )
}
