package com.thomaskioko.tvmaniac.discover.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.featuredshows.api.interactor.FeaturedShowsInteractor
import com.thomaskioko.tvmaniac.data.featuredshows.testing.FakeFeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsInteractor
import com.thomaskioko.tvmaniac.data.popularshows.testing.FakePopularShowsRepository
import com.thomaskioko.tvmaniac.data.topratedshows.testing.FakeTopRatedShowsRepository
import com.thomaskioko.tvmaniac.data.trendingshows.testing.FakeTrendingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsInteractor
import com.thomaskioko.tvmaniac.data.upcomingshows.testing.FakeUpcomingShowsRepository
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsInteractor
import com.thomaskioko.tvmaniac.discover.presenter.catalog.DiscoverCatalogPresenter
import com.thomaskioko.tvmaniac.discover.presenter.catalog.di.DiscoverCatalogChildGraph
import com.thomaskioko.tvmaniac.discover.presenter.featured.DiscoverFeaturedPresenter
import com.thomaskioko.tvmaniac.discover.presenter.featured.di.DiscoverFeaturedChildGraph
import com.thomaskioko.tvmaniac.discover.presenter.startwatching.DiscoverStartWatchingPresenter
import com.thomaskioko.tvmaniac.discover.presenter.startwatching.di.DiscoverStartWatchingChildGraph
import com.thomaskioko.tvmaniac.discover.presenter.upnext.DiscoverUpNextPresenter
import com.thomaskioko.tvmaniac.discover.presenter.upnext.di.DiscoverUpNextChildGraph
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveUpNextInteractor
import com.thomaskioko.tvmaniac.domain.discover.ObserveFeaturedShowsInteractor
import com.thomaskioko.tvmaniac.domain.discover.ObservePopularShowsInteractor
import com.thomaskioko.tvmaniac.domain.discover.ObserveTopRatedShowsInteractor
import com.thomaskioko.tvmaniac.domain.discover.ObserveTrendingShowsInteractor
import com.thomaskioko.tvmaniac.domain.discover.ObserveUpcomingShowsInteractor
import com.thomaskioko.tvmaniac.domain.genre.GenreShowsInteractor
import com.thomaskioko.tvmaniac.domain.startwatching.ObserveStartWatchingInteractor
import com.thomaskioko.tvmaniac.genre.FakeGenreRepository
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.navigation.testing.TestNavigator
import com.thomaskioko.tvmaniac.navigation.testing.test
import com.thomaskioko.tvmaniac.search.nav.SearchRoute
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.startwatching.testing.FakeStartWatchingRepository
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsInteractor
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
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

class DiscoverShowsPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val featuredShowsRepository = FakeFeaturedShowsRepository()
    private val trendingShowsRepository = FakeTrendingShowsRepository()
    private val upcomingShowsRepository = FakeUpcomingShowsRepository()
    private val popularShowsRepository = FakePopularShowsRepository()
    private val topRatedShowsRepository = FakeTopRatedShowsRepository()
    private val genreRepository = FakeGenreRepository()
    private val startWatchingRepository = FakeStartWatchingRepository()
    private val upNextRepository = FakeUpNextRepository()
    private val accountManager = FakeAccountManager()
    private val fakeLocalizer = FakeLocalizer()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should derive loaded screen state from featured and catalog children`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            setCatalogData(showList())

            var state = awaitItem()
            while (state.isLoading) {
                state = awaitItem()
            }
            state.isLoading shouldBe false
            state.isEmpty shouldBe false
            state.isRefreshing shouldBe false
            state.showError shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should navigate to search when search icon is clicked`() = runTest {
        val testNavigator = TestNavigator()
        val presenter = buildPresenter(navigator = testNavigator)

        testNavigator.test {
            presenter.dispatch(SearchIconClicked)

            awaitNavigateTo(SearchRoute)
        }
    }

    @Test
    fun `should fan out refresh to children and converge to loaded state`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            presenter.dispatch(RefreshData)
            setCatalogData(showList())

            var state = awaitItem()
            while (state.isLoading || state.isEmpty) {
                state = awaitItem()
            }
            state.isEmpty shouldBe false
            state.isLoading shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun setCatalogData(list: List<ShowEntity>) {
        genreRepository.setGenreResult(emptyList())
        featuredShowsRepository.setFeaturedShows(list)
        trendingShowsRepository.setTrendingShows(list)
        upcomingShowsRepository.setUpcomingShows(list)
        popularShowsRepository.setPopularShows(list)
        topRatedShowsRepository.setTopRatedShows(list)
    }

    private fun showList() = List(3) {
        ShowEntity(showId = 84958L, tmdbId = 84958L, title = "Loki", posterPath = "/loki.jpg", inLibrary = false)
    }.toImmutableList()

    private fun buildPresenter(
        lifecycle: LifecycleRegistry = LifecycleRegistry(),
        navigator: Navigator = NoOpNavigator(),
    ): DiscoverShowsPresenter = DiscoverShowsPresenter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        featuredGraphFactory = featuredFactory(navigator),
        catalogGraphFactory = catalogFactory(navigator),
        upNextGraphFactory = upNextFactory(navigator),
        startWatchingGraphFactory = startWatchingFactory(navigator),
        navigator = navigator,
    ).also { lifecycle.resume() }

    private fun featuredFactory(navigator: Navigator) = object : DiscoverFeaturedChildGraph.Factory {
        override fun createDiscoverFeaturedGraph(componentContext: ComponentContext): DiscoverFeaturedChildGraph =
            object : DiscoverFeaturedChildGraph {
                override val discoverFeaturedPresenter = DiscoverFeaturedPresenter(
                    componentContext = componentContext,
                    navigator = navigator,
                    observeFeaturedShowsInteractor = ObserveFeaturedShowsInteractor(featuredShowsRepository),
                    featuredShowsInteractor = FeaturedShowsInteractor(featuredShowsRepository, dispatchers),
                    accountManager = accountManager,
                    errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
                    logger = FakeLogger(),
                )
            }
    }

    private fun catalogFactory(navigator: Navigator) = object : DiscoverCatalogChildGraph.Factory {
        override fun createDiscoverCatalogGraph(componentContext: ComponentContext): DiscoverCatalogChildGraph =
            object : DiscoverCatalogChildGraph {
                override val discoverCatalogPresenter = DiscoverCatalogPresenter(
                    componentContext = componentContext,
                    navigator = navigator,
                    observeTrendingShowsInteractor = ObserveTrendingShowsInteractor(trendingShowsRepository),
                    observeUpcomingShowsInteractor = ObserveUpcomingShowsInteractor(upcomingShowsRepository),
                    observePopularShowsInteractor = ObservePopularShowsInteractor(popularShowsRepository),
                    observeTopRatedShowsInteractor = ObserveTopRatedShowsInteractor(topRatedShowsRepository),
                    trendingShowsInteractor = TrendingShowsInteractor(trendingShowsRepository, dispatchers),
                    upcomingShowsInteractor = UpcomingShowsInteractor(upcomingShowsRepository, dispatchers),
                    popularShowsInteractor = PopularShowsInteractor(popularShowsRepository, dispatchers),
                    topRatedShowsInteractor = TopRatedShowsInteractor(topRatedShowsRepository, dispatchers),
                    genreShowsInteractor = GenreShowsInteractor(genreRepository, dispatchers),
                    accountManager = accountManager,
                    localizer = fakeLocalizer,
                    errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
                    logger = FakeLogger(),
                )
            }
    }

    private fun upNextFactory(navigator: Navigator) = object : DiscoverUpNextChildGraph.Factory {
        override fun createDiscoverUpNextGraph(componentContext: ComponentContext): DiscoverUpNextChildGraph =
            object : DiscoverUpNextChildGraph {
                override val discoverUpNextPresenter = DiscoverUpNextPresenter(
                    componentContext = componentContext,
                    navigator = navigator,
                    observeUpNextInteractor = ObserveUpNextInteractor(upNextRepository),
                )
            }
    }

    private fun startWatchingFactory(navigator: Navigator) = object : DiscoverStartWatchingChildGraph.Factory {
        override fun createDiscoverStartWatchingGraph(componentContext: ComponentContext): DiscoverStartWatchingChildGraph =
            object : DiscoverStartWatchingChildGraph {
                override val discoverStartWatchingPresenter = DiscoverStartWatchingPresenter(
                    componentContext = componentContext,
                    navigator = navigator,
                    observeStartWatchingInteractor = ObserveStartWatchingInteractor(startWatchingRepository),
                    localizer = fakeLocalizer,
                )
            }
    }
}
