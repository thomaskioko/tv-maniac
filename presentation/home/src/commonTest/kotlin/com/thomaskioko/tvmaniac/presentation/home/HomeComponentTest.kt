package com.thomaskioko.tvmaniac.presentation.home

import app.cash.turbine.test
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.data.featuredshows.testing.FakeFeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.popularshows.testing.FakePopularShowsRepository
import com.thomaskioko.tvmaniac.data.topratedshows.testing.FakeTopRatedShowsRepository
import com.thomaskioko.tvmaniac.data.trendingshows.testing.FakeTrendingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.testing.FakeUpcomingShowsRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsComponent
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsComponentFactory
import com.thomaskioko.tvmaniac.presentation.search.SearchShowsComponent
import com.thomaskioko.tvmaniac.presentation.search.SearchComponentFactory
import com.thomaskioko.tvmaniac.presentation.settings.SettingsComponent
import com.thomaskioko.tvmaniac.presentation.settings.SettingsComponentFactory
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryComponent
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryComponentFactory
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

class HomeComponentTest {
  private val lifecycle = LifecycleRegistry()
  private val testDispatcher = StandardTestDispatcher()
  private val traktAuthManager = FakeTraktAuthManager()
  private val datastoreRepository = FakeDatastoreRepository()
  private val featuredShowsRepository = FakeFeaturedShowsRepository()
  private val trendingShowsRepository = FakeTrendingShowsRepository()
  private val upcomingShowsRepository = FakeUpcomingShowsRepository()
  private val searchRepository = FakeSearchRepository()

  private lateinit var component: HomeComponent

  @BeforeTest
  fun before() {
    Dispatchers.setMain(testDispatcher)
    lifecycle.resume()

    component = buildHomePresenterFactory()
  }

  @Test
  fun `initial state should be Discover`() = runTest {
    component.stack.test {
      awaitItem().active.instance.shouldBeInstanceOf<HomeComponent.Child.Discover>()
    }
  }

  @Test
  fun `should return Search as active instance when onSearchClicked`() = runTest {
    component.stack.test {
      awaitItem().active.instance.shouldBeInstanceOf<HomeComponent.Child.Discover>()
      component.onSearchClicked()

      awaitItem().active.instance.shouldBeInstanceOf<HomeComponent.Child.Search>()
    }
  }

  @Test
  fun `should return Library as active instance when onSettingsClicked`() = runTest {
    component.stack.test {
      awaitItem().active.instance.shouldBeInstanceOf<HomeComponent.Child.Discover>()
      component.onLibraryClicked()

      awaitItem().active.instance.shouldBeInstanceOf<HomeComponent.Child.Library>()
    }
  }

  @Test
  fun `should return Settings as active instance when onSettingsClicked`() = runTest {
    component.stack.test {
      awaitItem().active.instance.shouldBeInstanceOf<HomeComponent.Child.Discover>()
      component.onSettingsClicked()

      awaitItem().active.instance.shouldBeInstanceOf<HomeComponent.Child.Settings>()
    }
  }

  private fun buildSearchPresenterFactory(
    componentContext: ComponentContext,
  ): SearchComponentFactory = { _: ComponentContext, _: (id: Long) -> Unit ->
    SearchShowsComponent(
      componentContext = componentContext,
      searchRepository = searchRepository,
      onNavigateToShowDetails = {},
      featuredShowsRepository = featuredShowsRepository,
      trendingShowsRepository = trendingShowsRepository,
      upcomingShowsRepository = upcomingShowsRepository,
    )
  }

  private fun buildHomePresenterFactory(
    componentContext: ComponentContext = DefaultComponentContext(lifecycle = lifecycle)
  ): HomeComponent =
    HomeComponent(
      componentContext = componentContext,
      onShowClicked = {},
      onMoreShowClicked = {},
      traktAuthManager = traktAuthManager,
      searchComponentFactory = buildSearchPresenterFactory(componentContext),
      settingsComponentFactory = buildSettingsPresenterFactory(componentContext),
      discoverComponentFactory = buildDiscoverPresenterFactory(componentContext),
      libraryComponentFactory = buildLibraryPresenterFactory(componentContext),
    )

  private fun buildSettingsPresenterFactory(
    componentContext: ComponentContext,
  ): SettingsComponentFactory = { _: ComponentContext, _: () -> Unit ->
    SettingsComponent(
      componentContext = componentContext,
      launchWebView = {},
      datastoreRepository = datastoreRepository,
      traktAuthRepository = FakeTraktAuthRepository(),
    )
  }

  private fun buildDiscoverPresenterFactory(
    componentContext: ComponentContext,
  ): DiscoverShowsComponentFactory =
    { _: ComponentContext, _: (id: Long) -> Unit, _: (categoryId: Long) -> Unit ->
      DiscoverShowsComponent(
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
  ): LibraryComponentFactory = { _: ComponentContext, _: (showDetails: Long) -> Unit ->
    LibraryComponent(
      componentContext = componentContext,
      navigateToShowDetails = {},
      repository = FakeLibraryRepository(),
    )
  }
}
