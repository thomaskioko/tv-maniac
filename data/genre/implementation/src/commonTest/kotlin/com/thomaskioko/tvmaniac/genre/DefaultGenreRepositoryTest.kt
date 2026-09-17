package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.implementation.DefaultTvShowsDao
import com.thomaskioko.tvmaniac.tmdb.testing.FakeTmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultGenreRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var traktGenreDao: DefaultTraktGenreDao
    private lateinit var requestManager: FakeRequestManagerRepository
    private lateinit var repository: DefaultGenreRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        traktGenreDao = DefaultTraktGenreDao(database = database, showIdResolver = showIdResolver, dispatchers = dispatchers)
        requestManager = FakeRequestManagerRepository(initialRequestValid = true)
        repository = buildRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should skip a cached fresh page`() {
        addShow(tmdbId = 1L)
        traktGenreDao.upsertGenreShow(genreSlug = SLUG, showId = 1L, pageOrder = 0L, category = CATEGORY.name, page = 1L)
        requestManager.requestExpired = false

        val shouldFetch = repository.shouldFetchGenrePage(SLUG, CATEGORY, page = 1L, forceRefresh = false)

        shouldFetch shouldBe false
    }

    @Test
    fun `should fetch an expired page`() {
        addShow(tmdbId = 1L)
        traktGenreDao.upsertGenreShow(genreSlug = SLUG, showId = 1L, pageOrder = 0L, category = CATEGORY.name, page = 1L)
        requestManager.requestExpired = true

        val shouldFetch = repository.shouldFetchGenrePage(SLUG, CATEGORY, page = 1L, forceRefresh = false)

        shouldFetch shouldBe true
    }

    @Test
    fun `should fetch a missing page`() {
        requestManager.requestExpired = false

        val shouldFetch = repository.shouldFetchGenrePage(SLUG, CATEGORY, page = 2L, forceRefresh = false)

        shouldFetch shouldBe true
    }

    @Test
    fun `should fetch given force refresh is set regardless of cache state`() {
        addShow(tmdbId = 1L)
        traktGenreDao.upsertGenreShow(genreSlug = SLUG, showId = 1L, pageOrder = 0L, category = CATEGORY.name, page = 1L)
        requestManager.requestExpired = false

        val shouldFetch = repository.shouldFetchGenrePage(SLUG, CATEGORY, page = 1L, forceRefresh = true)

        shouldFetch shouldBe true
    }

    private fun addShow(tmdbId: Long) {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(tmdbId),
            name = "Show $tmdbId",
            overview = "overview",
            language = "en",
            year = "2024",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = "/$tmdbId.jpg",
            backdrop_path = null,
        )
    }

    private fun buildRepository(): DefaultGenreRepository {
        val scope = CoroutineScope(testDispatcher)
        val genreDao = DefaultGenreDao(database = database, dispatchers = dispatchers)
        val tmdbShowsSource = FakeTmdbShowsNetworkDataSource()
        val traktSource = FakeTraktShowsRemoteDataSource()
        val tmdbDetailsSource = FakeTmdbDetailsSource()
        val tvShowsDao = DefaultTvShowsDao(database = database, dispatchers = dispatchers)
        val formatterUtil = FakeFormatterUtil()

        return DefaultGenreRepository(
            store = GenreStore(
                genreDao = genreDao,
                tmdbRemoteDataSource = tmdbShowsSource,
                scope = scope,
                dispatchers = dispatchers,
            ),
            genrePosterStore = GenrePosterStore(
                genreDao = genreDao,
                tmdbRemoteDataSource = tmdbShowsSource,
                formatterUtil = formatterUtil,
                scope = scope,
                dispatchers = dispatchers,
            ),
            showsByGenreIdStore = ShowsByGenreIdStore(
                genreDao = genreDao,
                tmdbRemoteDataSource = tmdbShowsSource,
                formatterUtil = formatterUtil,
                scope = scope,
                dispatchers = dispatchers,
            ),
            genreDao = genreDao,
            traktGenresStore = TraktGenresStore(
                traktRemoteDataSource = traktSource,
                traktGenreDao = traktGenreDao,
                requestManagerRepository = requestManager,
                dispatchers = dispatchers,
            ),
            genreShowsStore = GenreShowsStore(
                traktRemoteDataSource = traktSource,
                tmdbDetailsDataSource = tmdbDetailsSource,
                traktGenreDao = traktGenreDao,
                tvShowsDao = tvShowsDao,
                requestManagerRepository = requestManager,
                formatterUtil = formatterUtil,
                dateTimeProvider = FakeDateTimeProvider(),
                databaseTransactionRunner = ImmediateTransactionRunner,
                dispatchers = dispatchers,
            ),
            traktGenreDao = traktGenreDao,
            datastoreRepository = FakeDatastoreRepository(),
            requestManagerRepository = requestManager,
            logger = FakeLogger(),
        )
    }

    private companion object {
        private const val SLUG = "drama"
        private val CATEGORY = GenreShowCategory.POPULAR
    }
}
