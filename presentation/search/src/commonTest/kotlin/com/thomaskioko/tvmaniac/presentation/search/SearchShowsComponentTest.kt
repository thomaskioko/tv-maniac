package com.thomaskioko.tvmaniac.presentation.search

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.ServerError
import com.thomaskioko.tvmaniac.search.testing.FakeSearchRepository
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class SearchShowsComponentTest {
  private val testDispatcher = StandardTestDispatcher()
  private lateinit var fakeSearchRepository: FakeSearchRepository
  private lateinit var component: SearchShowsComponent

  @BeforeTest
  fun before() {
    Dispatchers.setMain(testDispatcher)
    fakeSearchRepository = FakeSearchRepository()
    component = buildComponent()
  }

  @AfterTest
  fun after() {
    Dispatchers.resetMain()
  }

  @Test
  fun `should return empty state when initialized`() = runTest {
    component.state.test {
      awaitItem() shouldBe InitialState()
    }
  }

  @Test
  fun `should return initial state when query is blank`() = runTest {
    component.state.test {
      awaitItem() shouldBe InitialState()

      component.dispatch(QueryChanged(""))
      expectNoEvents()
    }
  }

  @Test
  fun `should return empty state when query is valid and results are empty`() = runTest {
    component.state.test {
      awaitItem() shouldBe InitialState()

      component.dispatch(QueryChanged("test"))

      awaitItem() shouldBe SearchResultAvailable(isUpdating = true, result = persistentListOf())

      testScheduler.advanceTimeBy(300)
      expectNoEvents()
    }
  }

  @Test
  fun `should not perform search when query is less than 3 characters`() = runTest {
    component.state.test {
      awaitItem() shouldBe InitialState()

      component.dispatch(QueryChanged("te"))
      expectNoEvents()
    }
  }

  @Test
  fun `should return loading state with previous results when query changes`() = runTest {

    component.state.test {
      awaitItem() shouldBe InitialState()

      // Dispatch first query change
      component.dispatch(QueryChanged("test"))


      awaitItem() shouldBe SearchResultAvailable(isUpdating = true, result = persistentListOf())

      // Advance time to trigger debounce and receive the result
      testScheduler.advanceTimeBy(300)

      val showEntities = listOf(
        ShowEntity(id = 1, title = "Initial", posterPath = "path", inLibrary = false, overview = "overview"),
      )

      fakeSearchRepository.setSearchResult(Either.Right(showEntities))

      val firstResult = awaitItem()
      firstResult shouldBe SearchResultAvailable(
        isUpdating = false,
        result = showEntities.toResult(),
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
  fun `should handle transition from valid to short query`() = runTest {
    component.state.test {
      awaitItem() shouldBe InitialState()

      component.dispatch(QueryChanged("test"))
      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = true,
        result = persistentListOf(),
      )

      component.dispatch(QueryChanged("te"))
      expectNoEvents()

      component.dispatch(QueryChanged(""))
      awaitItem() shouldBe InitialState()
    }
  }

  @Test
  fun `should handle transition from short to valid query`() = runTest {
    component.state.test {
      awaitItem() shouldBe InitialState()

      component.dispatch(QueryChanged("ab"))
      expectNoEvents()

      // Valid query
      component.dispatch(QueryChanged("abc"))
      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = true,
        result = persistentListOf(),
      )

      testScheduler.advanceTimeBy(300)

      val shows = listOf(
        ShowEntity(
          id = 1,
          title = "Test",
          posterPath = "path",
          inLibrary = false,
          overview = "overview",
          status = "",
        ),
      )

      fakeSearchRepository.setSearchResult(Either.Right(shows))

      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = false,
        result = shows.toResult(),
      )
    }
  }

  @Test
  fun `should handle empty short and valid query transitions correctly`() = runTest {
    component.state.test {
      awaitItem() shouldBe InitialState()

      component.dispatch(QueryChanged(""))
      expectNoEvents()

      component.dispatch(QueryChanged("ab"))
      expectNoEvents()

      testScheduler.advanceTimeBy(300)
      expectNoEvents()

      val expectedShows = listOf(
        ShowEntity(id = 1, title = "Test", posterPath = "path", inLibrary = false, overview = "overview"),
      )

      component.dispatch(QueryChanged("test"))

      fakeSearchRepository.setSearchResult(Either.Right(expectedShows))
      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = true,
        result = persistentListOf(),
      )

      awaitItem() shouldBe SearchResultAvailable(
        isUpdating = false,
        result = expectedShows.toResult(),
      )

      // Back to empty
      component.dispatch(QueryChanged(""))
      awaitItem() shouldBe InitialState()
    }
  }

  @Test
  fun `should return empty state when query is valid and search returns empty results`() = runTest {
    component.state.test {
      awaitItem() shouldBe InitialState()

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
      awaitItem() shouldBe InitialState()

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

      val shows = listOf(
        ShowEntity(id = 1, title = "Test", posterPath = "path", inLibrary = false, overview = "overview"),
      )

      fakeSearchRepository.setSearchResult(Either.Right(shows))

      val firstResult = awaitItem()
      firstResult shouldBe SearchResultAvailable(
        isUpdating = false,
        result = shows.toResult(),
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
      awaitItem() shouldBe InitialState()

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
    onNavigateToMore = {},
    searchRepository = fakeSearchRepository,
  )

  private fun List<ShowEntity>.toResult() = map {
    SearchResult(
      tmdbId = it.id,
      title = it.title,
      posterImageUrl = it.posterPath,
      inLibrary = it.inLibrary,
      overview = it.overview,
      status = it.status,
    )
  }.toImmutableList()
}
