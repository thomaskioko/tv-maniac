package com.thomaskioko.tvmaniac.search.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.search.api.model.RemoteSearchShow
import com.thomaskioko.tvmaniac.search.testing.FakeSearchRemoteDataSource
import com.thomaskioko.tvmaniac.shows.implementation.DefaultTvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.model.CreditsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.GenreResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.NetworksResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.VideosResponse
import com.thomaskioko.tvmaniac.tmdb.testing.FakeTmdbShowDetailsNetworkDataSource
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
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class SearchShowStoreTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var tvShowsDao: DefaultTvShowsDao
    private lateinit var searchDao: DefaultSearchDao
    private val tmdbSource = FakeTmdbShowDetailsNetworkDataSource()
    private val requestManager = FakeRequestManagerRepository(initialRequestValid = false)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        tvShowsDao = DefaultTvShowsDao(database = database, dispatchers = dispatchers)
        searchDao = DefaultSearchDao(database = database, showIdResolver = showIdResolver, dispatchers = dispatchers)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should persist rows in provider order given search succeeds`() = runTest(testDispatcher) {
        val source = FakeSearchRemoteDataSource(provider = SyncProviderSource.TRAKT)
        source.setSearchResult(
            "breaking",
            listOf(
                buildRemoteShow(providerShowId = "1", tmdbId = 100L, title = "First"),
                buildRemoteShow(providerShowId = "2", tmdbId = 200L, title = "Second"),
                buildRemoteShow(providerShowId = "3", tmdbId = 300L, title = "Third"),
            ),
        )
        val store = buildStore(source)

        fetch(store, key = SearchKey(SyncProviderSource.TRAKT, "breaking"))

        val results = database.searchResultsQueries.resultsByQuery("trakt:breaking").executeAsList()
        results.map { it.name } shouldBe listOf("First", "Second", "Third")
    }

    @Test
    fun `should replace only the refetched key given another key already has results`() = runTest(testDispatcher) {
        val source = FakeSearchRemoteDataSource(provider = SyncProviderSource.TRAKT)
        source.setSearchResult("one", listOf(buildRemoteShow(providerShowId = "1", tmdbId = 100L, title = "One")))
        source.setSearchResult("two", listOf(buildRemoteShow(providerShowId = "2", tmdbId = 200L, title = "Two")))
        val store = buildStore(source)

        fetch(store, key = SearchKey(SyncProviderSource.TRAKT, "one"))
        fetch(store, key = SearchKey(SyncProviderSource.TRAKT, "two"))

        source.setSearchResult("one", listOf(buildRemoteShow(providerShowId = "3", tmdbId = 300L, title = "One Updated")))
        fetch(store, key = SearchKey(SyncProviderSource.TRAKT, "one"))

        val one = database.searchResultsQueries.resultsByQuery("trakt:one").executeAsList()
        val two = database.searchResultsQueries.resultsByQuery("trakt:two").executeAsList()
        one.map { it.name } shouldBe listOf("One Updated")
        two.map { it.name } shouldBe listOf("Two")
    }

    @Test
    fun `should call tmdb only for ids missing a cached poster`() = runTest(testDispatcher) {
        insertShow(tmdbId = 100L, name = "Cached", posterPath = "/existing.jpg")
        tmdbSource.setDefaultShowDetails(
            ApiResponse.Success(buildTmdbDetails(id = 100, posterPath = "/should-not-be-used.jpg")),
        )
        tmdbSource.setShowDetails(
            id = 200L,
            response = ApiResponse.Success(buildTmdbDetails(id = 200, posterPath = "/loaded-from-tmdb.jpg")),
        )
        val source = FakeSearchRemoteDataSource(provider = SyncProviderSource.TRAKT)
        source.setSearchResult(
            "shows",
            listOf(
                buildRemoteShow(providerShowId = "1", tmdbId = 100L, title = "Cached"),
                buildRemoteShow(providerShowId = "2", tmdbId = 200L, title = "New"),
            ),
        )
        val store = buildStore(source)

        fetch(store, key = SearchKey(SyncProviderSource.TRAKT, "shows"))

        database.tvShowQueries.tvshowByTmdbId(Id<TmdbId>(100L)).executeAsOne().poster_path shouldBe "/existing.jpg"
        database.tvShowQueries.tvshowByTmdbId(Id<TmdbId>(200L)).executeAsOne().poster_path shouldBe ""
    }

    @Test
    fun `should preserve season numbers given the show already has them`() = runTest(testDispatcher) {
        insertShow(tmdbId = 100L, name = "Existing", posterPath = "/existing.jpg", seasonNumbers = "1,2,3")
        val source = FakeSearchRemoteDataSource(provider = SyncProviderSource.TRAKT)
        source.setSearchResult("existing", listOf(buildRemoteShow(providerShowId = "1", tmdbId = 100L, title = "Existing")))
        val store = buildStore(source)

        fetch(store, key = SearchKey(SyncProviderSource.TRAKT, "existing"))

        database.tvShowQueries.tvshowByTmdbId(Id<TmdbId>(100L)).executeAsOne().season_numbers shouldBe "1,2,3"
    }

    @Test
    fun `should write a last request entry given results are fetched`() = runTest(testDispatcher) {
        val source = FakeSearchRemoteDataSource(provider = SyncProviderSource.TRAKT)
        source.setSearchResult("query", listOf(buildRemoteShow(providerShowId = "1", tmdbId = 100L, title = "Show")))
        val store = buildStore(source)

        fetch(store, key = SearchKey(SyncProviderSource.TRAKT, "query"))

        requestManager.upsertCalled shouldBe true
    }

    @Test
    fun `should write a simkl external id and no trakt id given the active source is simkl`() = runTest(testDispatcher) {
        val source = FakeSearchRemoteDataSource(provider = SyncProviderSource.SIMKL)
        source.setSearchResult("query", listOf(buildRemoteShow(providerShowId = "9999", tmdbId = 100L, title = "Show")))
        val store = buildStore(source)

        fetch(store, key = SearchKey(SyncProviderSource.SIMKL, "query"))

        val showId = database.tvShowQueries.getShowIdByTmdbId(Id(100L)).executeAsOne()
        database.tvshowExternalIdQueries.externalIdForShow(showId, Provider.SIMKL).executeAsOneOrNull() shouldBe "9999"
        database.tvshowExternalIdQueries.externalIdForShow(showId, Provider.TRAKT).executeAsOneOrNull() shouldBe null
    }

    private suspend fun fetch(store: SearchShowStore, key: SearchKey) {
        store.stream(StoreReadRequest.fresh(key)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun buildStore(source: FakeSearchRemoteDataSource): SearchShowStore = SearchShowStore(
        searchDao = searchDao,
        tvShowsDao = tvShowsDao,
        searchRemoteDataSources = setOf(source),
        tmdbDetailsDataSource = tmdbSource,
        formatterUtil = FakeFormatterUtil(),
        dateTimeProvider = FakeDateTimeProvider(),
        requestManagerRepository = requestManager,
        databaseTransactionRunner = StoreTestTransactionRunner,
        dispatchers = dispatchers,
    )

    private fun insertShow(tmdbId: Long, name: String, posterPath: String?, seasonNumbers: String? = null) {
        database.tvShowQueries.upsert(
            tmdb_id = Id(tmdbId),
            name = name,
            overview = "Overview",
            language = "en",
            year = "2008",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Ended",
            episode_numbers = null,
            season_numbers = seasonNumbers,
            poster_path = posterPath,
            backdrop_path = null,
        )
    }

    private fun buildRemoteShow(providerShowId: String, tmdbId: Long, title: String): RemoteSearchShow = RemoteSearchShow(
        providerShowId = providerShowId,
        tmdbId = tmdbId,
        title = title,
        year = 2008,
        overview = "Overview",
        status = "Ended",
        episodeCount = 62,
        rating = 9.0,
        votes = 1000L,
        genres = listOf("drama"),
        language = "en",
        score = 100.0,
    )

    private fun buildTmdbDetails(id: Int, posterPath: String?): TmdbShowDetailsResponse = TmdbShowDetailsResponse(
        adult = false,
        backdropPath = null,
        episodeRunTime = arrayListOf(),
        firstAirDate = "2008-01-20",
        genres = arrayListOf(GenreResponse(id = 1, name = "Drama")),
        id = id,
        lastAirDate = null,
        lastEpisodeToAir = null,
        name = "Show $id",
        nextEpisodeToAir = null,
        networks = arrayListOf(NetworksResponse(id = 1, name = "AMC")),
        numberOfEpisodes = 62,
        numberOfSeasons = 5,
        overview = "TMDB overview",
        popularity = 80.0,
        posterPath = posterPath,
        seasons = arrayListOf(),
        status = "Ended",
        voteAverage = 9.5,
        voteCount = 5000,
        videos = VideosResponse(results = arrayListOf()),
        credits = CreditsResponse(cast = arrayListOf()),
        originalLanguage = "en",
    )
}

private object StoreTestTransactionRunner : DatabaseTransactionRunner {
    override fun <T> invoke(block: () -> T): T = block()
}
