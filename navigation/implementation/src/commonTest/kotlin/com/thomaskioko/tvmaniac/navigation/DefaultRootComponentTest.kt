package com.thomaskioko.tvmaniac.navigation

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomakioko.tvmaniac.util.testing.FakeFormatterUtil
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.cast.testing.FakeCastRepository
import com.thomaskioko.tvmaniac.data.recommendedshows.testing.FakeRecommendedShowsRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.discover.presenter.di.FakeDiscoverPresenterFactory
import com.thomaskioko.tvmaniac.domain.recommendedshows.RecommendedShowsInteractor
import com.thomaskioko.tvmaniac.domain.seasondetails.ObservableSeasonDetailsInteractor
import com.thomaskioko.tvmaniac.domain.seasondetails.SeasonDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObservableShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor
import com.thomaskioko.tvmaniac.domain.watchproviders.WatchProvidersInteractor
import com.thomaskioko.tvmaniac.genre.FakeGenreRepository
import com.thomaskioko.tvmaniac.navigation.RootPresenter.Child.Home
import com.thomaskioko.tvmaniac.navigation.RootPresenter.Child.MoreShows
import com.thomaskioko.tvmaniac.navigation.RootPresenter.Child.ShowDetails
import com.thomaskioko.tvmaniac.presentation.home.DefaultHomePresenter
import com.thomaskioko.tvmaniac.presentation.home.HomePresenter
import com.thomaskioko.tvmaniac.presentation.moreshows.FakeMoreShowsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.search.Mapper
import com.thomaskioko.tvmaniac.presentation.search.SearchPresenterFactory
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersPresenterFactory
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistPresenterFactory
import com.thomaskioko.tvmaniac.search.testing.FakeSearchRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.watchlist.testing.FakeWatchlistRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DefaultRootComponentTest {
    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val traktAuthManager = FakeTraktAuthManager()
    private val datastoreRepository = FakeDatastoreRepository()
    private val searchRepository = FakeSearchRepository()
    private val genreRepository = FakeGenreRepository()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var presenter: DefaultRootPresenter
    private lateinit var navigator: FakeRootNavigator

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        lifecycle.resume()

        val componentContext = DefaultComponentContext(lifecycle = lifecycle)
        navigator = FakeRootNavigator()
        presenter = DefaultRootPresenter(
            componentContext = componentContext,
            navigator = navigator,
            moreShowsPresenterFactory = FakeMoreShowsPresenterFactory(),
            showDetailsPresenterFactory = buildShowDetailsPresenterPresenterFactory(),
            seasonDetailsPresenterFactory = buildSeasonDetailsPresenterFactory(),
            trailersPresenterFactory = buildTrailersPresenterFactory(),
            homePresenterFactory = buildHomePresenterFactory(),
            datastoreRepository = datastoreRepository,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Home`() = runTest {
        presenter.childStack.test { awaitItem().active.instance.shouldBeInstanceOf<Home>() }
    }

    @Test
    fun `should return Home as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<Home>()

            navigator.bringToFront(RootDestinationConfig.ShowDetails(1))

            val moreScreen = awaitItem().active.instance

            moreScreen.shouldBeInstanceOf<ShowDetails>()

            navigator.pop()

            awaitItem().active.instance.shouldBeInstanceOf<Home>()
        }
    }

    @Test
    fun `should return ShowDetails as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<Home>()

            navigator.bringToFront(RootDestinationConfig.ShowDetails(1))

            val moreScreen = awaitItem().active.instance

            moreScreen.shouldBeInstanceOf<ShowDetails>()
        }
    }

    @Test
    fun `should return MoreShows as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<Home>()

            navigator.bringToFront(RootDestinationConfig.MoreShows(1))

            val moreScreen = awaitItem().active.instance

            moreScreen.shouldBeInstanceOf<MoreShows>()
        }
    }

    @Test
    fun `should return initial theme state`() = runTest {
        presenter.themeState.value shouldBe ThemeState()
    }

    @Test
    fun `should update theme to Dark when DarkTheme is set`() = runTest {
        presenter.themeState.test {
            awaitItem() shouldBe ThemeState()

            datastoreRepository.setTheme(AppTheme.DARK_THEME)

            awaitItem() shouldBe
                ThemeState(
                    isFetching = false,
                    appTheme = AppTheme.DARK_THEME,
                )
        }
    }

    private fun buildLibraryPresenterFactory(): WatchlistPresenterFactory = WatchlistPresenterFactory(
        repository = FakeWatchlistRepository(),
    )

    private fun buildSearchPresenterFactory(): SearchPresenterFactory = SearchPresenterFactory(
        genreRepository = genreRepository,
        searchRepository = searchRepository,
        mapper = Mapper(
            formatterUtil = FakeFormatterUtil(),
        ),
    )

    private fun buildSettingsPresenterFactory(): SettingsPresenterFactory = SettingsPresenterFactory(
        datastoreRepository = datastoreRepository,
        traktAuthRepository = FakeTraktAuthRepository(),
    )

    private fun buildShowDetailsPresenterPresenterFactory(): ShowDetailsPresenterFactory = ShowDetailsPresenterFactory(
        watchlistRepository = FakeWatchlistRepository(),
        recommendedShowsInteractor = RecommendedShowsInteractor(
            recommendedShowsRepository = FakeRecommendedShowsRepository(),
            dispatchers = coroutineDispatcher,
        ),
        showDetailsInteractor = ShowDetailsInteractor(
            showDetailsRepository = FakeShowDetailsRepository(),
            dispatchers = coroutineDispatcher,
        ),
        watchProvidersInteractor = WatchProvidersInteractor(
            repository = FakeWatchProviderRepository(),
            dispatchers = coroutineDispatcher,
        ),
        similarShowsInteractor = SimilarShowsInteractor(
            similarShowsRepository = FakeSimilarShowsRepository(),
            dispatchers = coroutineDispatcher,
        ),
        observableShowDetailsInteractor = ObservableShowDetailsInteractor(
            castRepository = FakeCastRepository(),
            recommendedShowsRepository = FakeRecommendedShowsRepository(),
            seasonsRepository = FakeSeasonsRepository(),
            showDetailsRepository = FakeShowDetailsRepository(),
            similarShowsRepository = FakeSimilarShowsRepository(),
            trailerRepository = FakeTrailerRepository(),
            watchProviders = FakeWatchProviderRepository(),
            formatterUtil = FakeFormatterUtil(),
            dispatchers = coroutineDispatcher,
        ),
        logger = FakeLogger(),
    )

    private fun buildHomePresenterFactory(): HomePresenter.Factory =
        DefaultHomePresenter.Factory(
            traktAuthManager = traktAuthManager,
            searchPresenterFactory = buildSearchPresenterFactory(),
            settingsPresenterFactory = buildSettingsPresenterFactory(),
            discoverPresenterFactory = FakeDiscoverPresenterFactory(),
            watchlistPresenterFactory = buildLibraryPresenterFactory(),
        )

    private fun buildSeasonDetailsPresenterFactory(): SeasonDetailsPresenterFactory = SeasonDetailsPresenterFactory(
        observableSeasonDetailsInteractor = ObservableSeasonDetailsInteractor(
            seasonDetailsRepository = FakeSeasonDetailsRepository(),
            castRepository = FakeCastRepository(),
        ),
        seasonDetailsInteractor = SeasonDetailsInteractor(
            seasonDetailsRepository = FakeSeasonDetailsRepository(),
            dispatchers = coroutineDispatcher,
        ),
        logger = FakeLogger(),
    )

    private fun buildTrailersPresenterFactory(): TrailersPresenterFactory = TrailersPresenterFactory(
        repository = FakeTrailerRepository(),
    )
}
