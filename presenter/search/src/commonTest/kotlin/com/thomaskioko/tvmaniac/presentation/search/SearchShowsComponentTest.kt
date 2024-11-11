package com.thomaskioko.tvmaniac.presentation.search

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomakioko.tvmaniac.util.testing.FakeFormatterUtil
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.ServerError
import com.thomaskioko.tvmaniac.data.featuredshows.testing.FakeFeaturedShowsRepository
import com.thomaskioko.tvmaniac.data.trendingshows.testing.FakeTrendingShowsRepository
import com.thomaskioko.tvmaniac.data.upcomingshows.testing.FakeUpcomingShowsRepository
import com.thomaskioko.tvmaniac.search.testing.FakeSearchRepository
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import io.kotest.matchers.shouldBe
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
  private lateinit var presenter: SearchShowsPresenter

  @BeforeTest
  fun before() {
    Dispatchers.setMain(testDispatcher)
    fakeSearchRepository = FakeSearchRepository()
    featuredShowsRepository = FakeFeaturedShowsRepository()
    trendingShowsRepository = FakeTrendingShowsRepository()
    upcomingShowsRepository = FakeUpcomingShowsRepository()
    presenter = buildPresenter()
  }

  @AfterTest
  fun after() {
    Dispatchers.resetMain()
  }

  @Test
  fun `should return loading state when initialized`() = runTest {
    presenter.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
    }
  }

  @Test
  fun `should return initial state when query is blank`() = runTest {
    presenter.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      presenter.dispatch(QueryChanged(""))
      expectNoEvents()
    }
  }

  @Test
  fun `should return empty state when show content is empty`() = runTest {
    presenter.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      setList(emptyList())

      awaitItem() shouldBe ErrorSearchState(errorMessage = null)
    }
  }

  @Test
  fun `should return show content when show content is not empty`() = runTest {
    presenter.state.test {
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
    presenter.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      presenter.dispatch(QueryChanged("te"))
      expectNoEvents()
    }
  }

  @Test
  fun `should return empty state when query is valid and results are empty`() = runTest {
    presenter.state.test {

      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      setList(createDiscoverShowList())
      awaitItem() shouldBe ShowContentAvailable(
        isUpdating = false,
        featuredShows = uiModelList(),
        trendingShows = uiModelList(),
        upcomingShows = uiModelList(),
      )

      presenter.dispatch(QueryChanged("test"))

      testScheduler.advanceTimeBy(300) // Wait for debounce

      fakeSearchRepository.setSearchResult(Either.Right(emptyList()))

      testScheduler.advanceUntilIdle()

      awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "test")

      awaitItem() shouldBe EmptySearchState("test")
    }
  }

  @Test
  fun `should return loading state with previous results when query changes`() = runTest {

    presenter.state.test {
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
      presenter.dispatch(QueryChanged("test"))


      awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "test")

      // Advance time to trigger debounce and receive the result
      testScheduler.advanceTimeBy(300)

      fakeSearchRepository.setSearchResult(Either.Right(createDiscoverShowList()))

      val firstResult = awaitItem()
      firstResult shouldBe SearchResultAvailable(
        isUpdating = false,
        results = uiModelList(),
        query = "test",
      )

      // Dispatch second query change to validate previous results shown as `isUpdating`
      presenter.dispatch(QueryChanged("new"))
      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = true,
        query = "new",
        results = (firstResult as SearchResultAvailable).results,
      )
    }
  }

  @Test
  fun `should handle transition from valid to short query to empty query`() = runTest {
    presenter.state.test {
      setList(emptyList())

      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      awaitItem() shouldBe ErrorSearchState(errorMessage = null)

      presenter.dispatch(QueryChanged("test"))
      awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "test")

      presenter.dispatch(QueryChanged("te"))
      expectNoEvents()

      presenter.dispatch(QueryChanged(""))

      expectNoEvents()
    }
  }

  @Test
  fun `should handle transition from short to valid query`() = runTest {
    presenter.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      presenter.dispatch(QueryChanged("ab"))
      expectNoEvents()

      // Valid query
      presenter.dispatch(QueryChanged("abc"))
      awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "abc")

      testScheduler.advanceTimeBy(300)

      fakeSearchRepository.setSearchResult(Either.Right(createDiscoverShowList()))

      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = false,
        query = "abc",
        results = uiModelList(),
      )
    }
  }

  @Test
  fun `should handle empty short and valid query transitions correctly`() = runTest {
    presenter.state.test {
      awaitItem() shouldBe ShowContentAvailable()

      presenter.dispatch(QueryChanged(""))
      expectNoEvents()

      presenter.dispatch(QueryChanged("ab"))
      expectNoEvents()

      testScheduler.advanceTimeBy(300)
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      presenter.dispatch(QueryChanged("test"))

      fakeSearchRepository.setSearchResult(Either.Right(createDiscoverShowList()))
      awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "test")

      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = false,
        query = "test",
        results = uiModelList(),
      )

      // Back to empty
      presenter.dispatch(QueryChanged(""))
      expectNoEvents()
    }
  }

  @Test
  fun `should return empty state when query is valid and search returns empty results`() = runTest {
    presenter.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      presenter.dispatch(QueryChanged("test"))

      awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "test")

      testScheduler.advanceTimeBy(300)

      fakeSearchRepository.setSearchResult(Either.Right(emptyList()))

      awaitItem() shouldBe EmptySearchState("test")
    }
  }

  @Test
  fun `should handle sequence of empty and non-empty results`() = runTest {
    presenter.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      presenter.dispatch(QueryChanged("empty"))
      awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "empty")

      testScheduler.advanceTimeBy(300)

      fakeSearchRepository.setSearchResult(Either.Right(emptyList()))
      awaitItem() shouldBe EmptySearchState("empty")

      presenter.dispatch(QueryChanged("test"))
      awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "test")

      testScheduler.advanceTimeBy(300)

      fakeSearchRepository.setSearchResult(Either.Right(createDiscoverShowList()))

      val firstResult = awaitItem()
      firstResult shouldBe SearchResultAvailable(
        isUpdating = false,
        query = "test",
        results = uiModelList(),
      )

      presenter.dispatch(QueryChanged("none"))
      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = true,
        query = "none",
        results = (firstResult as SearchResultAvailable).results,
      )

      testScheduler.advanceTimeBy(300)

      fakeSearchRepository.setSearchResult(Either.Right(emptyList()))
      awaitItem() shouldBe EmptySearchState("none")
    }
  }

  @Test
  fun `should update state when on clear query and show content is available`() = runTest {
    presenter.state.test {


      awaitItem() shouldBe ShowContentAvailable()
      setList(createDiscoverShowList())

      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
      awaitItem() shouldBe ShowContentAvailable(
        isUpdating = false,
        featuredShows = uiModelList(),
        trendingShows = uiModelList(),
        upcomingShows = uiModelList(),
      )

      presenter.dispatch(QueryChanged("test"))

      awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "test")

      testScheduler.advanceTimeBy(300)

      fakeSearchRepository.setSearchResult(Either.Right(createDiscoverShowList()))

      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = false,
        query = "test",
        results = uiModelList(),
      )

      presenter.dispatch(ClearQuery)

      setList(createDiscoverShowList())

      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      awaitItem() shouldBe ShowContentAvailable(
        isUpdating = false,
        featuredShows = uiModelList(),
        trendingShows = uiModelList(),
        upcomingShows = uiModelList(),
      )
    }
  }

  @Test
  fun `should handle error states correctly`() = runTest {
    presenter.state.test {
      awaitItem() shouldBe ShowContentAvailable()
      awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

      presenter.dispatch(QueryChanged("test"))
      awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "test")

      testScheduler.advanceTimeBy(300)

      val error = ServerError("Test error")
      fakeSearchRepository.setSearchResult(Either.Left(error))

      awaitItem() shouldBe ErrorSearchState(errorMessage = error.errorMessage)
    }
  }

  private fun buildPresenter(
    lifecycle: LifecycleRegistry = LifecycleRegistry(),
  ): SearchShowsPresenter = SearchShowsPresenter(
    componentContext = DefaultComponentContext(lifecycle = lifecycle),
    onNavigateToShowDetails = {},
    searchRepository = fakeSearchRepository,
    featuredShowsRepository = featuredShowsRepository,
    trendingShowsRepository = trendingShowsRepository,
    upcomingShowsRepository = upcomingShowsRepository,
    mapper = ShowMapper(
      formatterUtil = FakeFormatterUtil(),
    ),
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
        status = null,
        voteAverage = null
      )
    }
      .toImmutableList()

  private fun uiModelList(size: Int = LIST_SIZE) =
    createDiscoverShowList(size)
      .map {
        ShowItem(
          tmdbId = it.id,
          title = it.title,
          posterImageUrl = it.posterPath,
          inLibrary = it.inLibrary,
          overview = it.overview,
          status = it.status,
          voteAverage = it.voteAverage,
          year = it.year,
        )
      }
      .toImmutableList()

  companion object {
    const val LIST_SIZE = 5
  }
}
