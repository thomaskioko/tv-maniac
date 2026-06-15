package com.thomaskioko.tvmaniac.data.trailers.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
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
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.tmdb.api.model.VideoResultResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.VideosResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.WatchProvidersResult
import com.thomaskioko.tvmaniac.trakt.api.TimePeriod
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktGenreResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSearchResult
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowPeopleResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktVideosResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
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
internal class TrailerStoreTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var tvShowsDao: DefaultTvShowsDao
    private lateinit var trailerDao: DefaultTrailerDao
    private lateinit var requestManager: FakeRequestManagerRepository
    private lateinit var traktSource: FakeTraktSource
    private lateinit var tmdbSource: FakeTmdbDetailsSource
    private lateinit var store: TrailerStore

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        tvShowsDao = DefaultTvShowsDao(database = database, dispatchers = dispatchers)
        trailerDao = DefaultTrailerDao(
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
    fun `should persist trailers from trakt given trakt id is present`() = runTest(testDispatcher) {
        seedShow(tmdbId = SHOW_TMDB_ID)
        showIdForTraktId(traktId = SHOW_TRAKT_ID, tmdbId = SHOW_TMDB_ID)

        traktSource.setVideos(
            ApiResponse.Success(
                listOf(
                    TraktVideosResponse(
                        title = "Official Trailer",
                        url = "https://www.youtube.com/watch?v=abc123",
                        site = "YouTube",
                        type = "Trailer",
                        size = 1080,
                    ),
                ),
            ),
        )

        store.stream(StoreReadRequest.fresh(SHOW_TMDB_ID)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val trailers = trailerDao.getTrailersByShowId(SHOW_TMDB_ID)
        trailers shouldHaveSize 1
        trailers[0].trailer_id shouldBe "abc123"
        trailers[0].youtube_url shouldBe "https://www.youtube.com/watch?v=abc123"
        trailers[0].name shouldBe "Official Trailer"
    }

    @Test
    fun `should persist trailers from tmdb given no trakt id`() = runTest(testDispatcher) {
        seedShow(tmdbId = SHOW_TMDB_ID)

        tmdbSource.setShowDetails(
            ApiResponse.Success(
                buildTmdbDetails(
                    id = SHOW_TMDB_ID.toInt(),
                    videos = arrayListOf(
                        VideoResultResponse(
                            iso6391 = "en",
                            iso31661 = "US",
                            name = "TMDB Trailer",
                            key = "tmdb_key_1",
                            site = "YouTube",
                            size = 1080,
                            type = "Trailer",
                            official = true,
                            id = "vid_001",
                        ),
                    ),
                ),
            ),
        )

        store.stream(StoreReadRequest.fresh(SHOW_TMDB_ID)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val trailers = trailerDao.getTrailersByShowId(SHOW_TMDB_ID)
        trailers shouldHaveSize 1
        trailers[0].trailer_id shouldBe "tmdb_key_1"
        trailers[0].youtube_url shouldBe "https://www.youtube.com/watch?v=tmdb_key_1"
        trailers[0].name shouldBe "TMDB Trailer"
        trailers[0].type shouldBe "Trailer"
    }

    @Test
    fun `should skip non-youtube videos given no trakt id`() = runTest(testDispatcher) {
        seedShow(tmdbId = SHOW_TMDB_ID)

        tmdbSource.setShowDetails(
            ApiResponse.Success(
                buildTmdbDetails(
                    id = SHOW_TMDB_ID.toInt(),
                    videos = arrayListOf(
                        VideoResultResponse(
                            iso6391 = "en",
                            iso31661 = "US",
                            name = "Vimeo Clip",
                            key = "vimeo_key",
                            site = "Vimeo",
                            size = 720,
                            type = "Clip",
                            official = false,
                            id = "vid_002",
                        ),
                        VideoResultResponse(
                            iso6391 = "en",
                            iso31661 = "US",
                            name = "YouTube Teaser",
                            key = "yt_teaser",
                            site = "YouTube",
                            size = 1080,
                            type = "Teaser",
                            official = true,
                            id = "vid_003",
                        ),
                    ),
                ),
            ),
        )

        store.stream(StoreReadRequest.fresh(SHOW_TMDB_ID)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val trailers = trailerDao.getTrailersByShowId(SHOW_TMDB_ID)
        trailers shouldHaveSize 1
        trailers[0].trailer_id shouldBe "yt_teaser"
    }

    @Test
    fun `should return empty list given no trakt id and tmdb returns error`() = runTest(testDispatcher) {
        seedShow(tmdbId = SHOW_TMDB_ID)

        tmdbSource.setShowDetails(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "Internal error"),
        )

        store.stream(StoreReadRequest.fresh(SHOW_TMDB_ID)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val trailers = trailerDao.getTrailersByShowId(SHOW_TMDB_ID)
        trailers shouldHaveSize 0
    }

    private fun seedShow(tmdbId: Long) {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(tmdbId),
            name = "Test Show",
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

    private fun buildStore(): TrailerStore = TrailerStore(
        traktRemoteDataSource = traktSource,
        tmdbShowDetailsDataSource = tmdbSource,
        tvShowsDao = tvShowsDao,
        showIdResolver = showIdResolver,
        trailerDao = trailerDao,
        requestManagerRepository = requestManager,
        databaseTransactionRunner = ImmediateTransactionRunner,
        dispatchers = dispatchers,
    )

    private companion object {
        private const val SHOW_TMDB_ID = 5000L
        private const val SHOW_TRAKT_ID = 800L
    }
}

private object ImmediateTransactionRunner : DatabaseTransactionRunner {
    override fun <T> invoke(block: () -> T): T = block()
}

private class FakeTraktSource : TraktShowsRemoteDataSource {
    private var videosResponse: ApiResponse<List<TraktVideosResponse>>? = null

    fun setVideos(response: ApiResponse<List<TraktVideosResponse>>) {
        videosResponse = response
    }

    override suspend fun getShowVideos(showId: Long): ApiResponse<List<TraktVideosResponse>> =
        videosResponse ?: error("FakeTraktSource: getShowVideos not configured")
    override suspend fun getTrendingShows(page: Int, limit: Int, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getGenres(): ApiResponse<List<TraktGenreResponse>> = error("not configured")
    override suspend fun getPopularShows(page: Int, limit: Int, genres: String?): ApiResponse<List<TraktShowResponse>> = error("not configured")
    override suspend fun getFavoritedShows(page: Int, limit: Int, period: TimePeriod, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getMostWatchedShows(page: Int, limit: Int, period: TimePeriod, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getRelatedShows(showId: Long, page: Int, limit: Int): ApiResponse<List<TraktShowResponse>> = error("not configured")
    override suspend fun getShowDetails(showId: Long): ApiResponse<TraktShowResponse> = error("not configured")
    override suspend fun getShowByTmdbId(tmdbId: Long): ApiResponse<List<TraktSearchResult>> = error("not configured")
    override suspend fun searchShows(query: String, page: Int, limit: Int): ApiResponse<List<TraktSearchResult>> = error("not configured")
    override suspend fun getShowPeople(showId: Long): ApiResponse<TraktShowPeopleResponse> = error("not configured")
    override suspend fun getWatchedProgress(showId: Long): ApiResponse<TraktWatchedProgressResponse> = error("not configured")
}

private class FakeTmdbDetailsSource : TmdbShowDetailsNetworkDataSource {
    private var detailsResponse: ApiResponse<TmdbShowDetailsResponse> =
        ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "not configured")

    fun setShowDetails(response: ApiResponse<TmdbShowDetailsResponse>) {
        detailsResponse = response
    }

    override suspend fun getShowDetails(id: Long): ApiResponse<TmdbShowDetailsResponse> = detailsResponse
    override suspend fun getSimilarShows(id: Long, page: Long): ApiResponse<TmdbShowResult> = error("not configured")
    override suspend fun getRecommendedShows(id: Long, page: Long): ApiResponse<TmdbShowResult> = error("not configured")
    override suspend fun getShowWatchProviders(id: Long): ApiResponse<WatchProvidersResult> = error("not configured")
}

private fun buildTmdbDetails(
    id: Int,
    videos: ArrayList<VideoResultResponse> = arrayListOf(),
): TmdbShowDetailsResponse = TmdbShowDetailsResponse(
    adult = false,
    backdropPath = null,
    episodeRunTime = arrayListOf(),
    firstAirDate = "2023-01-01",
    genres = arrayListOf(GenreResponse(id = 1, name = "Drama")),
    id = id,
    lastAirDate = null,
    lastEpisodeToAir = null,
    name = "Test Show",
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
    videos = VideosResponse(results = videos),
    credits = CreditsResponse(cast = arrayListOf()),
    originalLanguage = "en",
)
