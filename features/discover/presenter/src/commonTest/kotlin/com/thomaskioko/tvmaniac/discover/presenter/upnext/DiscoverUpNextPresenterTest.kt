package com.thomaskioko.tvmaniac.discover.presenter.upnext

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveUpNextInteractor
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetParam
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetRoute
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.ScreenSource
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.navigation.testing.TestNavigator
import com.thomaskioko.tvmaniac.navigation.testing.test
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DiscoverUpNextPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val upNextRepository = FakeUpNextRepository()
    private val observeUpNextInteractor = ObserveUpNextInteractor(upNextRepository)

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should map next episodes into state`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            upNextRepository.setNextEpisodesForWatchlist(nextEpisodes())

            var state = awaitItem()
            while (state.nextEpisodes.isEmpty()) {
                state = awaitItem()
            }
            state.nextEpisodes.size shouldBe 1
            state.nextEpisodes.first().showId shouldBe 1L
            state.nextEpisodes.first().episodeNumberFormatted shouldBe "S1E1"
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should open episode sheet when episode is long pressed`() = runTest {
        val testNavigator = TestNavigator()
        val presenter = buildPresenter(navigator = testNavigator)

        testNavigator.test {
            presenter.dispatch(DiscoverEpisodeLongPressed(showId = 1L, episodeId = 99L))

            awaitNavigateTo(
                EpisodeSheetRoute(EpisodeSheetParam(episodeId = 99L, source = ScreenSource.DISCOVER)),
            )
        }
    }

    private fun buildPresenter(
        lifecycle: LifecycleRegistry = LifecycleRegistry(),
        navigator: Navigator = NoOpNavigator(),
    ): DiscoverUpNextPresenter = DiscoverUpNextPresenter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        navigator = navigator,
        observeUpNextInteractor = observeUpNextInteractor,
    ).also { lifecycle.resume() }

    private fun nextEpisodes() = listOf(
        NextEpisodeWithShow(
            showId = 1L,
            showName = "Test Show",
            showPoster = "/poster.jpg",
            showStatus = "Ended",
            showYear = "2024",
            episodeId = 99L,
            episodeName = "Pilot",
            seasonId = 2L,
            seasonNumber = 1L,
            episodeNumber = 1L,
            runtime = 45L,
            stillPath = "/still.jpg",
            overview = "Overview",
            seasonCount = 2,
            episodeCount = 12,
            watchedCount = 0,
            totalCount = 10,
        ),
    )
}
