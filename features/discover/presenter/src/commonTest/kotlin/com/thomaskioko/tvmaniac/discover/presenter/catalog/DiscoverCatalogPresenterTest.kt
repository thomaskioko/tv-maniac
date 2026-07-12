package com.thomaskioko.tvmaniac.discover.presenter.catalog

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsInteractor
import com.thomaskioko.tvmaniac.data.popularshows.testing.FakePopularShowsRepository
import com.thomaskioko.tvmaniac.data.topratedshows.testing.FakeTopRatedShowsRepository
import com.thomaskioko.tvmaniac.data.trendingshows.testing.FakeTrendingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsInteractor
import com.thomaskioko.tvmaniac.data.upcomingshows.testing.FakeUpcomingShowsRepository
import com.thomaskioko.tvmaniac.datastore.api.DiscoverSection
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsInteractor
import com.thomaskioko.tvmaniac.domain.discover.ObservePopularShowsInteractor
import com.thomaskioko.tvmaniac.domain.discover.ObserveTopRatedShowsInteractor
import com.thomaskioko.tvmaniac.domain.discover.ObserveTrendingShowsInteractor
import com.thomaskioko.tvmaniac.domain.discover.ObserveUpcomingShowsInteractor
import com.thomaskioko.tvmaniac.domain.genre.GenreShowsInteractor
import com.thomaskioko.tvmaniac.genre.FakeGenreRepository
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
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsInteractor
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

class DiscoverCatalogPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val trendingShowsRepository = FakeTrendingShowsRepository()
    private val upcomingShowsRepository = FakeUpcomingShowsRepository()
    private val popularShowsRepository = FakePopularShowsRepository()
    private val topRatedShowsRepository = FakeTopRatedShowsRepository()
    private val genreRepository = FakeGenreRepository()
    private val accountManager = FakeAccountManager()
    private val datastoreRepository = FakeDatastoreRepository()
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
    fun `should emit rail shows and titles when repositories have data`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            genreRepository.setGenreResult(emptyList())
            trendingShowsRepository.setTrendingShows(showList())
            upcomingShowsRepository.setUpcomingShows(showList())
            popularShowsRepository.setPopularShows(showList())
            topRatedShowsRepository.setTopRatedShows(showList())

            var state = awaitItem()
            while (state.trendingShows.isEmpty() || state.topRatedShows.isEmpty()) {
                state = awaitItem()
            }

            state.trendingShows shouldBe expectedShows()
            state.upcomingShows shouldBe expectedShows()
            state.popularShows shouldBe expectedShows()
            state.topRatedShows shouldBe expectedShows()
            state.trendingTitle shouldBe fakeLocalizer.getString(StringResourceKey.LabelDiscoverTrendingToday)
            state.upcomingTitle shouldBe fakeLocalizer.getString(StringResourceKey.LabelDiscoverUpcoming)
            state.popularTitle shouldBe fakeLocalizer.getString(StringResourceKey.LabelDiscoverPopular)
            state.topRatedTitle shouldBe fakeLocalizer.getString(StringResourceKey.LabelDiscoverTopRated)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should report section not visible given it is hidden while keeping its data`() = runTest {
        datastoreRepository.saveHiddenDiscoverSections(setOf(DiscoverSection.POPULAR))
        val presenter = buildPresenter()

        presenter.state.test {
            genreRepository.setGenreResult(emptyList())
            trendingShowsRepository.setTrendingShows(showList())
            upcomingShowsRepository.setUpcomingShows(showList())
            popularShowsRepository.setPopularShows(showList())
            topRatedShowsRepository.setTopRatedShows(showList())

            var state = awaitItem()
            while (state.popularShows.isEmpty()) {
                state = awaitItem()
            }

            state.popularShows shouldBe expectedShows()
            state.popularVisible shouldBe false
            state.trendingVisible shouldBe true
            state.upcomingVisible shouldBe true
            state.topRatedVisible shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should navigate to show details when catalog show is clicked`() = runTest {
        val testNavigator = TestNavigator()
        val presenter = buildPresenter(navigator = testNavigator)

        testNavigator.test {
            presenter.dispatch(CatalogShowClicked(showId = 84958L))

            awaitNavigateTo(ShowDetailsRoute(ShowDetailsParam(showId = 84958L)))
        }
    }

    @Test
    fun `should navigate to more shows when a rail more is clicked`() = runTest {
        val testNavigator = TestNavigator()
        val presenter = buildPresenter(navigator = testNavigator)

        testNavigator.test {
            presenter.dispatch(TrendingMoreClicked)
            awaitNavigateTo(MoreShowsRoute(Category.TRENDING_TODAY.id))

            presenter.dispatch(UpcomingMoreClicked)
            awaitNavigateTo(MoreShowsRoute(Category.UPCOMING.id))

            presenter.dispatch(PopularMoreClicked)
            awaitNavigateTo(MoreShowsRoute(Category.POPULAR.id))

            presenter.dispatch(TopRatedMoreClicked)
            awaitNavigateTo(MoreShowsRoute(Category.TOP_RATED.id))
        }
    }

    private fun buildPresenter(
        lifecycle: LifecycleRegistry = LifecycleRegistry(),
        navigator: Navigator = NoOpNavigator(),
    ): DiscoverCatalogPresenter = DiscoverCatalogPresenter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
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
        datastoreRepository = datastoreRepository,
        localizer = fakeLocalizer,
        errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
        logger = FakeLogger(),
    ).also { lifecycle.resume() }

    private fun showList() = List(3) {
        ShowEntity(showId = 84958L, tmdbId = 84958L, title = "Loki", posterPath = "/loki.jpg", inLibrary = false)
    }.toImmutableList()

    private fun expectedShows() = showList().map {
        com.thomaskioko.tvmaniac.discover.presenter.model.DiscoverShow(
            showId = it.showId,
            tmdbId = it.tmdbId,
            title = it.title,
            posterImageUrl = it.posterPath,
            inLibrary = it.inLibrary,
            overView = it.overview,
        )
    }.toImmutableList()
}
