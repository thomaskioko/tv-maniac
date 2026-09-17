package com.thomaskioko.tvmaniac.genre

import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
import com.thomaskioko.tvmaniac.genre.model.GenreShowsStoreKey
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.shows.implementation.DefaultTvShowsDao
import com.thomaskioko.tvmaniac.trakt.api.model.ShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class GenreShowsStoreTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var tvShowsDao: DefaultTvShowsDao
    private lateinit var traktGenreDao: DefaultTraktGenreDao
    private lateinit var requestManager: FakeRequestManagerRepository
    private lateinit var traktSource: FakeTraktShowsRemoteDataSource
    private lateinit var tmdbSource: FakeTmdbDetailsSource
    private lateinit var store: GenreShowsStore

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        tvShowsDao = DefaultTvShowsDao(database = database, dispatchers = dispatchers)
        traktGenreDao = DefaultTraktGenreDao(database = database, showIdResolver = showIdResolver, dispatchers = dispatchers)
        requestManager = FakeRequestManagerRepository(initialRequestValid = false)
        traktSource = FakeTraktShowsRemoteDataSource()
        tmdbSource = FakeTmdbDetailsSource()
        store = buildStore()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should stamp the fetched page on every persisted row`() = runTest(testDispatcher) {
        traktSource.setPopularShows(popularShowsResponse(SHOW_TMDB_ID))
        tmdbSource.setShowDetails(SHOW_TMDB_ID, ApiResponse.Success(buildTmdbDetails(SHOW_TMDB_ID.toInt(), "Show 1")))

        fetch(GenreShowsStoreKey(genreSlug = SLUG, category = GenreShowCategory.POPULAR, page = 1L))

        val rows = loadPage(page = 0)
        rows.map { it.tmdbId } shouldBe listOf(SHOW_TMDB_ID)
        rows.single().page shouldBe 1L
    }

    @Test
    fun `should delete every row of the genre and category given page 1 is written`() = runTest(testDispatcher) {
        traktSource.setPopularShows(popularShowsResponse(SHOW_TMDB_ID))
        tmdbSource.setShowDetails(SHOW_TMDB_ID, ApiResponse.Success(buildTmdbDetails(SHOW_TMDB_ID.toInt(), "Show 1")))
        fetch(GenreShowsStoreKey(genreSlug = SLUG, category = GenreShowCategory.POPULAR, page = 2L))
        traktGenreDao.pageExists(SLUG, GenreShowCategory.POPULAR.name, 2L) shouldBe true

        traktSource.setPopularShows(popularShowsResponse(OTHER_TMDB_ID))
        tmdbSource.setShowDetails(OTHER_TMDB_ID, ApiResponse.Success(buildTmdbDetails(OTHER_TMDB_ID.toInt(), "Show 2")))
        fetch(GenreShowsStoreKey(genreSlug = SLUG, category = GenreShowCategory.POPULAR, page = 1L))

        traktGenreDao.pageExists(SLUG, GenreShowCategory.POPULAR.name, 2L) shouldBe false
        traktGenreDao.pageExists(SLUG, GenreShowCategory.POPULAR.name, 1L) shouldBe true
    }

    @Test
    fun `should keep page 1 rows given page 2 is written`() = runTest(testDispatcher) {
        traktSource.setPopularShows(popularShowsResponse(SHOW_TMDB_ID))
        tmdbSource.setShowDetails(SHOW_TMDB_ID, ApiResponse.Success(buildTmdbDetails(SHOW_TMDB_ID.toInt(), "Show 1")))
        fetch(GenreShowsStoreKey(genreSlug = SLUG, category = GenreShowCategory.POPULAR, page = 1L))

        traktSource.setPopularShows(popularShowsResponse(OTHER_TMDB_ID))
        tmdbSource.setShowDetails(OTHER_TMDB_ID, ApiResponse.Success(buildTmdbDetails(OTHER_TMDB_ID.toInt(), "Show 2")))
        fetch(GenreShowsStoreKey(genreSlug = SLUG, category = GenreShowCategory.POPULAR, page = 2L))

        traktGenreDao.pageExists(SLUG, GenreShowCategory.POPULAR.name, 1L) shouldBe true
        traktGenreDao.pageExists(SLUG, GenreShowCategory.POPULAR.name, 2L) shouldBe true
    }

    @Test
    fun `should emit only the fetched page's rows given page 1 is already cached`() = runTest(testDispatcher) {
        traktSource.setPopularShows(popularShowsResponse(SHOW_TMDB_ID))
        tmdbSource.setShowDetails(SHOW_TMDB_ID, ApiResponse.Success(buildTmdbDetails(SHOW_TMDB_ID.toInt(), "Show 1")))
        fetch(GenreShowsStoreKey(genreSlug = SLUG, category = GenreShowCategory.POPULAR, page = 1L))

        traktSource.setPopularShows(popularShowsResponse(OTHER_TMDB_ID))
        tmdbSource.setShowDetails(OTHER_TMDB_ID, ApiResponse.Success(buildTmdbDetails(OTHER_TMDB_ID.toInt(), "Show 2")))
        val secondPage = store.fresh(GenreShowsStoreKey(genreSlug = SLUG, category = GenreShowCategory.POPULAR, page = 2L))

        secondPage.map { it.tmdbId } shouldBe listOf(OTHER_TMDB_ID)
    }

    @Test
    fun `should emit an empty list given Trakt returns no shows for the fetched page`() = runTest(testDispatcher) {
        traktSource.setPopularShows(popularShowsResponse(SHOW_TMDB_ID))
        tmdbSource.setShowDetails(SHOW_TMDB_ID, ApiResponse.Success(buildTmdbDetails(SHOW_TMDB_ID.toInt(), "Show 1")))
        fetch(GenreShowsStoreKey(genreSlug = SLUG, category = GenreShowCategory.POPULAR, page = 1L))

        traktSource.setPopularShows(ApiResponse.Success(emptyList()))
        val secondPage = store.fresh(GenreShowsStoreKey(genreSlug = SLUG, category = GenreShowCategory.POPULAR, page = 2L))

        secondPage shouldBe emptyList()
    }

    private suspend fun fetch(key: GenreShowsStoreKey) {
        store.stream(StoreReadRequest.fresh(key)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }
    }

    private suspend fun loadPage(page: Int): List<ShowEntity> {
        val source = traktGenreDao.getPagedShowsByGenreSlugAndCategory(SLUG, GenreShowCategory.POPULAR.name)
        val result = source.load(LoadParams.Refresh(key = page, loadSize = 10, placeholdersEnabled = false))
        return (result as LoadResult.Page<Int, ShowEntity>).data
    }

    private fun popularShowsResponse(tmdbId: Long): ApiResponse<List<TraktShowResponse>> = ApiResponse.Success(
        listOf(
            TraktShowResponse(
                title = "Show $tmdbId",
                ids = ShowIds(trakt = tmdbId + 1000, tmdb = tmdbId),
                overview = "Overview $tmdbId",
            ),
        ),
    )

    private fun buildStore(): GenreShowsStore = GenreShowsStore(
        traktRemoteDataSource = traktSource,
        tmdbDetailsDataSource = tmdbSource,
        traktGenreDao = traktGenreDao,
        tvShowsDao = tvShowsDao,
        requestManagerRepository = requestManager,
        formatterUtil = FakeFormatterUtil(),
        dateTimeProvider = FakeDateTimeProvider(),
        databaseTransactionRunner = ImmediateTransactionRunner,
        dispatchers = dispatchers,
    )

    private companion object {
        private const val SLUG = "drama"
        private const val SHOW_TMDB_ID = 2000L
        private const val OTHER_TMDB_ID = 3000L
    }
}
