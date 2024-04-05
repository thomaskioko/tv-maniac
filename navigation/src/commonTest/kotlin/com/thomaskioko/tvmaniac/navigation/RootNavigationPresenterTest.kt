package com.thomaskioko.tvmaniac.navigation

import app.cash.turbine.test
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
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
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

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
    presenter =
      RootNavigationPresenter(
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
  fun when_create_THEN_DiscoverChild_active() = runTest {
    presenter.screenStackFlow.test {
      val discoverScreen = awaitItem().active.instance

      discoverScreen.shouldBeInstanceOf<Discover>()

      presenter.shouldShowBottomNav(discoverScreen) shouldBe true
    }
  }

  @Test
  fun when_bringToFrontSearch_THEN_DiscoverChild_active() = runTest {
    presenter.screenStackFlow.test {
      presenter.bringToFront(Config.Search)

      val searchScreen = awaitItem().active.instance

      awaitItem().active.instance.shouldBeInstanceOf<Search>()
      presenter.shouldShowBottomNav(searchScreen) shouldBe true
    }
  }

  @Test
  fun when_bringToFrontLibrary_THEN_LibraryChild_active() = runTest {
    presenter.screenStackFlow.test {
      presenter.bringToFront(Config.Library)
      val library = awaitItem().active.instance

      awaitItem().active.instance.shouldBeInstanceOf<Library>()
      presenter.shouldShowBottomNav(library) shouldBe true
    }
  }

  @Test
  fun when_bringToFrontSettings_THEN_SettingsChild_active() = runTest {
    presenter.screenStackFlow.test {
      val settings = awaitItem().active.instance
      presenter.bringToFront(Config.Settings)

      awaitItem().active.instance.shouldBeInstanceOf<Settings>()
      presenter.shouldShowBottomNav(settings) shouldBe true
    }
  }

  @Test
  fun when_bringToFrontMoreShows_THEN_MoreShowsChild_active() = runTest {
    presenter.screenStackFlow.test {
      awaitItem().active.instance.shouldBeInstanceOf<Discover>()

      presenter.bringToFront(Config.MoreShows(1))

      val moreScreen = awaitItem().active.instance

      moreScreen.shouldBeInstanceOf<MoreShows>()
      presenter.shouldShowBottomNav(moreScreen) shouldBe false
    }
  }

  @Test
  fun when_create_THEN_DefaultTheme_is_Returned() = runTest {
    presenter.themeState.value shouldBe ThemeState()
  }

  @Test
  fun when_themeIsUpdated_THEN_correctState_is_Returned() = runTest {
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
  ): LibraryPresenterFactory = { _: ComponentContext, _: (showDetails: Long) -> Unit ->
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
  ): SearchPresenterFactory = { _: ComponentContext, _: () -> Unit ->
    SearchPresenter(
      componentContext = componentContext,
      goBack = {},
    )
  }

  private fun buildSettingsPresenterFactory(
    componentContext: ComponentContext,
  ): SettingsPresenterFactory = { _: ComponentContext, _: () -> Unit ->
    SettingsPresenter(
      componentContext = componentContext,
      launchWebView = {},
      datastoreRepository = datastoreRepository,
      traktAuthRepository = FakeTraktAuthRepository(),
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
  ): TrailersPresenterFactory = { _: ComponentContext, id: Long ->
    TrailersPresenter(
      componentContext = componentContext,
      traktShowId = id,
      repository = FakeTrailerRepository(),
    )
  }
}
