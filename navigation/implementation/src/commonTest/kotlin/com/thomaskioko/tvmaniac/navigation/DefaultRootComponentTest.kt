package com.thomaskioko.tvmaniac.navigation

import app.cash.turbine.test
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomakioko.tvmaniac.util.testing.FakeFormatterUtil
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
import com.thomaskioko.tvmaniac.genre.FakeGenreRepository
import com.thomaskioko.tvmaniac.navigation.RootPresenter.Child.Home
import com.thomaskioko.tvmaniac.navigation.RootPresenter.Child.MoreShows
import com.thomaskioko.tvmaniac.navigation.RootPresenter.Child.ShowDetails
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverPresenterFactory
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presentation.home.DefaultHomePresenter
import com.thomaskioko.tvmaniac.presentation.home.HomePresenter
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsPresenter
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.search.SearchPresenterFactory
import com.thomaskioko.tvmaniac.presentation.search.SearchShowsPresenter
import com.thomaskioko.tvmaniac.presentation.search.ShowMapper
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenter
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersPresenter
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersPresenterFactory
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryPresenter
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryPresenterFactory
import com.thomaskioko.tvmaniac.search.testing.FakeSearchRepository
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
  private val featuredShowsRepository = FakeFeaturedShowsRepository()
  private val trendingShowsRepository = FakeTrendingShowsRepository()
  private val upcomingShowsRepository = FakeUpcomingShowsRepository()
  private val topRatedShowsRepository = FakeTopRatedShowsRepository()
  private val popularShowsRepository = FakePopularShowsRepository()
  private val searchRepository = FakeSearchRepository()
  private val genreRepository = FakeGenreRepository()

  private lateinit var presenter: DefaultRootPresenter

  @BeforeTest
  fun before() {
    Dispatchers.setMain(testDispatcher)
    lifecycle.resume()

    val componentContext = DefaultComponentContext(lifecycle = lifecycle)
    presenter =
      DefaultRootPresenter(
        componentContext = componentContext,
        moreShowsPresenterFactory = buildMoreShowsPresenterFactory(componentContext),
        showDetailsPresenterFactory = buildShowDetailsPresenterPresenterFactory(componentContext),
        seasonDetailsPresenterFactory = buildSeasonDetailsPresenterFactory(componentContext),
        trailersPresenterFactory = buildTrailersPresenterFactory(componentContext),
        homePresenterFactory = buildHomePresenterFactory(componentContext),
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

      presenter.bringToFront(RootDestinationConfig.ShowDetails(1))

      val moreScreen = awaitItem().active.instance

      moreScreen.shouldBeInstanceOf<ShowDetails>()

      presenter.onBackClicked()

      awaitItem().active.instance.shouldBeInstanceOf<Home>()
    }
  }

  @Test
  fun `should return ShowDetails as active instance`() = runTest {
    presenter.childStack.test {
      awaitItem().active.instance.shouldBeInstanceOf<Home>()

      presenter.bringToFront(RootDestinationConfig.ShowDetails(1))

      val moreScreen = awaitItem().active.instance

      moreScreen.shouldBeInstanceOf<ShowDetails>()
    }
  }

  @Test
  fun `should return MoreShows as active instance`() = runTest {
    presenter.childStack.test {
      awaitItem().active.instance.shouldBeInstanceOf<Home>()

      presenter.bringToFront(RootDestinationConfig.MoreShows(1))

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

  private fun buildDiscoverPresenterFactory(
    componentContext: ComponentContext,
  ): DiscoverPresenterFactory =
    DiscoverPresenterFactory(
      create = { _: ComponentContext, _: (id: Long) -> Unit, _: (categoryId: Long) -> Unit ->
        DiscoverShowsPresenter(
          componentContext = componentContext,
          onNavigateToShowDetails = {},
          onNavigateToMore = {},
          featuredShowsRepository = featuredShowsRepository,
          trendingShowsRepository = trendingShowsRepository,
          upcomingShowsRepository = upcomingShowsRepository,
          topRatedShowsRepository = topRatedShowsRepository,
          popularShowsRepository = popularShowsRepository,
          libraryRepository = FakeLibraryRepository(),
        )
      },
    )

  private fun buildLibraryPresenterFactory(
    componentContext: ComponentContext,
  ): LibraryPresenterFactory = LibraryPresenterFactory(
    create = { _: ComponentContext, _: (showDetails: Long) -> Unit ->
      LibraryPresenter(
        componentContext = componentContext,
        navigateToShowDetails = {},
        repository = FakeLibraryRepository(),
      )
    },
  )

  private fun buildMoreShowsPresenterFactory(
    componentContext: ComponentContext,
  ): MoreShowsPresenterFactory = MoreShowsPresenterFactory(
    create = { _: ComponentContext, _: Long, _: () -> Unit, _: (id: Long) -> Unit ->
      MoreShowsPresenter(
        componentContext = componentContext,
        categoryId = 0,
        onBack = {},
        onNavigateToShowDetails = {},
        popularShowsRepository = popularShowsRepository,
        upcomingShowsRepository = upcomingShowsRepository,
        trendingShowsRepository = trendingShowsRepository,
        topRatedShowsRepository = topRatedShowsRepository,
      )
    },
  )

  private fun buildSearchPresenterFactory(
    componentContext: ComponentContext,
  ): SearchPresenterFactory = SearchPresenterFactory(
    create = { _: ComponentContext, _: (id: Long) -> Unit, _: (id: Long) -> Unit ->
      SearchShowsPresenter(
        componentContext = componentContext,
        onNavigateToShowDetails = {},
        onNavigateToGenre = {},
        genreRepository = genreRepository,
        searchRepository = searchRepository,
        mapper = ShowMapper(
          formatterUtil = FakeFormatterUtil(),
        ),
      )
    },
  )

  private fun buildSettingsPresenterFactory(
    componentContext: ComponentContext,
  ): SettingsPresenterFactory = SettingsPresenterFactory(
    create = { _: ComponentContext, _: () -> Unit ->
      SettingsPresenter(
        componentContext = componentContext,
        launchWebView = {},
        datastoreRepository = datastoreRepository,
        traktAuthRepository = FakeTraktAuthRepository(),
      )
    },
  )

  private fun buildShowDetailsPresenterPresenterFactory(
    componentContext: ComponentContext,
  ): ShowDetailsPresenterFactory =
    ShowDetailsPresenterFactory(
      create = {
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
      },
    )

  private fun buildHomePresenterFactory(componentContext: ComponentContext): HomePresenter.Factory =
    DefaultHomePresenter.Factory(
      traktAuthManager = traktAuthManager,
      searchPresenterFactory = buildSearchPresenterFactory(componentContext),
      settingsPresenterFactory = buildSettingsPresenterFactory(componentContext),
      discoverPresenterFactory = buildDiscoverPresenterFactory(componentContext),
      libraryPresenterFactory = buildLibraryPresenterFactory(componentContext),
    )


  private fun buildSeasonDetailsPresenterFactory(
    componentContext: ComponentContext,
  ): SeasonDetailsPresenterFactory =
    SeasonDetailsPresenterFactory(
      create = { _: ComponentContext, param: SeasonDetailsUiParam, _: () -> Unit, _: (id: Long) -> Unit ->
        SeasonDetailsPresenter(
          componentContext = componentContext,
          param = param,
          onBack = {},
          onEpisodeClick = {},
          seasonDetailsRepository = FakeSeasonDetailsRepository(),
          castRepository = FakeCastRepository(),
        )
      },
    )

  private fun buildTrailersPresenterFactory(
    componentContext: ComponentContext,
  ): TrailersPresenterFactory = TrailersPresenterFactory(
    create = { _: ComponentContext, id: Long ->
      TrailersPresenter(
        componentContext = componentContext,
        traktShowId = id,
        repository = FakeTrailerRepository(),
      )
    },
  )
}
