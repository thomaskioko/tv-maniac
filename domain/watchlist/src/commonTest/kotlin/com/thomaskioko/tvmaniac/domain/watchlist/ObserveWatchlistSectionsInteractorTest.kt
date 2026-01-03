package com.thomaskioko.tvmaniac.domain.watchlist

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.db.FollowedShows
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SearchFollowedShows
import com.thomaskioko.tvmaniac.domain.watchlist.model.WatchlistSections
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
class ObserveWatchlistSectionsInteractorTest {
    private val testDispatcher = StandardTestDispatcher()
    private val watchlistRepository = FakeWatchlistRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val dateTimeProvider = FakeDateTimeProvider()

    private lateinit var interactor: ObserveWatchlistSectionsInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        interactor = ObserveWatchlistSectionsInteractor(
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
    fun `should return empty sections when watchlist is empty`() = runTest {
        watchlistRepository.setObserveResult(emptyList())
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
        val watchlist = createTestWatchlist()
        watchlistRepository.setObserveResult(watchlist)
        episodeRepository.setNextEpisodesForWatchlist(emptyList())

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
    fun `should group stale items when lastWatched is over 7 days ago`() = runTest {
        val currentTime = 1000000000000L
        val twentyTwoDaysAgo = currentTime - (22 * 24 * 60 * 60 * 1000L)

        dateTimeProvider.setCurrentTimeMillis(currentTime)

        val watchlist = createTestWatchlist()
        watchlistRepository.setObserveResult(watchlist)
        episodeRepository.setNextEpisodesForWatchlist(
            listOf(
                createNextEpisode(showId = 84958, showName = "Loki", lastWatchedAt = twentyTwoDaysAgo),
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
    fun `should filter watchlist by query using search`() = runTest {
        val searchResults = listOf(
            SearchFollowedShows(
                id = Id(84958),
                name = "Loki",
                poster_path = "/poster.jpg",
                status = "Ended",
                first_air_date = "2024",
                created_at = 0,
                season_count = 2,
                episode_count = 12,
                metadata_status = "Ended",
                watched_count = 0,
                total_episode_count = 10,
            ),
        )
        watchlistRepository.setSearchResult(searchResults)
        episodeRepository.setNextEpisodesForWatchlist(emptyList())

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
        val watchlist = listOf(
            FollowedShows(
                id = Id(1),
                name = "Show with Progress",
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
        watchlistRepository.setObserveResult(watchlist)
        episodeRepository.setNextEpisodesForWatchlist(emptyList())

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

    private fun createTestWatchlist() = listOf(
        FollowedShows(
            id = Id(84958),
            name = "Loki",
            poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
            status = "Ended",
            first_air_date = "2024",
            created_at = 0,
            season_count = 2,
            episode_count = 12,
            watched_count = 0,
            total_episode_count = 10,
        ),
        FollowedShows(
            id = Id(1232),
            name = "The Lazarus Project",
            poster_path = "/lazarus_poster.jpg",
            status = "Ongoing",
            first_air_date = "2023",
            created_at = 0,
            season_count = 1,
            episode_count = 8,
            watched_count = 0,
            total_episode_count = 10,
        ),
    )

    private fun createNextEpisode(
        showId: Long,
        showName: String,
        lastWatchedAt: Long? = null,
    ) = NextEpisodeWithShow(
        showId = showId,
        showName = showName,
        showPoster = "/poster.jpg",
        episodeId = 1L,
        episodeName = "Episode 1",
        seasonId = 1L,
        seasonNumber = 1L,
        episodeNumber = 1L,
        runtime = 45L,
        stillPath = "/still.jpg",
        overview = "Overview",
        airDate = null,
        lastWatchedAt = lastWatchedAt,
    )
}
