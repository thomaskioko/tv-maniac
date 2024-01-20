package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.data.cast.testing.FakeCastRepository
import com.thomaskioko.tvmaniac.data.featuredshows.testing.FakeFeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.popularshows.testing.FakePopularShowsRepository
import com.thomaskioko.tvmaniac.data.recommendedshows.testing.FakeRecommendedShowsRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.topratedshows.testing.FakeTopRatedShowsRepository
import com.thomaskioko.tvmaniac.data.trendingshows.testing.FakeTrendingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.testing.FakeUpcomingShowsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.navigation.RootNavigationPresenter.Config
import com.thomaskioko.tvmaniac.navigation.Screen.Discover
import com.thomaskioko.tvmaniac.navigation.Screen.Library
import com.thomaskioko.tvmaniac.navigation.Screen.MoreShows
import com.thomaskioko.tvmaniac.navigation.Screen.Search
import com.thomaskioko.tvmaniac.navigation.Screen.Settings
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsPresenter
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.search.SearchPresenter
import com.thomaskioko.tvmaniac.presentation.search.SearchPresenterFactory
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenter
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsPresenterPresenterFactory
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersPresenter
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersPresenterFactory
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryPresenter
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryPresenterFactory
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.similar.testing.FakeSimilarShowsRepository
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.watchlist.testing.FakeLibraryRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@Suppress("TestFunctionName")
@OptIn(ExperimentalCoroutinesApi::class)
class RootNavigationPresenterTest {
    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val traktAuthManager = FakeTraktAuthManager()
    private val datastoreRepository = FakeDatastoreRepository()
    private val featuredShowsRepository = FakeFeaturedShowsRepository()

    private lateinit var presenter: RootNavigationPresenter

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        lifecycle.resume()

