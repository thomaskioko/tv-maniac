package com.thomaskioko.tvmaniac.presenter.search

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.genre.FakeGenreRepository
import com.thomaskioko.tvmaniac.genre.ShowGenresEntity
import com.thomaskioko.tvmaniac.search.presenter.ClearQuery
import com.thomaskioko.tvmaniac.search.presenter.DefaultSearchShowsPresenter
import com.thomaskioko.tvmaniac.search.presenter.EmptySearchResult
import com.thomaskioko.tvmaniac.search.presenter.InitialSearchState
import com.thomaskioko.tvmaniac.search.presenter.Mapper
import com.thomaskioko.tvmaniac.search.presenter.QueryChanged
import com.thomaskioko.tvmaniac.search.presenter.SearchResultAvailable
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import com.thomaskioko.tvmaniac.search.presenter.ShowContentAvailable
import com.thomaskioko.tvmaniac.search.presenter.model.ShowGenre
import com.thomaskioko.tvmaniac.search.presenter.model.ShowItem
import com.thomaskioko.tvmaniac.search.testing.FakeSearchRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.shouldBe
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

class SearchShowsPresenterTest {
    private val testDispatcher = StandardTestDispatcher()
    private val fakeSearchRepository = FakeSearchRepository()
    private val genreRepository = FakeGenreRepository()
    private lateinit var presenter: SearchShowsPresenter

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        presenter = buildPresenter()
    }

    @AfterTest
    fun after() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should return loading state when initialized`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe InitialSearchState()
            awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
            awaitItem() shouldBe ShowContentAvailable(isUpdating = false)
        }
    }

    @Test
    fun `should return initial state when query is blank`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe InitialSearchState()
            awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
            awaitItem() shouldBe ShowContentAvailable(isUpdating = false)

            presenter.dispatch(QueryChanged(""))
            expectNoEvents()
        }
    }

    @Test
    fun `should return empty state when show content is empty`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe InitialSearchState()
            awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

            setList(emptyList())

            awaitItem() shouldBe ShowContentAvailable(errorMessage = null)
        }
    }

    @Test
    fun `should return show content when show content is not empty`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe InitialSearchState()
            awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
            awaitItem() shouldBe ShowContentAvailable(isUpdating = false)

            setList(createGenreShowList())

            awaitItem() shouldBe ShowContentAvailable(
                isUpdating = false,
                genres = genreList(),
            )
        }
    }

    @Test
    fun `should not perform search when query is less than 3 characters`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe InitialSearchState()
            awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
            awaitItem() shouldBe ShowContentAvailable(isUpdating = false)

            presenter.dispatch(QueryChanged("te"))
            expectNoEvents()
        }
    }

    @Test
    fun `should return empty state when query is valid and results are empty`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe InitialSearchState()
            awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
            awaitItem() shouldBe ShowContentAvailable(isUpdating = false)

            setList(createGenreShowList())
            awaitItem() shouldBe ShowContentAvailable(
                isUpdating = false,
                genres = genreList(),
            )

            presenter.dispatch(QueryChanged("test"))

            testScheduler.advanceTimeBy(300) // Wait for debounce

            fakeSearchRepository.setSearchResult(emptyList())

            testScheduler.advanceUntilIdle()

            awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "test")

            awaitItem() shouldBe SearchResultAvailable("test", results = persistentListOf())
        }
    }

    @Test
    fun `should return loading state with previous results when query changes`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe InitialSearchState()
            awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
            awaitItem() shouldBe ShowContentAvailable(isUpdating = false)

            setList(createGenreShowList())

            awaitItem() shouldBe ShowContentAvailable(
                isUpdating = false,
                genres = genreList(),
            )

            // Dispatch first query change
            presenter.dispatch(QueryChanged("test"))

            awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "test")

            // Advance time to trigger debounce and receive the result
            testScheduler.advanceTimeBy(300)

            fakeSearchRepository.setSearchResult(createDiscoverShowList())

            val firstResult = awaitItem()
            firstResult shouldBe SearchResultAvailable(
                isUpdating = false,
                query = "test",
            )

            // Dispatch second query change to validate previous results shown as `isUpdating`
            presenter.dispatch(QueryChanged("new"))
            awaitItem() shouldBe SearchResultAvailable(
                isUpdating = false,
                query = "new",
                results = uiModelList(),
            )
        }
    }

    @Test
    fun `should handle transition from valid to short query to empty query`() = runTest {
        presenter.state.test {
            setList(emptyList())

            awaitItem() shouldBe InitialSearchState()
            awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

            awaitItem() shouldBe ShowContentAvailable(errorMessage = null)

            presenter.dispatch(QueryChanged("test"))
            awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "test")
            awaitItem() shouldBe SearchResultAvailable(
                isUpdating = false,
                query = "test",
            )

            presenter.dispatch(QueryChanged("te"))
            expectNoEvents()

            presenter.dispatch(QueryChanged(""))

            expectNoEvents()
        }
    }

    @Test
    fun `should handle transition from short to valid query`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe InitialSearchState()
            awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
            awaitItem() shouldBe ShowContentAvailable(isUpdating = false)

            presenter.dispatch(QueryChanged("ab"))
            expectNoEvents()

            // Valid query
            presenter.dispatch(QueryChanged("abc"))
            awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "abc")

            testScheduler.advanceTimeBy(300)

            fakeSearchRepository.setSearchResult(createDiscoverShowList())

            awaitItem() shouldBe SearchResultAvailable(
                isUpdating = false,
                query = "abc",
            )

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
            awaitItem() shouldBe InitialSearchState()

            presenter.dispatch(QueryChanged(""))
            expectNoEvents()

            presenter.dispatch(QueryChanged("ab"))
            expectNoEvents()

            testScheduler.advanceTimeBy(300)
            awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
            awaitItem() shouldBe ShowContentAvailable(isUpdating = false)

            presenter.dispatch(QueryChanged("test"))

            fakeSearchRepository.setSearchResult(createDiscoverShowList())
            awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
            awaitItem() shouldBe ShowContentAvailable(isUpdating = false)

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
            awaitItem() shouldBe InitialSearchState()
            awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
            awaitItem() shouldBe ShowContentAvailable(isUpdating = false)

            presenter.dispatch(QueryChanged("test"))

            awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "test")

            testScheduler.advanceTimeBy(300)

            fakeSearchRepository.setSearchResult(emptyList())

            awaitItem() shouldBe SearchResultAvailable("test", results = persistentListOf())
        }
    }

    @Test
    fun `should handle sequence of empty and non-empty results`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe InitialSearchState()
            awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
            awaitItem() shouldBe ShowContentAvailable(isUpdating = false)

            presenter.dispatch(QueryChanged("empty"))
            awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "empty")

            testScheduler.advanceTimeBy(300)

            fakeSearchRepository.setSearchResult(emptyList())
            awaitItem() shouldBe SearchResultAvailable("empty", results = persistentListOf())

            presenter.dispatch(QueryChanged("test"))
            awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "test", results = persistentListOf())

            testScheduler.advanceTimeBy(300)

            fakeSearchRepository.setSearchResult(createDiscoverShowList())

            awaitItem() shouldBe SearchResultAvailable(
                isUpdating = false,
                query = "test",
            )

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

            fakeSearchRepository.setSearchResult(emptyList())

            awaitItem() shouldBe SearchResultAvailable(
                isUpdating = false,
                query = "none",
                results = firstResult.results,
            )
            awaitItem() shouldBe EmptySearchResult("none")
        }
    }

    @Test
    fun `should update state when on clear query and show content is available`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe InitialSearchState()
            setList(createGenreShowList())

            awaitItem() shouldBe ShowContentAvailable(isUpdating = true)
            awaitItem() shouldBe ShowContentAvailable(
                isUpdating = false,
                genres = genreList(),
            )

            presenter.dispatch(QueryChanged("test"))

            awaitItem() shouldBe SearchResultAvailable(isUpdating = true, query = "test")

            testScheduler.advanceTimeBy(300)

            fakeSearchRepository.setSearchResult(createDiscoverShowList())

            awaitItem() shouldBe SearchResultAvailable(
                isUpdating = false,
                query = "test",
            )

            awaitItem() shouldBe SearchResultAvailable(
                isUpdating = false,
                query = "test",
                results = uiModelList(),
            )

            presenter.dispatch(ClearQuery)

            setList(createGenreShowList())

            awaitItem() shouldBe ShowContentAvailable(isUpdating = true)

            awaitItem() shouldBe ShowContentAvailable(
                isUpdating = false,
                genres = genreList(),
            )
        }
    }

    private fun buildPresenter(
        lifecycle: LifecycleRegistry = LifecycleRegistry(),
    ): SearchShowsPresenter = DefaultSearchShowsPresenter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        onNavigateToShowDetails = {},
        onNavigateToGenre = {},
        searchRepository = fakeSearchRepository,
        genreRepository = genreRepository,
        mapper = Mapper(
            formatterUtil = FakeFormatterUtil(),
        ),
    )

    private suspend fun TestScope.setList(list: List<ShowGenresEntity>) {
        genreRepository.setGenreResult(list)

        testScheduler.advanceUntilIdle()
    }

    private fun createDiscoverShowList(size: Int = LIST_SIZE) = List(size) {
        ShowEntity(
            id = 84958,
            title = "Loki",
            posterPath = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
            inLibrary = false,
            overview = null,
            status = null,
            voteAverage = null,
        )
    }
        .toImmutableList()

    private fun uiModelList(size: Int = LIST_SIZE) = createDiscoverShowList(size)
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

    private fun createGenreShowList(size: Int = LIST_SIZE) = List(size) {
        ShowGenresEntity(
            id = 84958,
            name = "Horror",
            posterUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        )
    }
        .toImmutableList()

    private fun genreList(size: Int = LIST_SIZE) = List(size) {
        ShowGenre(
            id = 84958,
            name = "Horror",
            posterUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        )
    }.toImmutableList()

    companion object {
        const val LIST_SIZE = 5
    }
}
