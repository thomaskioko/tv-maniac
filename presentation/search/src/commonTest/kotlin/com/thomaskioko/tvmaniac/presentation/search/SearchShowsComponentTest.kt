package com.thomaskioko.tvmaniac.presentation.search

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.ServerError
import com.thomaskioko.tvmaniac.data.featuredshows.testing.FakeFeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.trendingshows.testing.FakeTrendingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.testing.FakeUpcomingShowsRepository
import com.thomaskioko.tvmaniac.search.testing.FakeSearchRepository
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class SearchShowsComponentTest {
  private val testDispatcher = StandardTestDispatcher()
  private lateinit var fakeSearchRepository: FakeSearchRepository
  private lateinit var featuredShowsRepository: FakeFeaturedShowsRepository
  private lateinit var trendingShowsRepository: FakeTrendingShowsRepository
  private lateinit var upcomingShowsRepository: FakeUpcomingShowsRepository
  private lateinit var component: SearchShowsComponent

  @BeforeTest
  fun before() {
    Dispatchers.setMain(testDispatcher)
    fakeSearchRepository = FakeSearchRepository()
    featuredShowsRepository = FakeFeaturedShowsRepository()
    trendingShowsRepository = FakeTrendingShowsRepository()
    upcomingShowsRepository = FakeUpcomingShowsRepository()
    component = buildComponent()
  }

  @AfterTest
  fun after() {
    Dispatchers.resetMain()
  }

  @Test
  fun `should return loading state when initialized`() = runTest {
    component.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
    }
  }

  @Test
  fun `should return initial state when query is blank`() = runTest {
    component.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      component.dispatch(QueryChanged(""))
      expectNoEvents()
    }
  }

  @Test
  fun `should return empty state when show content is empty`() = runTest {
    component.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      setList(emptyList())

      awaitItem() shouldBe EmptyState
    }
  }

  @Test
  fun `should return show content when show content is not empty`() = runTest {
    component.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      setList(createDiscoverShowList())

      awaitItem() shouldBe ShowContentAvailable(
        isUpdating = false,
        featuredShows = uiModelList(),
        trendingShows = uiModelList(),
        upcomingShows = uiModelList(),
      )
    }
  }

  @Test
  fun `should not perform search when query is less than 3 characters`() = runTest {
    component.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      component.dispatch(QueryChanged("te"))
      expectNoEvents()
    }
  }

  @Test
  fun `should return empty state when query is valid and results are empty`() = runTest {
    component.state.test {

      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      setList(createDiscoverShowList())
      awaitItem() shouldBe ShowContentAvailable(
        isUpdating = false,
        featuredShows = uiModelList(),
        trendingShows = uiModelList(),
        upcomingShows = uiModelList(),
      )

      component.dispatch(QueryChanged("test"))

      testScheduler.advanceTimeBy(300) // Wait for debounce

      fakeSearchRepository.setSearchResult(Either.Right(emptyList()))

      testScheduler.advanceUntilIdle()

      awaitItem() shouldBe SearchResultAvailable(isUpdating = true, result = persistentListOf())

      awaitItem() shouldBe EmptyState
    }
  }

  @Test
  fun `should return loading state with previous results when query changes`() = runTest {

    component.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      setList(createDiscoverShowList())

      awaitItem() shouldBe ShowContentAvailable(
        isUpdating = false,
        featuredShows = uiModelList(),
        trendingShows = uiModelList(),
        upcomingShows = uiModelList(),
      )

      // Dispatch first query change
      component.dispatch(QueryChanged("test"))


      awaitItem() shouldBe SearchResultAvailable(isUpdating = true, result = persistentListOf())

      // Advance time to trigger debounce and receive the result
      testScheduler.advanceTimeBy(300)

      fakeSearchRepository.setSearchResult(Either.Right(createDiscoverShowList()))

      val firstResult = awaitItem()
      firstResult shouldBe SearchResultAvailable(
        isUpdating = false,
        result = uiModelList(),
      )

      // Dispatch second query change to validate previous results shown as `isUpdating`
      component.dispatch(QueryChanged("new"))
      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = true,
        result = (firstResult as SearchResultAvailable).result,
      )
    }
  }

  @Test
  fun `should handle transition from valid to short query to empty query`() = runTest {
    component.state.test {
      setList(emptyList())

      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      awaitItem() shouldBe EmptyState

      component.dispatch(QueryChanged("test"))
      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = true,
        result = persistentListOf(),
      )

      component.dispatch(QueryChanged("te"))
      expectNoEvents()

      component.dispatch(QueryChanged(""))

      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
    }
  }

  @Test
  fun `should handle transition from short to valid query`() = runTest {
    component.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      component.dispatch(QueryChanged("ab"))
      expectNoEvents()

      // Valid query
      component.dispatch(QueryChanged("abc"))
      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = true,
        result = persistentListOf(),
      )

      testScheduler.advanceTimeBy(300)

      fakeSearchRepository.setSearchResult(Either.Right(createDiscoverShowList()))

      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = false,
        result = uiModelList(),
      )
    }
  }

  @Test
  fun `should handle empty short and valid query transitions correctly`() = runTest {
    component.state.test {
      awaitItem() shouldBe ShowContentAvailable()

      component.dispatch(QueryChanged(""))
      expectNoEvents()

      component.dispatch(QueryChanged("ab"))
      expectNoEvents()

      testScheduler.advanceTimeBy(300)
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      component.dispatch(QueryChanged("test"))

      fakeSearchRepository.setSearchResult(Either.Right(createDiscoverShowList()))
      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = true,
        result = persistentListOf(),
      )

      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = false,
        result = uiModelList(),
      )

      // Back to empty
      component.dispatch(QueryChanged(""))
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
    }
  }

  @Test
  fun `should return empty state when query is valid and search returns empty results`() = runTest {
    component.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      component.dispatch(QueryChanged("test"))

      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = true,
        result = persistentListOf(),
      )

      testScheduler.advanceTimeBy(300)

      fakeSearchRepository.setSearchResult(Either.Right(emptyList()))

      awaitItem() shouldBe EmptyState
    }
  }

  @Test
  fun `should handle sequence of empty and non-empty results`() = runTest {
    component.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      component.dispatch(QueryChanged("empty"))
      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = true,
        result = persistentListOf(),
      )

      testScheduler.advanceTimeBy(300)

      fakeSearchRepository.setSearchResult(Either.Right(emptyList()))
      awaitItem() shouldBeSameInstanceAs EmptyState

      component.dispatch(QueryChanged("test"))
      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = true,
        result = persistentListOf(),
      )

      testScheduler.advanceTimeBy(300)

      fakeSearchRepository.setSearchResult(Either.Right(createDiscoverShowList()))

      val firstResult = awaitItem()
      firstResult shouldBe SearchResultAvailable(
        isUpdating = false,
        result = uiModelList(),
      )

      component.dispatch(QueryChanged("none"))
      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = true,
        result = (firstResult as SearchResultAvailable).result,
      )

      testScheduler.advanceTimeBy(300)

      fakeSearchRepository.setSearchResult(Either.Right(emptyList()))
      awaitItem() shouldBeSameInstanceAs EmptyState
    }
  }

  @Test
  fun `should handle error states correctly`() = runTest {
    component.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      component.dispatch(QueryChanged("test"))
      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = true,
        result = persistentListOf(),
      )

      testScheduler.advanceTimeBy(300)

      val error = ServerError("Test error")
      fakeSearchRepository.setSearchResult(Either.Left(error))

      awaitItem() shouldBe ErrorState(error.errorMessage)
    }
  }

  private fun buildComponent(
    lifecycle: LifecycleRegistry = LifecycleRegistry(),
  ): SearchShowsComponent = SearchShowsComponent(
    componentContext = DefaultComponentContext(lifecycle = lifecycle),
    onNavigateToShowDetails = {},
    searchRepository = fakeSearchRepository,
    featuredShowsRepository = featuredShowsRepository,
    trendingShowsRepository = trendingShowsRepository,
    upcomingShowsRepository = upcomingShowsRepository,
  )


  private suspend fun TestScope.setList(list: List<ShowEntity>) {
    featuredShowsRepository.setFeaturedShows(Either.Right(list))
    upcomingShowsRepository.setUpcomingShows(Either.Right(list))
    trendingShowsRepository.setTrendingShows(Either.Right(list))

    testScheduler.advanceUntilIdle()
  }

  private fun createDiscoverShowList(size: Int = LIST_SIZE) =
    List(size) {
      ShowEntity(
        id = 84958,
        title = "Loki",
        posterPath = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        inLibrary = false,
        overview = null,
        status = null
      )
    }
      .toImmutableList()

  private fun uiModelList(size: Int = LIST_SIZE) =
    createDiscoverShowList(size)
      .map {
        SearchResult(
          tmdbId = it.id,
          title = it.title,
          posterImageUrl = it.posterPath,
          inLibrary = it.inLibrary,
          overview = it.overview,
          status = it.status,
        )
      }
      .toImmutableList()

  companion object {
    const val LIST_SIZE = 5
  }
}
