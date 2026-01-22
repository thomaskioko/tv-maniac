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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

private fun LocalDate.toEpochMillis(): Long =
    atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

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

            factory.episodeRepository.setNextEpisodesForWatchlist(cachedNextEpisodes)

            val state = awaitItem()
            state.query shouldBe ""
            state.isSearchActive shouldBe false
            state.isGridMode shouldBe true
            state.watchNextItems shouldBe expectedUiResult(cachedNextEpisodes)

            factory.episodeRepository.setNextEpisodesForWatchlist(updatedNextEpisodes)

            val secondUpdate = awaitItem()
            secondUpdate.watchNextItems shouldBe expectedUiResult()
        }
    }

    @Test
    fun `should toggle list style when ChangeListStyleClicked is dispatched`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe WatchlistState()

            factory.episodeRepository.setNextEpisodesForWatchlist(cachedNextEpisodes)

            val initialState = awaitItem()
            initialState.isGridMode shouldBe true
            initialState.watchNextItems shouldBe expectedUiResult(cachedNextEpisodes)

            presenter.dispatch(ChangeListStyleClicked(isGridMode = true))

            val updatedState = awaitItem()
            updatedState.isGridMode shouldBe false

            presenter.dispatch(ChangeListStyleClicked(isGridMode = false))

            val finalState = awaitItem()
            finalState.isGridMode shouldBe true
        }
    }

    @Test
    fun `should update query and search state when WatchlistQueryChanged is dispatched`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe WatchlistState()

            factory.episodeRepository.setNextEpisodesForWatchlist(cachedNextEpisodes)

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
            createNextEpisodeWithShow(showTraktId = 1L, showName = "Loki", episodeId = 101L),
            createNextEpisodeWithShow(showTraktId = 2L, showName = "Wednesday", episodeId = 201L),
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
            createNextEpisodeWithShow(showTraktId = 1L, showName = "Loki", episodeId = 101L),
            createNextEpisodeWithShow(showTraktId = 2L, showName = "Wednesday", episodeId = 201L),
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
        val currentTime = LocalDate(2024, 11, 14).toEpochMillis()
        factory.dateTimeProvider.setCurrentTimeMillis(currentTime)

        val premiereEpisode = createNextEpisodeWithShow(
            showTraktId = 1L,
            showName = "Loki",
            episodeId = 101L,
            episodeNumber = 1L,
            seasonNumber = 2L,
            firstAired = currentTime,
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
    fun `should group episodes into stale section when last watched over 16 days ago`() = runTest {
        val currentTime = LocalDate(2023, 11, 14).toEpochMillis()
        val seventeenDaysAgo = LocalDate(2023, 10, 28).toEpochMillis()
        val oneDayAgo = LocalDate(2023, 11, 13).toEpochMillis()

        factory.dateTimeProvider.setCurrentTimeMillis(currentTime)

        val staleEpisode = createNextEpisodeWithShow(
            showTraktId = 1L,
            showName = "Stale Show",
            episodeId = 101L,
            lastWatchedAt = seventeenDaysAgo,
        )
        val activeEpisode = createNextEpisodeWithShow(
            showTraktId = 2L,
            showName = "Active Show",
            episodeId = 201L,
            lastWatchedAt = oneDayAgo,
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
        showTraktId: Long,
        showName: String,
        episodeId: Long,
        episodeNumber: Long = 2L,
        seasonNumber: Long = 1L,
        lastWatchedAt: Long? = null,
        firstAired: Long? = LocalDate(2021, 6, 9).toEpochMillis(),
    ) = NextEpisodeWithShow(
        showTraktId = showTraktId,
        showTmdbId = showTraktId,
        showName = showName,
        showPoster = "/poster.jpg",
        showStatus = "Ended",
        showYear = "2024",
        episodeId = episodeId,
        episodeName = "Episode Title",
        seasonId = seasonNumber,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        runtime = 45L,
        stillPath = "/still.jpg",
        overview = "Episode overview",
        firstAired = firstAired,
        lastWatchedAt = lastWatchedAt,
        seasonCount = 2,
        episodeCount = 12,
        watchedCount = 0,
        totalCount = 10,
    )
}
