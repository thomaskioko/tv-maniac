package com.thomaskioko.tvmaniac.presentation.home

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomakioko.tvmaniac.util.testing.FakeFormatterUtil
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.featuredshows.api.interactor.FeaturedShowsInteractor
import com.thomaskioko.tvmaniac.data.featuredshows.testing.FakeFeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsInteractor
import com.thomaskioko.tvmaniac.data.popularshows.testing.FakePopularShowsRepository
import com.thomaskioko.tvmaniac.data.topratedshows.testing.FakeTopRatedShowsRepository
import com.thomaskioko.tvmaniac.data.trendingshows.testing.FakeTrendingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsInteractor
import com.thomaskioko.tvmaniac.data.upcomingshows.testing.FakeUpcomingShowsRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsInteractor
import com.thomaskioko.tvmaniac.domain.discover.DiscoverShowsInteractor
import com.thomaskioko.tvmaniac.genre.FakeGenreRepository
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverPresenterFactory
import com.thomaskioko.tvmaniac.presentation.search.Mapper
import com.thomaskioko.tvmaniac.presentation.search.SearchPresenterFactory
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistPresenterFactory
import com.thomaskioko.tvmaniac.search.testing.FakeSearchRepository
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsInteractor
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test

class HomePresenterTest {
    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val traktAuthManager = FakeTraktAuthManager()
    private val datastoreRepository = FakeDatastoreRepository()
    private val featuredShowsRepository = FakeFeaturedShowsRepository()
    private val trendingShowsRepository = FakeTrendingShowsRepository()
    private val upcomingShowsRepository = FakeUpcomingShowsRepository()
    private val topRatedShowsRepository = FakeTopRatedShowsRepository()
    private val popularShowsRepository = FakePopularShowsRepository()
    private val searchRepository = FakeSearchRepository()
    private val genreRepository = FakeGenreRepository()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var presenter: HomePresenter

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        lifecycle.resume()

        presenter = buildHomePresenterFactory().create(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            onShowClicked = {},
            onMoreShowClicked = {},
            onShowGenreClicked = {},
        )
    }

    @Test
    fun `initial state should be Discover`() = runTest {
        presenter.homeChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Discover>()
        }
    }

    @Test
    fun `should return Search as active instance when onSearchClicked`() = runTest {
        presenter.homeChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Discover>()
            presenter.onSearchClicked()

            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Search>()
        }
    }

    @Test
    fun `should return Library as active instance when onSettingsClicked`() = runTest {
        presenter.homeChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Discover>()
            presenter.onLibraryClicked()

            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Watchlist>()
        }
    }

    @Test
    fun `should return Settings as active instance when onSettingsClicked`() = runTest {
        presenter.homeChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Discover>()
            presenter.onSettingsClicked()

            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Settings>()
        }
    }

    private fun buildSearchPresenterFactory(): SearchPresenterFactory = SearchPresenterFactory(
        searchRepository = searchRepository,
        genreRepository = genreRepository,
        mapper = Mapper(
            formatterUtil = FakeFormatterUtil(),
        ),
    )

    private fun buildHomePresenterFactory(): HomePresenter.Factory =
        DefaultHomePresenter.Factory(
            discoverPresenterFactory = buildDiscoverPresenterFactory(),
            watchlistPresenterFactory = buildLibraryPresenterFactory(),
            searchPresenterFactory = buildSearchPresenterFactory(),
            settingsPresenterFactory = buildSettingsPresenterFactory(),
            traktAuthManager = traktAuthManager,
        )

    private fun buildSettingsPresenterFactory(): SettingsPresenterFactory = SettingsPresenterFactory(
        datastoreRepository = datastoreRepository,
        traktAuthRepository = FakeTraktAuthRepository(),
    )

    private fun buildDiscoverPresenterFactory(): DiscoverPresenterFactory = DiscoverPresenterFactory(
        discoverShowsInteractor = DiscoverShowsInteractor(
            featuredShowsRepository = featuredShowsRepository,
            topRatedShowsRepository = topRatedShowsRepository,
            popularShowsRepository = popularShowsRepository,
            trendingShowsRepository = trendingShowsRepository,
            upcomingShowsRepository = upcomingShowsRepository,
            genreRepository = genreRepository,
            dispatchers = coroutineDispatcher,
        ),
        watchlistRepository = FakeWatchlistRepository(),
        featuredShowsInteractor = FeaturedShowsInteractor(
            featuredShowsRepository = featuredShowsRepository,
            dispatchers = coroutineDispatcher,
        ),
        topRatedShowsInteractor = TopRatedShowsInteractor(
            topRatedShowsRepository = topRatedShowsRepository,
            dispatchers = coroutineDispatcher,
        ),
        popularShowsInteractor = PopularShowsInteractor(
            popularShowsRepository = popularShowsRepository,
            dispatchers = coroutineDispatcher,
        ),
        trendingShowsInteractor = TrendingShowsInteractor(
            trendingShowsRepository = trendingShowsRepository,
            dispatchers = coroutineDispatcher,
        ),
        upcomingShowsInteractor = UpcomingShowsInteractor(
            upcomingShowsRepository = upcomingShowsRepository,
            dispatchers = coroutineDispatcher,
        ),
        logger = FakeLogger(),
    )

    private fun buildLibraryPresenterFactory(): WatchlistPresenterFactory = WatchlistPresenterFactory(
        repository = FakeWatchlistRepository(),
    )
}
