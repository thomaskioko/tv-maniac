package com.thomaskioko.tvmaniac.domain.watchlist

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.FollowedShows
import com.thomaskioko.tvmaniac.db.Id
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
class ObservableWatchlistInteractorTest {
    private val testDispatcher = StandardTestDispatcher()

    private val watchlistRepository = FakeWatchlistRepository()

    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var interactor: ObservableWatchlistInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        interactor = ObservableWatchlistInteractor(
            watchlistRepository = watchlistRepository,
            dispatchers = coroutineDispatcher,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should return empty WatchlistData when repository returns empty list`() = runTest {
        watchlistRepository.setObserveResult(emptyList())

        interactor(ObservableWatchlistInteractor.Param())

        interactor.flow.test {
            awaitItem() shouldBe WatchlistData(
                watchlist = emptyList(),
                isGridMode = true,
                query = "",
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return populated WatchlistData when repository returns watchlist`() = runTest {
        val testWatchlist = createTestWatchlist()
        watchlistRepository.setObserveResult(testWatchlist)

        interactor(ObservableWatchlistInteractor.Param())

        interactor.flow.test {
            awaitItem() shouldBe WatchlistData(
                watchlist = testWatchlist,
                isGridMode = true,
                query = "",
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return WatchlistData with query when param contains query`() = runTest {
        val testQuery = "test query"
        val testWatchlist = createTestWatchlist()
        watchlistRepository.setObserveResult(testWatchlist)

        interactor(ObservableWatchlistInteractor.Param(query = testQuery))

        interactor.flow.test {
            awaitItem() shouldBe WatchlistData(
                watchlist = testWatchlist,
                isGridMode = true,
                query = testQuery,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return WatchlistData with list mode when repository returns false for grid mode`() = runTest {
        val testWatchlist = createTestWatchlist()
        watchlistRepository.setObserveResult(testWatchlist)
        watchlistRepository.saveListStyle(false) // Set to list mode

        interactor(ObservableWatchlistInteractor.Param())

        interactor.flow.test {
            awaitItem() shouldBe WatchlistData(
                watchlist = testWatchlist,
                isGridMode = false,
                query = "",
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should emit new data when repository data changes`() = runTest {
        val initialWatchlist = createTestWatchlist()
        val updatedWatchlist = createTestWatchlist().plus(
            FollowedShows(
                id = Id(999),
                name = "New Show",
                poster_path = "/new_poster.jpg",
                status = "Ongoing",
                first_air_date = "2024",
                created_at = 0,
                season_count = 2,
                episode_count = 20,
                watched_count = 0,
                total_episode_count = 10,
            ),
        )

        watchlistRepository.setObserveResult(initialWatchlist)

        interactor(ObservableWatchlistInteractor.Param())

        interactor.flow.test {
            awaitItem() shouldBe WatchlistData(
                watchlist = initialWatchlist,
                isGridMode = true,
                query = "",
            )

            watchlistRepository.setObserveResult(updatedWatchlist)

            awaitItem() shouldBe WatchlistData(
                watchlist = updatedWatchlist,
                isGridMode = true,
                query = "",
            )

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should emit new data when list style changes`() = runTest {
        val testWatchlist = createTestWatchlist()
        watchlistRepository.setObserveResult(testWatchlist)

        interactor(ObservableWatchlistInteractor.Param())

        interactor.flow.test {
            awaitItem() shouldBe WatchlistData(
                watchlist = testWatchlist,
                isGridMode = true,
                query = "",
            )

            watchlistRepository.saveListStyle(false)

            awaitItem() shouldBe WatchlistData(
                watchlist = testWatchlist,
                isGridMode = false,
                query = "",
            )

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
}
