package com.thomaskioko.tvmaniac.data.recommendedshows.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsParams
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.implementation.DefaultTvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.CreditsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.GenreResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.NetworksResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.tmdb.api.model.VideosResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.WatchProvidersResult
import com.thomaskioko.tvmaniac.trakt.api.TimePeriod
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.ShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktGenreResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSearchResult
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowPeopleResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktVideosResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.collections.shouldHaveSize
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
internal class RecommendedShowsStoreTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var tvShowsDao: DefaultTvShowsDao
    private lateinit var recommendedShowsDao: DefaultRecommendedShowsDao
    private lateinit var requestManager: FakeRequestManagerRepository
    private lateinit var traktSource: FakeTraktSource
    private lateinit var tmdbSource: FakeTmdbDetailsSource
    private lateinit var store: RecommendedShowsStore

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        tvShowsDao = DefaultTvShowsDao(database = database, dispatchers = dispatchers)
        recommendedShowsDao = DefaultRecommendedShowsDao(
            database = database,
            showIdResolver = showIdResolver,
            dispatchers = dispatchers,
        )
        requestManager = FakeRequestManagerRepository(initialRequestValid = false)
        traktSource = FakeTraktSource()
        tmdbSource = FakeTmdbDetailsSource()
        store = buildStore()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should fetch and persist recommended shows given trakt id is present`() = runTest(testDispatcher) {
        seedParentShow(tmdbId = PARENT_TMDB_ID)
        showIdForTraktId(traktId = PARENT_TRAKT_ID, tmdbId = PARENT_TMDB_ID)

        traktSource.setRelatedShows(
            ApiResponse.Success(
                listOf(
                    TraktShowResponse(
                        title = "Related Show",
                        ids = ShowIds(trakt = 200L, tmdb = REC_TMDB_ID),
                        overview = "Related overview",
                    ),
                ),
            ),
        )
        tmdbSource.setShowDetails(
            id = REC_TMDB_ID,
            response = ApiResponse.Success(buildTmdbDetails(id = REC_TMDB_ID.toInt(), name = "Related Show")),
        )

        store.stream(StoreReadRequest.fresh(RecommendedShowsParams(page = 1L, showId = PARENT_TMDB_ID))).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val rows = database.recommendedShowsQueries.recommendedShows(Id(PARENT_TRAKT_ID)).executeAsList()
        rows shouldHaveSize 1
        rows[0].tmdb_id.id shouldBe REC_TMDB_ID
    }

    @Test
    fun `should fetch and persist recommended shows given no trakt id`() = runTest(testDispatcher) {
        seedParentShow(tmdbId = PARENT_TMDB_ID)

        tmdbSource.setRecommendedShows(
            ApiResponse.Success(
                TmdbShowResult(
                    page = 1,
                    totalPages = 1,
                    totalResults = 1,
                    results = arrayListOf(
                        TmdbShowResponse(
                            id = REC_TMDB_ID.toInt(),
                            name = "TMDB Recommended",
                            overview = "A TMDB overview",
                            popularity = 9.0,
                            voteAverage = 7.5,
                            voteCount = 300,
                            genreIds = arrayListOf(),
                            originCountry = arrayListOf("US"),
                            posterPath = "/poster.jpg",
                        ),
                    ),
                ),
            ),
        )

        store.stream(StoreReadRequest.fresh(RecommendedShowsParams(page = 1L, showId = PARENT_TMDB_ID))).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val show = database.tvShowQueries.tvshowByTmdbId(Id<TmdbId>(REC_TMDB_ID)).executeAsOneOrNull()
        show?.name shouldBe "TMDB Recommended"
        show?.overview shouldBe "A TMDB overview"

        val rows = database.recommendedShowsQueries.recommendedShows(Id(PARENT_TMDB_ID)).executeAsList()
        rows shouldHaveSize 1
        rows[0].tmdb_id.id shouldBe REC_TMDB_ID
    }

    @Test
    fun `should return empty list given no trakt id and tmdb returns empty results`() = runTest(testDispatcher) {
        seedParentShow(tmdbId = PARENT_TMDB_ID)

        tmdbSource.setRecommendedShows(
            ApiResponse.Success(
                TmdbShowResult(
                    page = 1,
                    totalPages = 1,
                    totalResults = 0,
                    results = arrayListOf(),
                ),
            ),
        )

        store.stream(StoreReadRequest.fresh(RecommendedShowsParams(page = 1L, showId = PARENT_TMDB_ID))).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val rows = database.recommendedShowsQueries.recommendedShows(Id(PARENT_TMDB_ID)).executeAsList()
        rows shouldHaveSize 0
    }

    @Test
    fun `should return empty list given no trakt id and tmdb returns error`() = runTest(testDispatcher) {
        seedParentShow(tmdbId = PARENT_TMDB_ID)

        tmdbSource.setRecommendedShows(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "Internal error"),
        )

        store.stream(StoreReadRequest.fresh(RecommendedShowsParams(page = 1L, showId = PARENT_TMDB_ID))).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val rows = database.recommendedShowsQueries.recommendedShows(Id(PARENT_TMDB_ID)).executeAsList()
        rows shouldHaveSize 0
    }

    private fun seedParentShow(tmdbId: Long) {
        database.tvShowQueries.upsert(
            tmdb_id = Id(tmdbId),
            name = "Parent Show",
            overview = "",
            language = null,
            year = null,
            ratings = 0.0,
            vote_count = 0L,
            genres = null,
            status = null,
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
    }

    private fun buildStore(): RecommendedShowsStore = RecommendedShowsStore(
        traktRemoteDataSource = traktSource,
        tmdbDataSource = tmdbSource,
        tvShowsDao = tvShowsDao,
        recommendedShowsDao = recommendedShowsDao,
        requestManagerRepository = requestManager,
        formatterUtil = FakeFormatterUtil(),
        dateTimeProvider = FakeDateTimeProvider(),
        databaseTransactionRunner = ImmediateTransactionRunner,
        dispatchers = dispatchers,
    )

    private companion object {
        private const val PARENT_TMDB_ID = 1000L
        private const val PARENT_TRAKT_ID = 500L
        private const val REC_TMDB_ID = 2000L
    }
}

private object ImmediateTransactionRunner : DatabaseTransactionRunner {
    override fun <T> invoke(block: () -> T): T = block()
}

private class FakeTraktSource : TraktShowsRemoteDataSource {
    private var relatedShowsResponse: ApiResponse<List<TraktShowResponse>>? = null

    fun setRelatedShows(response: ApiResponse<List<TraktShowResponse>>) {
        relatedShowsResponse = response
    }

    override suspend fun getRelatedShows(showId: Long, page: Int, limit: Int): ApiResponse<List<TraktShowResponse>> =
        relatedShowsResponse ?: error("FakeTraktSource: getRelatedShows not configured")
    override suspend fun getTrendingShows(page: Int, limit: Int, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getGenres(): ApiResponse<List<TraktGenreResponse>> = error("not configured")
    override suspend fun getPopularShows(page: Int, limit: Int, genres: String?): ApiResponse<List<TraktShowResponse>> = error("not configured")
    override suspend fun getFavoritedShows(page: Int, limit: Int, period: TimePeriod, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getMostWatchedShows(page: Int, limit: Int, period: TimePeriod, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getShowDetails(showId: Long): ApiResponse<TraktShowResponse> = error("not configured")
    override suspend fun getShowByTmdbId(tmdbId: Long): ApiResponse<List<TraktSearchResult>> = error("not configured")
    override suspend fun searchShows(query: String, page: Int, limit: Int): ApiResponse<List<TraktSearchResult>> = error("not configured")
    override suspend fun getShowPeople(showId: Long): ApiResponse<TraktShowPeopleResponse> = error("not configured")
    override suspend fun getShowVideos(showId: Long): ApiResponse<List<TraktVideosResponse>> = error("not configured")
    override suspend fun getWatchedProgress(showId: Long): ApiResponse<TraktWatchedProgressResponse> = error("not configured")
}

private class FakeTmdbDetailsSource : TmdbShowDetailsNetworkDataSource {
    private val detailsResponses = mutableMapOf<Long, ApiResponse<TmdbShowDetailsResponse>>()
    private var recommendedResponse: ApiResponse<TmdbShowResult> =
        ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "not configured")

    fun setShowDetails(id: Long, response: ApiResponse<TmdbShowDetailsResponse>) {
        detailsResponses[id] = response
    }

    fun setRecommendedShows(response: ApiResponse<TmdbShowResult>) {
        recommendedResponse = response
    }

    override suspend fun getShowDetails(id: Long): ApiResponse<TmdbShowDetailsResponse> =
        detailsResponses[id] ?: ApiResponse.Error.HttpError(code = 404, errorBody = null, errorMessage = "not found")
    override suspend fun getSimilarShows(id: Long, page: Long): ApiResponse<TmdbShowResult> = error("not configured")
    override suspend fun getRecommendedShows(id: Long, page: Long): ApiResponse<TmdbShowResult> = recommendedResponse
    override suspend fun getShowWatchProviders(id: Long): ApiResponse<WatchProvidersResult> = error("not configured")
}

private fun buildTmdbDetails(id: Int, name: String): TmdbShowDetailsResponse = TmdbShowDetailsResponse(
    adult = false,
    backdropPath = null,
    episodeRunTime = arrayListOf(),
    firstAirDate = "2023-01-01",
    genres = arrayListOf(GenreResponse(id = 1, name = "Drama")),
    id = id,
    lastAirDate = null,
    lastEpisodeToAir = null,
    name = name,
    nextEpisodeToAir = null,
    networks = arrayListOf(NetworksResponse(id = 1, name = "Netflix")),
    numberOfEpisodes = 10,
    numberOfSeasons = 1,
    overview = "An overview",
    popularity = 80.0,
    posterPath = null,
    seasons = arrayListOf(),
    status = "Ended",
    voteAverage = 8.0,
    voteCount = 500,
    videos = VideosResponse(results = arrayListOf()),
    credits = CreditsResponse(cast = arrayListOf()),
    originalLanguage = "en",
)
