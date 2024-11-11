package com.thomaskioko.tvmaniac.presentation.discover

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.data.featuredshows.testing.FakeFeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.popularshows.testing.FakePopularShowsRepository
import com.thomaskioko.tvmaniac.data.topratedshows.testing.FakeTopRatedShowsRepository
import com.thomaskioko.tvmaniac.data.trendingshows.testing.FakeTrendingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.testing.FakeUpcomingShowsRepository
import com.thomaskioko.tvmaniac.presentation.discover.model.DiscoverShow
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

class DiscoverShowsPresenterTest {

  private val testDispatcher = StandardTestDispatcher()

  private val featuredShowsRepository = FakeFeaturedShowsRepository()
  private val trendingShowsRepository = FakeTrendingShowsRepository()
  private val upcomingShowsRepository = FakeUpcomingShowsRepository()
  private val topRatedShowsRepository = FakeTopRatedShowsRepository()
  private val popularShowsRepository = FakePopularShowsRepository()

  private lateinit var presenter: DiscoverShowsPresenter

  @BeforeTest
  fun before() {
    Dispatchers.setMain(testDispatcher)

    presenter = buildPresenter()
  }

  @AfterTest
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `instance is maintained across recreations`() = runTest {
    // Create the first instance
    val initialPresenter = buildPresenter()

    // Simulate some state change
    initialPresenter.dispatch(RefreshData)

    val recreatedPresenter = buildPresenter()

    // Assert that the states are the same
    initialPresenter.state.value shouldBeEqual recreatedPresenter.state.value

    recreatedPresenter.dispatch(PopularClicked)

    // Assert that the internal PresenterInstance is the same
    initialPresenter.presenterInstance shouldNotBeSameInstanceAs
      recreatedPresenter.presenterInstance
  }

  @Test
  fun `should return EmptyState when data is empty`() = runTest {
    presenter.state.test {
      setList(emptyList())

      awaitItem() shouldBe Loading
      awaitItem() shouldBe EmptyState
    }
  }

  @Test
  fun `should return DataLoaded when data is fetched`() = runTest {
    presenter.state.test {
      setList(emptyList())

      awaitItem() shouldBe Loading
      awaitItem() shouldBe EmptyState

      setList(createDiscoverShowList())

      awaitItem() shouldBe
        DataLoaded(
          featuredShows = uiModelList(),
          topRatedShows = uiModelList(),
          popularShows = uiModelList(),
          upcomingShows = uiModelList(),
          trendingToday = uiModelList(),
          isRefreshing = false,
        )
    }
  }

  @Test
  fun `should return DataLoaded when data is fetched from cache`() = runTest {
    presenter.state.test {
      setList(createDiscoverShowList())

      awaitItem() shouldBe Loading
      awaitItem() shouldBe
        DataLoaded(
          featuredShows = uiModelList(),
          topRatedShows = uiModelList(),
          popularShows = uiModelList(),
          upcomingShows = uiModelList(),
          trendingToday = uiModelList(),
          isRefreshing = false,
        )
    }
  }

  @Test
  fun `should return DataLoaded when data is empty and refresh is clicked`() = runTest {
    presenter.state.test {
      setList(emptyList())

      awaitItem() shouldBe Loading
      awaitItem() shouldBe EmptyState

      presenter.dispatch(ReloadData)

      setList(createDiscoverShowList())

      awaitItem() shouldBe Loading
      awaitItem() shouldBe
        DataLoaded(
          featuredShows = uiModelList(),
          topRatedShows = uiModelList(),
          popularShows = uiModelList(),
          upcomingShows = uiModelList(),
          trendingToday = uiModelList(),
          isRefreshing = false,
        )
    }
  }

  @Test
  fun `should return DataLoaded with refreshed data when refresh is clicked`() = runTest {
    presenter.state.test {
      setList(createDiscoverShowList())
      awaitItem() shouldBe Loading

      val expectedList = uiModelList()
      val expectedResult =
        DataLoaded(
          featuredShows = expectedList,
          topRatedShows = expectedList,
          popularShows = expectedList,
          upcomingShows = expectedList,
          trendingToday = expectedList,
          isRefreshing = false,
        )

      awaitItem() shouldBe expectedResult

      presenter.dispatch(RefreshData)

      awaitItem() shouldBe expectedResult.copy(isRefreshing = true)

      setList(createDiscoverShowList(2))

      val expectedUpdatedList = uiModelList(2)

      val expectedUpdatedResult =
        DataLoaded(
          featuredShows = expectedUpdatedList,
          topRatedShows = expectedUpdatedList,
          popularShows = expectedUpdatedList,
          upcomingShows = expectedUpdatedList,
          trendingToday = expectedUpdatedList,
          isRefreshing = false,
        )

      awaitItem() shouldBe expectedUpdatedResult
    }
  }

  @Test
  fun `should return DataLoaded when error occurs and refresh is clicked`() = runTest {
    setList(createDiscoverShowList())

    presenter.state.test {
      awaitItem() shouldBe Loading

      val expectedList = uiModelList()
      val expectedResult =
        DataLoaded(
          featuredShows = expectedList,
          topRatedShows = expectedList,
          popularShows = expectedList,
          upcomingShows = expectedList,
          trendingToday = expectedList,
          isRefreshing = false,
        )

      awaitItem() shouldBe expectedResult

      presenter.dispatch(RefreshData)

      awaitItem() shouldBe expectedResult.copy(isRefreshing = true)

      setList(createDiscoverShowList(2))

      val expectedUpdatedList = uiModelList(2)

      val expectedUpdatedResult =
        DataLoaded(
          featuredShows = expectedUpdatedList,
          topRatedShows = expectedUpdatedList,
          popularShows = expectedUpdatedList,
          upcomingShows = expectedUpdatedList,
          trendingToday = expectedUpdatedList,
          isRefreshing = false,
          errorMessage = null,
        )

      awaitItem() shouldBe expectedUpdatedResult
    }
  }

  private suspend fun setList(list: List<ShowEntity>) {
    featuredShowsRepository.setFeaturedShows(Either.Right(list))
    topRatedShowsRepository.setTopRatedShows(Either.Right(list))
    popularShowsRepository.setPopularShows(Either.Right(list))
    upcomingShowsRepository.setUpcomingShows(Either.Right(list))
    trendingShowsRepository.setTrendingShows(Either.Right(list))
  }

  private fun createDiscoverShowList(size: Int = LIST_SIZE) =
    List(size) {
        ShowEntity(
          id = 84958,
          title = "Loki",
          posterPath = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
          inLibrary = false,
        )
      }
      .toImmutableList()

  private fun uiModelList(size: Int = LIST_SIZE) =
    createDiscoverShowList(size)
      .map {
        DiscoverShow(
          tmdbId = it.id,
          title = it.title,
          posterImageUrl = it.posterPath,
          inLibrary = it.inLibrary,
        )
      }
      .toImmutableList()

  companion object {
    const val LIST_SIZE = 5
  }

  private fun buildPresenter(
    lifecycle: LifecycleRegistry = LifecycleRegistry(),
  ): DiscoverShowsPresenter =
    DiscoverShowsPresenter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        onNavigateToShowDetails = {},
        onNavigateToMore = {},
        featuredShowsRepository = featuredShowsRepository,
        trendingShowsRepository = trendingShowsRepository,
        upcomingShowsRepository = upcomingShowsRepository,
        topRatedShowsRepository = topRatedShowsRepository,
        popularShowsRepository = popularShowsRepository,
      )
      .also { lifecycle.resume() }
}
