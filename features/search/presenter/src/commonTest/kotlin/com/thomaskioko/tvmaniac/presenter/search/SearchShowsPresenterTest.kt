package com.thomaskioko.tvmaniac.presenter.search

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.domain.genre.FetchGenreContentInteractor
import com.thomaskioko.tvmaniac.genre.FakeGenreRepository
import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
import com.thomaskioko.tvmaniac.genre.model.GenreWithShowsEntity
import com.thomaskioko.tvmaniac.genre.model.TraktGenreEntity
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.search.nav.SearchNavigator
import com.thomaskioko.tvmaniac.search.presenter.CategoryChanged
import com.thomaskioko.tvmaniac.search.presenter.ClearQuery
import com.thomaskioko.tvmaniac.search.presenter.Mapper
import com.thomaskioko.tvmaniac.search.presenter.QueryChanged
import com.thomaskioko.tvmaniac.search.presenter.SearchShowState
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import com.thomaskioko.tvmaniac.search.presenter.model.CategoryItem
import com.thomaskioko.tvmaniac.search.presenter.model.GenreRowModel
import com.thomaskioko.tvmaniac.search.presenter.model.ShowItem
import com.thomaskioko.tvmaniac.search.testing.FakeSearchRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.shouldBe
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

internal class SearchShowsPresenterTest {
    private val testDispatcher = StandardTestDispatcher()
    private val fakeSearchRepository = FakeSearchRepository()
    private val genreRepository = FakeGenreRepository()
    private val fakeLocalizer = FakeLocalizer()
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
            skipItems(1)
            awaitItem() shouldBe settledState()
        }
    }

    @Test
    fun `should return initial state when query is blank`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty
            skipItems(1)
            awaitItem() shouldBe settledState()

            presenter.dispatch(QueryChanged(""))
            expectNoEvents()
        }
    }

    @Test
    fun `should return empty state when genre rows are empty`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty

            setGenreRows(emptyList())
            skipItems(1)

            awaitItem() shouldBe settledState()
        }
    }

    @Test
    fun `should return genre rows when genre content is available`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty

            setGenreRows(createGenreWithShowsList())
            skipItems(1)

            awaitItem() shouldBe settledState(
                genreRows = genreRowModelList(),
            )
        }
    }

    @Test
    fun `should not trigger search given query below minimum length`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty

            presenter.dispatch(QueryChanged("t"))
            skipItems(1) // Skip init settled state

            awaitItem() shouldBe settledState(query = "t")
        }
    }

    @Test
    fun `should return empty state when query is valid and results are empty`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty

            setGenreRows(createGenreWithShowsList())
            skipItems(1)
            awaitItem() shouldBe settledState(
                genreRows = genreRowModelList(),
            )

            presenter.dispatch(QueryChanged("test"))
            fakeSearchRepository.setSearchResult(emptyList())
            skipItems(1) // Skip immediate query state update with isUpdating=true

            awaitItem() shouldBe settledState(
                query = "test",
                genreRows = genreRowModelList(),
            )
        }
    }

    @Test
    fun `should return loading state with previous results when query changes`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty

            setGenreRows(createGenreWithShowsList())
            skipItems(1)

            awaitItem() shouldBe settledState(
                genreRows = genreRowModelList(),
            )

            presenter.dispatch(QueryChanged("test"))
            skipItems(1) // Skip immediate query state update with isUpdating=true

            awaitItem() shouldBe settledState(
                query = "test",
                genreRows = genreRowModelList(),
            )

            fakeSearchRepository.setSearchResult(createDiscoverShowList())

            awaitItem() shouldBe settledState(
                query = "test",
                genreRows = genreRowModelList(),
                searchResults = uiModelList(),
            )
        }
    }

    @Test
    fun `should handle transition from valid to short query to empty query`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty
            setGenreRows(emptyList())
            skipItems(1)

            awaitItem() shouldBe settledState()

            presenter.dispatch(QueryChanged("test"))
            fakeSearchRepository.setSearchResult(emptyList())
            skipItems(1) // Skip immediate query state update with isUpdating=true

            awaitItem() shouldBe settledState(query = "test")

            presenter.dispatch(QueryChanged("t"))

            awaitItem() shouldBe settledState(query = "t")

            presenter.dispatch(QueryChanged(""))

            awaitItem() shouldBe settledState(
                query = "",
                searchResults = persistentListOf(),
            )
        }
    }

    @Test
    fun `should handle transition from short to valid query`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty
            skipItems(1)

            presenter.dispatch(QueryChanged("a"))

            skipItems(1) // Skip init settled state
            awaitItem() shouldBe settledState(query = "a")

            presenter.dispatch(QueryChanged("abc"))
            skipItems(1) // Skip immediate query state update with isUpdating=true

            awaitItem() shouldBe settledState(query = "abc")

            fakeSearchRepository.setSearchResult(createDiscoverShowList())

            awaitItem() shouldBe settledState(
                query = "abc",
                searchResults = uiModelList(),
            )
        }
    }

    @Test
    fun `should handle empty short and valid query transitions correctly`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty
            skipItems(1)

            presenter.dispatch(QueryChanged(""))
            expectNoEvents()

            presenter.dispatch(QueryChanged("a"))

            skipItems(1) // Skip init settled state
            awaitItem() shouldBe settledState(query = "a")

            presenter.dispatch(QueryChanged("test"))
            skipItems(1) // Skip immediate query state update with isUpdating=true

            awaitItem() shouldBe settledState(query = "test")

            fakeSearchRepository.setSearchResult(createDiscoverShowList())

            awaitItem() shouldBe settledState(
                query = "test",
                searchResults = uiModelList(),
            )

            presenter.dispatch(QueryChanged(""))

            awaitItem() shouldBe settledState(
                query = "",
                searchResults = persistentListOf(),
            )
        }
    }

    @Test
    fun `should update state when on clear query and genre rows are available`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty
            setGenreRows(createGenreWithShowsList())
            skipItems(1)

            awaitItem() shouldBe settledState(
                genreRows = genreRowModelList(),
            )

            presenter.dispatch(QueryChanged("test"))
            skipItems(1) // Skip immediate query state update with isUpdating=true

            awaitItem() shouldBe settledState(
                query = "test",
                genreRows = genreRowModelList(),
            )

            fakeSearchRepository.setSearchResult(createDiscoverShowList())

            awaitItem() shouldBe settledState(
                query = "test",
                genreRows = genreRowModelList(),
                searchResults = uiModelList(),
            )

            presenter.dispatch(ClearQuery)

            awaitItem() shouldBe settledState(
                query = "",
                searchResults = persistentListOf(),
                genreRows = genreRowModelList(),
            )
        }
    }

    @Test
    fun `should update selected category when category is changed`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty

            setGenreRows(createGenreWithShowsList())
            skipItems(1)

            awaitItem() shouldBe settledState(
                genreRows = genreRowModelList(),
            )

            genreRepository.setGenreShowCategory(GenreShowCategory.TRENDING)
            skipItems(2)

            awaitItem() shouldBe settledState(
                genreRows = genreRowModelList(),
                selectedCategory = GenreShowCategory.TRENDING,
            )
        }
    }

    @Test
    fun `should display category-specific genre rows when filter changes`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe SearchShowState.Empty

            setGenreRows(createGenreWithShowsForCategory("Loki", 84958))
            skipItems(1)

            awaitItem() shouldBe settledState(
                genreRows = toExpectedGenreRowModels("Loki", 84958),
            )

            presenter.dispatch(CategoryChanged(GenreShowCategory.TRENDING))
            setGenreRows(createGenreWithShowsForCategory("Breaking Bad", 1388))
            skipItems(2)

            awaitItem() shouldBe settledState(
                selectedCategory = GenreShowCategory.TRENDING,
                genreRows = toExpectedGenreRowModels("Breaking Bad", 1388),
            )
        }
    }

    private fun settledState(
        query: String = "",
        genreRows: kotlinx.collections.immutable.ImmutableList<GenreRowModel> = persistentListOf(),
        searchResults: kotlinx.collections.immutable.ImmutableList<ShowItem> = persistentListOf(),
        selectedCategory: GenreShowCategory = GenreShowCategory.POPULAR,
    ) = SearchShowState(
        query = query,
        isRefreshing = false,
        genreRows = genreRows,
        searchResults = searchResults,
        selectedCategory = selectedCategory,
        categoryTitle = expectedCategoryTitle,
        categories = expectedCategories,
    )

    private fun buildPresenter(
        lifecycle: LifecycleRegistry = LifecycleRegistry(),
    ): SearchShowsPresenter = SearchShowsPresenter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        navigator = object : SearchNavigator {
            override fun showDetails(traktId: Long) {}
            override fun showGenre(genreId: Long) {}
            override fun goBack() {}
        },
        searchRepository = fakeSearchRepository,
        genreRepository = genreRepository,
        fetchGenreContentInteractor = FetchGenreContentInteractor(
            repository = genreRepository,
            dispatchers = AppCoroutineDispatchers(
                io = testDispatcher,
                computation = testDispatcher,
                databaseWrite = testDispatcher,
                databaseRead = testDispatcher,
                main = testDispatcher,
            ),
        ),
        errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
        logger = FakeLogger(),
        mapper = Mapper(
            formatterUtil = FakeFormatterUtil(),
            localizer = fakeLocalizer,
        ),
    )

    private suspend fun setGenreRows(list: List<GenreWithShowsEntity>) {
        genreRepository.setGenreWithShowsResult(list)
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

    private fun createGenreWithShowsList() = listOf(
        GenreWithShowsEntity(
            genre = TraktGenreEntity(slug = "horror", name = "Horror"),
            shows = List(LIST_SIZE) {
                ShowEntity(
                    traktId = 84958,
                    tmdbId = 84958,
                    title = "Loki",
                    posterPath = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                    inLibrary = false,
                )
            },
        ),
    )

    private fun genreRowModelList() = listOf(
        GenreRowModel(
            slug = "horror",
            name = "Horror",
            subtitle = fakeLocalizer.getString(StringResourceKey.GenreDescHorror),
            shows = List(LIST_SIZE) {
                ShowItem(
                    tmdbId = 84958,
                    traktId = 84958,
                    title = "Loki",
                    posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
                    inLibrary = false,
                )
            }.toImmutableList(),
        ),
    ).toImmutableList()

    private val expectedCategoryTitle = fakeLocalizer.getString(
        StringResourceKey.LabelGenreCategoryTitle,
    )
    private val expectedCategories = GenreShowCategory.entries.map { category ->
        val key = when (category) {
            GenreShowCategory.POPULAR -> StringResourceKey.LabelGenreCategoryPopular
            GenreShowCategory.TRENDING -> StringResourceKey.LabelGenreCategoryTrending
            GenreShowCategory.TOP_RATED -> StringResourceKey.LabelGenreCategoryTopRated
            GenreShowCategory.MOST_WATCHED -> StringResourceKey.LabelGenreCategoryMostWatched
        }
        CategoryItem(category = category, label = fakeLocalizer.getString(key))
    }.toImmutableList()

    private fun createGenreWithShowsForCategory(title: String, id: Long) = listOf(
        GenreWithShowsEntity(
            genre = TraktGenreEntity(slug = "horror", name = "Horror"),
            shows = List(LIST_SIZE) {
                ShowEntity(
                    traktId = id,
                    tmdbId = id,
                    title = title,
                    posterPath = "/poster.jpg",
                    inLibrary = false,
                )
            },
        ),
    )

    private fun toExpectedGenreRowModels(title: String, id: Long) = listOf(
        GenreRowModel(
            slug = "horror",
            name = "Horror",
            subtitle = fakeLocalizer.getString(StringResourceKey.GenreDescHorror),
            shows = List(LIST_SIZE) {
                ShowItem(
                    tmdbId = id,
                    traktId = id,
                    title = title,
                    posterImageUrl = "/poster.jpg",
                    inLibrary = false,
                )
            }.toImmutableList(),
        ),
    ).toImmutableList()

    companion object {
        const val LIST_SIZE = 5
    }
}
