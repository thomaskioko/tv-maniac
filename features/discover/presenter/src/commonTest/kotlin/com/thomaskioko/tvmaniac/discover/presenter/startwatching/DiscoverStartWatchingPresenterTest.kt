package com.thomaskioko.tvmaniac.discover.presenter.startwatching

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.datastore.api.DiscoverSection
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.discover.presenter.model.DiscoverShow
import com.thomaskioko.tvmaniac.domain.startwatching.ObserveStartWatchingInteractor
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.moreshows.nav.MoreShowsRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.navigation.testing.TestNavigator
import com.thomaskioko.tvmaniac.navigation.testing.test
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.shows.api.model.Category
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingShow
import com.thomaskioko.tvmaniac.startwatching.testing.FakeStartWatchingRepository
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DiscoverStartWatchingPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val startWatchingRepository = FakeStartWatchingRepository()
    private val datastoreRepository = FakeDatastoreRepository()
    private val fakeLocalizer = FakeLocalizer()
    private val observeStartWatchingInteractor = ObserveStartWatchingInteractor(startWatchingRepository)

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should map start watching shows into state with title`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            startWatchingRepository.setStartWatchingShows(startWatchingShows())

            var state = awaitItem()
            while (state.startWatchingShows.isEmpty()) {
                state = awaitItem()
            }
            state.startWatchingShows shouldBe expectedShows()
            state.startWatchingTitle shouldBe fakeLocalizer.getString(StringResourceKey.LabelStartWatching)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should report start watching not visible given it is hidden while keeping its data`() = runTest {
        datastoreRepository.saveHiddenDiscoverSections(setOf(DiscoverSection.START_WATCHING))
        val presenter = buildPresenter()

        presenter.state.test {
            startWatchingRepository.setStartWatchingShows(startWatchingShows())

            var state = awaitItem()
            while (state.startWatchingShows.isEmpty()) {
                state = awaitItem()
            }
            state.startWatchingShows shouldBe expectedShows()
            state.startWatchingVisible shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should navigate to show details when start watching item is clicked`() = runTest {
        val testNavigator = TestNavigator()
        val presenter = buildPresenter(navigator = testNavigator)

        testNavigator.test {
            presenter.dispatch(StartWatchingItemClicked(showId = 1L))

            awaitNavigateTo(ShowDetailsRoute(ShowDetailsParam(showId = 1L)))
        }
    }

    @Test
    fun `should navigate to more shows when start watching more is clicked`() = runTest {
        val testNavigator = TestNavigator()
        val presenter = buildPresenter(navigator = testNavigator)

        testNavigator.test {
            presenter.dispatch(StartWatchingMoreClicked)

            awaitNavigateTo(MoreShowsRoute(Category.START_WATCHING.id))
        }
    }

    private fun buildPresenter(
        lifecycle: LifecycleRegistry = LifecycleRegistry(),
        navigator: Navigator = NoOpNavigator(),
    ): DiscoverStartWatchingPresenter = DiscoverStartWatchingPresenter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        navigator = navigator,
        observeStartWatchingInteractor = observeStartWatchingInteractor,
        datastoreRepository = datastoreRepository,
        localizer = fakeLocalizer,
    ).also { lifecycle.resume() }

    private fun startWatchingShows() = listOf(
        StartWatchingShow(showId = 1L, tmdbId = 11L, title = "Breaking Bad", posterPath = "/1.jpg", year = "2008", inLibrary = true),
    )

    private fun expectedShows() = listOf(
        DiscoverShow(showId = 1L, tmdbId = 11L, title = "Breaking Bad", posterImageUrl = "/1.jpg", inLibrary = true),
    ).toImmutableList()
}
