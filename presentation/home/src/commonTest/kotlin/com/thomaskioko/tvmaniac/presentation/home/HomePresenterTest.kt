package com.thomaskioko.tvmaniac.presentation.home

import app.cash.turbine.test
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomakioko.tvmaniac.util.testing.FakeFormatterUtil
import com.thomaskioko.tvmaniac.data.featuredshows.testing.FakeFeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.popularshows.testing.FakePopularShowsRepository
import com.thomaskioko.tvmaniac.data.topratedshows.testing.FakeTopRatedShowsRepository
import com.thomaskioko.tvmaniac.data.trendingshows.testing.FakeTrendingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.testing.FakeUpcomingShowsRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.search.SearchShowsPresenter
import com.thomaskioko.tvmaniac.presentation.search.SearchPresenterFactory
import com.thomaskioko.tvmaniac.presentation.search.ShowMapper
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenter
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryPresenter
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryPresenterFactory
import com.thomaskioko.tvmaniac.search.testing.FakeSearchRepository
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import com.thomaskioko.tvmaniac.watchlist.testing.FakeLibraryRepository
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class HomePresenterTest {
  private val lifecycle = LifecycleRegistry()
  private val testDispatcher = StandardTestDispatcher()
  private val traktAuthManager = FakeTraktAuthManager()
  private val datastoreRepository = FakeDatastoreRepository()
  private val featuredShowsRepository = FakeFeaturedShowsRepository()
  private val trendingShowsRepository = FakeTrendingShowsRepository()
  private val upcomingShowsRepository = FakeUpcomingShowsRepository()
  private val searchRepository = FakeSearchRepository()

  private lateinit var presenter: HomePresenter

  @BeforeTest
  fun before() {
    Dispatchers.setMain(testDispatcher)
    lifecycle.resume()

    presenter = buildHomePresenterFactory()
  }

  @Test
  fun `initial state should be Discover`() = runTest {
    presenter.stack.test {
      awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Discover>()
    }
  }

  @Test
  fun `should return Search as active instance when onSearchClicked`() = runTest {
    presenter.stack.test {
      awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Discover>()
      presenter.onSearchClicked()

      awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Search>()
    }
  }

  @Test
  fun `should return Library as active instance when onSettingsClicked`() = runTest {
    presenter.stack.test {
      awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Discover>()
      presenter.onLibraryClicked()

      awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Library>()
    }
  }

  @Test
  fun `should return Settings as active instance when onSettingsClicked`() = runTest {
    presenter.stack.test {
      awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Discover>()
      presenter.onSettingsClicked()

      awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Settings>()
    }
  }

  private fun buildSearchPresenterFactory(
    componentContext: ComponentContext,
  ): SearchPresenterFactory = { _: ComponentContext, _: (id: Long) -> Unit ->
    SearchShowsPresenter(
      componentContext = componentContext,
      searchRepository = searchRepository,
      onNavigateToShowDetails = {},
      featuredShowsRepository = featuredShowsRepository,
      trendingShowsRepository = trendingShowsRepository,
      upcomingShowsRepository = upcomingShowsRepository,
      mapper = ShowMapper(
       formatterUtil = FakeFormatterUtil(),
      ),
    )
  }

  private fun buildHomePresenterFactory(
    componentContext: ComponentContext = DefaultComponentContext(lifecycle = lifecycle)
  ): HomePresenter =
    HomePresenter(
      componentContext = componentContext,
      onShowClicked = {},
      onMoreShowClicked = {},
      traktAuthManager = traktAuthManager,
      searchPresenterFactory = buildSearchPresenterFactory(componentContext),
      settingsPresenterFactory = buildSettingsPresenterFactory(componentContext),
      discoverComponentFactory = buildDiscoverPresenterFactory(componentContext),
      libraryPresenterFactory = buildLibraryPresenterFactory(componentContext),
    )

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

  private fun buildDiscoverPresenterFactory(
    componentContext: ComponentContext,
  ): DiscoverShowsPresenterFactory =
    { _: ComponentContext, _: (id: Long) -> Unit, _: (categoryId: Long) -> Unit ->
      DiscoverShowsPresenter(
        componentContext = componentContext,
        onNavigateToShowDetails = {},
        onNavigateToMore = {},
        featuredShowsRepository = featuredShowsRepository,
        trendingShowsRepository = trendingShowsRepository,
        upcomingShowsRepository = upcomingShowsRepository,
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
}
