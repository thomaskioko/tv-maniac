package com.thomaskioko.tvmaniac.domain.watchlist

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.watchlist.presenter.ChangeListStyleClicked
import com.thomaskioko.tvmaniac.watchlist.presenter.FakeWatchlistPresenterFactory
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistPresenter
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistQueryChanged
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistState
import com.thomaskioko.tvmaniac.watchlist.presenter.model.EpisodeBadge
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class WatchlistPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val factory = FakeWatchlistPresenterFactory()

    private lateinit var presenter: WatchlistPresenter

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)

        lifecycle.resume()
        presenter = factory.invoke(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            navigateToShowDetails = { },
            navigateToSeason = { _, _, _ -> },
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit initial state on init`() = runTest(factory.testDispatcher) {
        presenter.state.test {
            awaitItem() shouldBe WatchlistState()
        }
    }

    @Test
    fun `should emit WatchlistState with content on success`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe WatchlistState()

            factory.repository.setObserveResult(cachedResult)

            val state = awaitItem()
            state.query shouldBe ""
            state.isSearchActive shouldBe false
            state.isGridMode shouldBe true
            state.watchNextItems shouldBe expectedUiResult(cachedResult)

            factory.repository.setObserveResult(updatedData)

            val secondUpdate = awaitItem()
            secondUpdate.watchNextItems shouldBe expectedUiResult()
        }
    }

    @Test
    fun `should toggle list style when ChangeListStyleClicked is dispatched`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe WatchlistState()

            factory.repository.setObserveResult(cachedResult)

            val initialState = awaitItem()
            initialState.isGridMode shouldBe true
            initialState.watchNextItems shouldBe expectedUiResult(cachedResult)

            presenter.dispatch(ChangeListStyleClicked)

            val updatedState = awaitItem()
            updatedState.isGridMode shouldBe false

            presenter.dispatch(ChangeListStyleClicked)

            val finalState = awaitItem()
            finalState.isGridMode shouldBe true
        }
    }

    @Test
    fun `should update query and search state when WatchlistQueryChanged is dispatched`() = runTest {
        factory.repository.setSearchResult(emptyList())

        presenter.state.test {
            awaitItem() shouldBe WatchlistState()

            factory.repository.setObserveResult(cachedResult)

            val initialState = awaitItem()
            initialState.query shouldBe ""
            initialState.isSearchActive shouldBe false

            presenter.dispatch(WatchlistQueryChanged("test query"))

            val updatedState = awaitItem()
            updatedState.query shouldBe "test query"
            updatedState.isSearchActive shouldBe true
        }
    }

    @Test
    fun `should emit watchNextEpisodes when episodes are available`() = runTest {
        val nextEpisodes = listOf(
            createNextEpisodeWithShow(showId = 1L, showName = "Loki", episodeId = 101L),
            createNextEpisodeWithShow(showId = 2L, showName = "Wednesday", episodeId = 201L),
        )

        presenter.state.test {
            awaitItem() shouldBe WatchlistState()

            factory.episodeRepository.setNextEpisodesForWatchlist(nextEpisodes)

            val state = awaitItem()
            state.watchNextEpisodes.size shouldBe 2
            state.watchNextEpisodes[0].showName shouldBe "Loki"
            state.watchNextEpisodes[1].showName shouldBe "Wednesday"
        }
    }

    @Test
    fun `should filter watchNextEpisodes by show name when query is active`() = runTest {
        val nextEpisodes = listOf(
            createNextEpisodeWithShow(showId = 1L, showName = "Loki", episodeId = 101L),
            createNextEpisodeWithShow(showId = 2L, showName = "Wednesday", episodeId = 201L),
        )

        presenter.state.test {
            awaitItem() shouldBe WatchlistState()

            factory.episodeRepository.setNextEpisodesForWatchlist(nextEpisodes)
            awaitItem()

            presenter.dispatch(WatchlistQueryChanged("Loki"))

            skipItems(1)

            val filteredState = awaitItem()
            filteredState.query shouldBe "Loki"
            filteredState.watchNextEpisodes.size shouldBe 1
            filteredState.watchNextEpisodes[0].showName shouldBe "Loki"
        }
    }

    @Test
    fun `should show PREMIERE badge for episode 1`() = runTest {
        val testDate = "2025-12-14"
        val testDateMillis = 20076L * 24 * 60 * 60 * 1000L
        factory.dateTimeProvider.setCurrentTimeMillis(testDateMillis)

        val premiereEpisode = createNextEpisodeWithShow(
            showId = 1L,
            showName = "Loki",
            episodeId = 101L,
            episodeNumber = 1L,
            airDate = testDate,
        )

        presenter.state.test {
            awaitItem() shouldBe WatchlistState()

            factory.episodeRepository.setNextEpisodesForWatchlist(listOf(premiereEpisode))

            val state = awaitItem()
            state.watchNextEpisodes.size shouldBe 1
            state.watchNextEpisodes[0].badge shouldBe EpisodeBadge.PREMIERE
        }
    }

    @Test
    fun `should group episodes into stale section when last watched over 7 days ago`() = runTest {
        val currentTime = 1000000000000L
        val eightDaysAgo = currentTime - (8 * 24 * 60 * 60 * 1000L)

        factory.dateTimeProvider.setCurrentTimeMillis(currentTime)

        val staleEpisode = createNextEpisodeWithShow(
            showId = 1L,
            showName = "Stale Show",
            episodeId = 101L,
            lastWatchedAt = eightDaysAgo,
        )
        val activeEpisode = createNextEpisodeWithShow(
            showId = 2L,
            showName = "Active Show",
            episodeId = 201L,
            lastWatchedAt = currentTime - (1 * 24 * 60 * 60 * 1000L),
        )

        presenter.state.test {
            awaitItem() shouldBe WatchlistState()

            factory.episodeRepository.setNextEpisodesForWatchlist(listOf(staleEpisode, activeEpisode))

            val state = awaitItem()
            state.watchNextEpisodes.size shouldBe 1
            state.watchNextEpisodes[0].showName shouldBe "Active Show"
            state.staleEpisodes.size shouldBe 1
            state.staleEpisodes[0].showName shouldBe "Stale Show"
        }
    }

    private fun createNextEpisodeWithShow(
        showId: Long,
        showName: String,
        episodeId: Long,
        episodeNumber: Long = 2L,
        lastWatchedAt: Long? = null,
        airDate: String? = "2021-06-09",
    ) = NextEpisodeWithShow(
        showId = showId,
        showName = showName,
        showPoster = "/poster.jpg",
        episodeId = episodeId,
        episodeName = "Episode Title",
        seasonId = 1L,
        seasonNumber = 1L,
        episodeNumber = episodeNumber,
        runtime = 45L,
        stillPath = "/still.jpg",
        overview = "Episode overview",
        airDate = airDate,
        lastWatchedAt = lastWatchedAt,
    )
}