        val componentContext = DefaultComponentContext(lifecycle = lifecycle)
        presenter = RootNavigationPresenter(
            componentContext = componentContext,
            discoverPresenterFactory = buildDiscoverPresenterFactory(componentContext),
            libraryPresenterFactory = buildLibraryPresenterFactory(componentContext),
            moreShowsPresenterFactory = buildMoreShowsPresenterFactory(componentContext),
            searchPresenterFactory = buildSearchPresenterFactory(componentContext),
            settingsPresenterFactory = buildSettingsPresenterFactory(componentContext),
            showDetailsPresenterFactory = buildShowDetailsPresenterPresenterFactory(componentContext),
            seasonDetailsPresenterFactory = buildSeasonDetailsPresenterFactory(componentContext),
            trailersPresenterFactory = buildTrailersPresenterFactory(componentContext),
            traktAuthManager = traktAuthManager,
            datastoreRepository = datastoreRepository,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun WHEN_create_THEN_DiscoverChild_active() {
        val discoverScreen = presenter.screenStack.active.instance
        discoverScreen.shouldBeInstanceOf<Discover>()

        presenter.shouldShowBottomNav(discoverScreen) shouldBe true
    }

    @Test
    fun WHEN_bringToFrontSearch_THEN_DiscoverChild_active() {
        presenter.bringToFront(Config.Search)
        val searchScreen = presenter.screenStack.active.instance

        searchScreen.shouldBeInstanceOf<Search>()
        presenter.shouldShowBottomNav(searchScreen) shouldBe true
    }

    @Test
    fun WHEN_bringToFrontLibrary_THEN_LibraryChild_active() {
        presenter.bringToFront(Config.Library)
        val library = presenter.screenStack.active.instance

        library.shouldBeInstanceOf<Library>()
        presenter.shouldShowBottomNav(library) shouldBe true
    }

    @Test
    fun WHEN_bringToFrontSettings_THEN_SettingsChild_active() {
        val settings = presenter.screenStack.active.instance
        presenter.bringToFront(Config.Settings)

        presenter.screenStack.active.instance.shouldBeInstanceOf<Settings>()
        presenter.shouldShowBottomNav(settings) shouldBe true
    }

    @Test
    fun WHEN_bringToFrontMoreShows_THEN_MoreShowsChild_active() {
        presenter.bringToFront(Config.MoreShows(1))
        val moreScreen = presenter.screenStack.active.instance

        presenter.screenStack.active.instance.shouldBeInstanceOf<MoreShows>()
        presenter.shouldShowBottomNav(moreScreen) shouldBe false
    }

    @Test
    fun WHEN_create_THEN_DefaultTheme_is_Returned() = runTest {
        presenter.state.value shouldBe ThemeState()
    }

    @Test
    fun WHEN_themeIsUpdated_THEN_correctState_is_Returned() = runTest {
        presenter.state.value shouldBe ThemeState()
        datastoreRepository.setTheme(AppTheme.DARK_THEME)

        advanceUntilIdle()
        presenter.state.value shouldBe ThemeState(
            isFetching = false,
            appTheme = AppTheme.DARK_THEME
        )
    }

    private fun buildDiscoverPresenterFactory(
        componentContext: ComponentContext,
    ): DiscoverShowsPresenterFactory =
        { _: ComponentContext, _: (id: Long) -> Unit, _: (categoryId: Long) -> Unit ->
            DiscoverShowsPresenter(
                componentContext = componentContext,
                onNavigateToShowDetails = {},
                onNavigateToMore = {},
                featuredShowsRepository = featuredShowsRepository,
                trendingShowsRepository = FakeTrendingShowsRepository(),
                upcomingShowsRepository = FakeUpcomingShowsRepository(),
                topRatedShowsRepository = FakeTopRatedShowsRepository(),
                popularShowsRepository = FakePopularShowsRepository(),
            )
        }

    private fun buildLibraryPresenterFactory(
        componentContext: ComponentContext,
    ): LibraryPresenterFactory =
        { _: ComponentContext, _: (showDetails: Long) -> Unit ->
            LibraryPresenter(
                componentContext = componentContext,
                navigateToShowDetails = {},
                repository = FakeLibraryRepository(),
            )
        }

    private fun buildMoreShowsPresenterFactory(
        componentContext: ComponentContext,
    ): MoreShowsPresenterFactory =
        { _: ComponentContext, _: Long, _: () -> Unit, _: (id: Long) -> Unit ->
            MoreShowsPresenter(
                componentContext = componentContext,
                categoryId = 0,
                onBack = {},
                onNavigateToShowDetails = {},
                popularShowsRepository = FakePopularShowsRepository(),
                upcomingShowsRepository = FakeUpcomingShowsRepository(),
                trendingShowsRepository = FakeTrendingShowsRepository(),
                topRatedShowsRepository = FakeTopRatedShowsRepository(),
            )

        }

    private fun buildSearchPresenterFactory(
        componentContext: ComponentContext,
    ): SearchPresenterFactory =
        { _: ComponentContext, _: () -> Unit ->
            SearchPresenter(
                componentContext = componentContext,
                goBack = {},
            )
        }

    private fun buildSettingsPresenterFactory(
        componentContext: ComponentContext,
    ): SettingsPresenterFactory =
        { _: ComponentContext, _: () -> Unit ->
            SettingsPresenter(
                componentContext = componentContext,
                launchWebView = {},
                datastoreRepository = datastoreRepository,
                traktAuthRepository = FakeTraktAuthRepository()
            )
        }

    private fun buildShowDetailsPresenterPresenterFactory(
        componentContext: ComponentContext,
    ): ShowDetailsPresenterPresenterFactory =
        {
                _: ComponentContext,
                showId: Long,
                _: () -> Unit,
                _: (id: Long) -> Unit,
                _: (param: ShowSeasonDetailsParam) -> Unit,
                _: (id: Long) -> Unit,
            ->
            ShowDetailsPresenter(
                showId = showId,
                onBack = {},
                onNavigateToShow = {},
                onNavigateToSeason = {},
                onNavigateToTrailer = {},
                componentContext = componentContext,
                castRepository = FakeCastRepository(),
                libraryRepository = FakeLibraryRepository(),
                recommendedShowsRepository = FakeRecommendedShowsRepository(),
                seasonsRepository = FakeSeasonsRepository(),
                showDetailsRepository = FakeShowDetailsRepository(),
                similarShowsRepository = FakeSimilarShowsRepository(),
                trailerRepository = FakeTrailerRepository(),
                watchProviders = FakeWatchProviderRepository(),
            )
        }

    private fun buildSeasonDetailsPresenterFactory(
        componentContext: ComponentContext,
    ): SeasonDetailsPresenterFactory =
        { _: ComponentContext, param: SeasonDetailsUiParam, _: () -> Unit, _: (id: Long) -> Unit ->
            SeasonDetailsPresenter(
                componentContext = componentContext,
                param = param,
                onBack = {},
                onEpisodeClick = {},
                seasonDetailsRepository = FakeSeasonDetailsRepository(),
                castRepository = FakeCastRepository(),
            )
        }

    private fun buildTrailersPresenterFactory(
        componentContext: ComponentContext,
    ): TrailersPresenterFactory =
        { _: ComponentContext, id: Long ->
            TrailersPresenter(
                componentContext = componentContext,
                traktShowId = id,
                repository = FakeTrailerRepository(),
            )
        }
}
