package com.thomaskioko.tvmaniac.presenter.search

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.genre.FakeGenreRepository
import com.thomaskioko.tvmaniac.genre.ShowGenresEntity
import com.thomaskioko.tvmaniac.search.presenter.ClearQuery
import com.thomaskioko.tvmaniac.search.presenter.DefaultSearchShowsPresenter
import com.thomaskioko.tvmaniac.search.presenter.Mapper
import com.thomaskioko.tvmaniac.search.presenter.QueryChanged
import com.thomaskioko.tvmaniac.search.presenter.SearchShowState
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
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
            awaitItem() shouldBe SearchShowState.Empty
            awaitItem() shouldBe SearchShowState(isUpdating = true)
            awaitItem() shouldBe SearchShowState(isUpdating = false)
        }
    }

    @Test
    fun `should return initial state when query is blank`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty
            awaitItem() shouldBe SearchShowState(isUpdating = true)
            awaitItem() shouldBe SearchShowState(isUpdating = false)

            presenter.dispatch(QueryChanged(""))
            expectNoEvents()
        }
    }

    @Test
    fun `should return empty state when show content is empty`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty
            awaitItem() shouldBe SearchShowState(isUpdating = true)

            setList(emptyList())

            awaitItem() shouldBe SearchShowState(isUpdating = false)
        }
    }

    @Test
    fun `should return show content when show content is not empty`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty
            awaitItem() shouldBe SearchShowState(isUpdating = true)
            awaitItem() shouldBe SearchShowState(isUpdating = false)

            setList(createGenreShowList())

            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                genres = genreList(),
            )
        }
    }

    @Test
    fun `should not perform search when query is less than 3 characters`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty
            awaitItem() shouldBe SearchShowState(isUpdating = true)
            awaitItem() shouldBe SearchShowState(isUpdating = false)

            presenter.dispatch(QueryChanged("te"))
            testScheduler.advanceTimeBy(400)
            expectNoEvents()
        }
    }

    @Test
    fun `should return empty state when query is valid and results are empty`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty
            awaitItem() shouldBe SearchShowState(isUpdating = true)
            awaitItem() shouldBe SearchShowState(isUpdating = false)

            setList(createGenreShowList())
            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                genres = genreList(),
            )

            presenter.dispatch(QueryChanged("test"))

            testScheduler.advanceTimeBy(350)

            fakeSearchRepository.setSearchResult(emptyList())
            testScheduler.advanceUntilIdle()

            awaitItem() shouldBe SearchShowState(
                isUpdating = true,
                query = "test",
                genres = genreList(),
            )

            awaitItem() shouldBe SearchShowState(
                query = "test",
                searchResults = persistentListOf(),
                genres = genreList(),
            )
        }
    }

    @Test
    fun `should return loading state with previous results when query changes`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty
            awaitItem() shouldBe SearchShowState(isUpdating = true)
            awaitItem() shouldBe SearchShowState(isUpdating = false)

            setList(createGenreShowList())

            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                genres = genreList(),
            )

            // Dispatch first query change
            presenter.dispatch(QueryChanged("test"))

            testScheduler.advanceTimeBy(350)

            awaitItem() shouldBe SearchShowState(
                isUpdating = true,
                query = "test",
                genres = genreList(),
            )

            fakeSearchRepository.setSearchResult(createDiscoverShowList())
            testScheduler.advanceUntilIdle()

            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                query = "test",
                genres = genreList(),
            )

            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                query = "test",
                searchResults = uiModelList(),
                genres = genreList(),
            )
        }
    }

    @Test
    fun `should handle transition from valid to short query to empty query`() = runTest {
        presenter.state.test {
            setList(emptyList())

            awaitItem() shouldBe SearchShowState.Empty
            awaitItem() shouldBe SearchShowState(isUpdating = true)

            awaitItem() shouldBe SearchShowState(isUpdating = false)

            presenter.dispatch(QueryChanged("test"))
            testScheduler.advanceTimeBy(350)

            awaitItem() shouldBe SearchShowState(isUpdating = true, query = "test")

            fakeSearchRepository.setSearchResult(emptyList())
            testScheduler.advanceUntilIdle()

            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                query = "test",
            )

            presenter.dispatch(QueryChanged("te"))
            testScheduler.advanceTimeBy(350)
            expectNoEvents()

            presenter.dispatch(QueryChanged(""))
            testScheduler.advanceUntilIdle()

            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                query = "",
                searchResults = persistentListOf(),
            )
        }
    }

    @Test
    fun `should handle transition from short to valid query`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty
            awaitItem() shouldBe SearchShowState(isUpdating = true)
            awaitItem() shouldBe SearchShowState(isUpdating = false)

            presenter.dispatch(QueryChanged("ab"))
            testScheduler.advanceTimeBy(350)
            expectNoEvents()

            // Valid query
            presenter.dispatch(QueryChanged("abc"))
            testScheduler.advanceTimeBy(350)

            awaitItem() shouldBe SearchShowState(isUpdating = true, query = "abc")

            fakeSearchRepository.setSearchResult(createDiscoverShowList())
            testScheduler.advanceUntilIdle()

            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                query = "abc",
            )

            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                query = "abc",
                searchResults = uiModelList(),
            )
        }
    }

    @Test
    fun `should handle empty short and valid query transitions correctly`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty

            presenter.dispatch(QueryChanged(""))
            expectNoEvents()

            presenter.dispatch(QueryChanged("ab"))
            testScheduler.advanceTimeBy(350)

            awaitItem() shouldBe SearchShowState(isUpdating = true)
            awaitItem() shouldBe SearchShowState(isUpdating = false)

            presenter.dispatch(QueryChanged("test"))
            testScheduler.advanceTimeBy(350)

            awaitItem() shouldBe SearchShowState(isUpdating = true, query = "test")

            fakeSearchRepository.setSearchResult(createDiscoverShowList())
            testScheduler.advanceUntilIdle()

            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                query = "test",
            )

            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                query = "test",
                searchResults = uiModelList(),
            )

            presenter.dispatch(QueryChanged(""))
            testScheduler.advanceUntilIdle()

            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                query = "",
                searchResults = persistentListOf(),
            )
        }
    }

    @Test
    fun `should return empty state when query is valid and search returns empty results`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty
            awaitItem() shouldBe SearchShowState(isUpdating = true)
            awaitItem() shouldBe SearchShowState(isUpdating = false)

            presenter.dispatch(QueryChanged("test"))
            testScheduler.advanceTimeBy(350)

            awaitItem() shouldBe SearchShowState(isUpdating = true, query = "test")

            fakeSearchRepository.setSearchResult(emptyList())
            testScheduler.advanceUntilIdle()

            awaitItem() shouldBe SearchShowState(query = "test", searchResults = persistentListOf())
        }
    }

    @Test
    fun `should handle sequence of empty and non-empty results`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty
            awaitItem() shouldBe SearchShowState(isUpdating = true)
            awaitItem() shouldBe SearchShowState(isUpdating = false)

            presenter.dispatch(QueryChanged("empty"))
            testScheduler.advanceTimeBy(350)

            awaitItem() shouldBe SearchShowState(isUpdating = true, query = "empty")

            fakeSearchRepository.setSearchResult(emptyList())
            testScheduler.advanceUntilIdle()

            awaitItem() shouldBe SearchShowState(query = "empty", searchResults = persistentListOf())

            presenter.dispatch(QueryChanged("test"))
            testScheduler.advanceTimeBy(350)

            awaitItem() shouldBe SearchShowState(
                isUpdating = true,
                query = "test",
                searchResults = persistentListOf(),
            )

            fakeSearchRepository.setSearchResult(createDiscoverShowList())
            testScheduler.advanceUntilIdle()

            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                query = "test",
            )

            val firstResult = awaitItem()
            firstResult shouldBe SearchShowState(
                isUpdating = false,
                query = "test",
                searchResults = uiModelList(),
            )

            presenter.dispatch(QueryChanged("none"))
            testScheduler.advanceTimeBy(350)

            awaitItem() shouldBe SearchShowState(
                isUpdating = true,
                query = "none",
                searchResults = firstResult.searchResults,
            )

            fakeSearchRepository.setSearchResult(emptyList())
            testScheduler.advanceUntilIdle()

            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                query = "none",
                searchResults = firstResult.searchResults,
            )

            awaitItem() shouldBe SearchShowState(
                query = "none",
                searchResults = persistentListOf(),
            )
        }
    }

    @Test
    fun `should update state when on clear query and show content is available`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty
            setList(createGenreShowList())

            awaitItem() shouldBe SearchShowState(isUpdating = true)
            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                genres = genreList(),
            )

            presenter.dispatch(QueryChanged("test"))
            testScheduler.advanceTimeBy(350)

            awaitItem() shouldBe SearchShowState(
                isUpdating = true,
                query = "test",
                genres = genreList(),
            )

            fakeSearchRepository.setSearchResult(createDiscoverShowList())
            testScheduler.advanceUntilIdle()

            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                query = "test",
                genres = genreList(),
            )

            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                query = "test",
                searchResults = uiModelList(),
                genres = genreList(),
            )

            presenter.dispatch(ClearQuery)
            testScheduler.advanceUntilIdle()

            awaitItem() shouldBe SearchShowState(
                isUpdating = false,
                query = "",
                searchResults = persistentListOf(),
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
            traktId = 84958,
            tmdbId = 84958,
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
                tmdbId = it.tmdbId,
                traktId = it.traktId,
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
