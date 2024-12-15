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
import com.thomaskioko.tvmaniac.genre.FakeGenreRepository
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverPresenterFactory
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsPresenter
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
  private val genreRepository = FakeGenreRepository()

  private lateinit var presenter: HomePresenter

  @BeforeTest
  fun before() {
    Dispatchers.setMain(testDispatcher)
    lifecycle.resume()

    presenter = buildHomePresenterFactory().create(
      componentContext = DefaultComponentContext(lifecycle = lifecycle),
      onShowClicked = {},
      onMoreShowClicked = {},
      onShowGenreClicked = {}
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

      awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Library>()
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

  private fun buildSearchPresenterFactory(
    componentContext: ComponentContext,
  ): SearchPresenterFactory = SearchPresenterFactory(
    create = { _: ComponentContext, _: (id: Long) -> Unit, _: (id: Long) -> Unit ->
      SearchShowsPresenter(
        componentContext = componentContext,
        searchRepository = searchRepository,
        onNavigateToShowDetails = {},
        onNavigateToGenre = {},
        genreRepository = genreRepository,
        mapper = ShowMapper(
          formatterUtil = FakeFormatterUtil(),
        ),
      )
    }
  )

  private fun buildHomePresenterFactory(): HomePresenter.Factory =
    DefaultHomePresenter.Factory(
      discoverPresenterFactory = buildDiscoverPresenterFactory(DefaultComponentContext(lifecycle = lifecycle)),
      libraryPresenterFactory = buildLibraryPresenterFactory(DefaultComponentContext(lifecycle = lifecycle)),
      searchPresenterFactory = buildSearchPresenterFactory(DefaultComponentContext(lifecycle = lifecycle)),
      settingsPresenterFactory = buildSettingsPresenterFactory(DefaultComponentContext(lifecycle = lifecycle)),
      traktAuthManager = traktAuthManager,
    )

  private fun buildSettingsPresenterFactory(
    componentContext: ComponentContext,
  ): SettingsPresenterFactory = SettingsPresenterFactory(create = { _: ComponentContext, _: () -> Unit ->
    SettingsPresenter(
      componentContext = componentContext,
      launchWebView = {},
      datastoreRepository = datastoreRepository,
      traktAuthRepository = FakeTraktAuthRepository(),
    )
  })

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
        topRatedShowsRepository = FakeTopRatedShowsRepository(),
        popularShowsRepository = FakePopularShowsRepository(),
        libraryRepository = FakeLibraryRepository(),
      )
    }
    )

  private fun buildLibraryPresenterFactory(
    componentContext: ComponentContext,
  ): LibraryPresenterFactory =LibraryPresenterFactory(
    create = { _: ComponentContext, _: (showDetails: Long) -> Unit ->
    LibraryPresenter(
      componentContext = componentContext,
      navigateToShowDetails = {},
      repository = FakeLibraryRepository(),
    )
  })
}
