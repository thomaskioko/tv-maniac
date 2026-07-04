package com.thomaskioko.tvmaniac.discover.presenter.featured

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.featuredshows.api.interactor.FeaturedShowsInteractor
import com.thomaskioko.tvmaniac.data.featuredshows.testing.FakeFeaturedShowsRepository
import com.thomaskioko.tvmaniac.discover.presenter.model.DiscoverShow
import com.thomaskioko.tvmaniac.domain.discover.ObserveFeaturedShowsInteractor
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.navigation.testing.TestNavigator
import com.thomaskioko.tvmaniac.navigation.testing.test
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
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

class DiscoverFeaturedPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val featuredShowsRepository = FakeFeaturedShowsRepository()
    private val accountManager = FakeAccountManager()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val observeFeaturedShowsInteractor = ObserveFeaturedShowsInteractor(featuredShowsRepository)
    private val featuredShowsInteractor = FeaturedShowsInteractor(featuredShowsRepository, dispatchers)

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit featured shows when repository has data`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            featuredShowsRepository.setFeaturedShows(showList())

            var state = awaitItem()
            while (state.featuredShows.isEmpty()) {
                state = awaitItem()
            }
            state.featuredShows shouldBe expectedShows()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should navigate to show details when featured show is clicked`() = runTest {
        val testNavigator = TestNavigator()
        val presenter = buildPresenter(navigator = testNavigator)

        testNavigator.test {
            presenter.dispatch(FeaturedShowClicked(showId = 84958L))

            awaitNavigateTo(ShowDetailsRoute(ShowDetailsParam(showId = 84958L)))
        }
    }

    private fun buildPresenter(
        lifecycle: LifecycleRegistry = LifecycleRegistry(),
        navigator: Navigator = NoOpNavigator(),
    ): DiscoverFeaturedPresenter = DiscoverFeaturedPresenter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        navigator = navigator,
        observeFeaturedShowsInteractor = observeFeaturedShowsInteractor,
        featuredShowsInteractor = featuredShowsInteractor,
        accountManager = accountManager,
        errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
        logger = FakeLogger(),
    ).also { lifecycle.resume() }

    private fun showList() = List(3) {
        ShowEntity(showId = 84958L, tmdbId = 84958L, title = "Loki", posterPath = "/loki.jpg", inLibrary = false)
    }.toImmutableList()

    private fun expectedShows() = showList().map {
        DiscoverShow(
            showId = it.showId,
            tmdbId = it.tmdbId,
            title = it.title,
            posterImageUrl = it.posterPath,
            inLibrary = it.inLibrary,
            overView = it.overview,
        )
    }.toImmutableList()
}
