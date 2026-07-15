package com.thomaskioko.tvmaniac.data.showdetails.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.seasons.implementation.DefaultSeasonsDao
import com.thomaskioko.tvmaniac.shows.implementation.DefaultTvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.CreditsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.GenreResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.NetworksResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.SeasonsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
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
import io.kotest.matchers.nulls.shouldNotBeNull
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
internal class ShowDetailsStoreTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var tvShowsDao: DefaultTvShowsDao
    private lateinit var showDetailsDao: DefaultShowDetailsDao
    private lateinit var seasonsDao: DefaultSeasonsDao
    private lateinit var requestManager: FakeRequestManagerRepository
    private lateinit var traktSource: FakeTraktShowsSource
    private lateinit var tmdbSource: FakeTmdbShowDetailsSource
    private lateinit var store: ShowDetailsStore

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        tvShowsDao = DefaultTvShowsDao(database = database, dispatchers = dispatchers)
        showDetailsDao = DefaultShowDetailsDao(database = database, dispatchers = dispatchers)
        seasonsDao = DefaultSeasonsDao(database = database, showIdResolver = showIdResolver, dispatcher = dispatchers)
        requestManager = FakeRequestManagerRepository(initialRequestValid = false)
        traktSource = FakeTraktShowsSource()
        tmdbSource = FakeTmdbShowDetailsSource()
        store = buildStore()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should persist tvshow and seasons given trakt id is present`() = runTest(testDispatcher) {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(TMDB_ID),
            name = SHOW_NAME,
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
        showIdForTraktId(traktId = TRAKT_ID, tmdbId = TMDB_ID)

        traktSource.setShowDetails(
            ApiResponse.Success(
                TraktShowResponse(
                    title = SHOW_NAME,
                    overview = "An overview",
                    language = "en",
                    status = "Ended",
                    firstAirDate = "2019-01-01",
                    rating = 8.1,
                    votes = 5000L,
                    airedEpisodes = 10L,
                    genres = listOf("drama", "comedy"),
                    ids = ShowIds(trakt = TRAKT_ID, tmdb = TMDB_ID),
                ),
            ),
        )
        tmdbSource.setShowDetails(
            ApiResponse.Success(
                buildTmdbShowDetailsResponse(
                    id = TMDB_ID.toInt(),
                    name = SHOW_NAME,
                    seasons = arrayListOf(buildSeasonsResponse(id = 1, seasonNumber = 1, episodeCount = 10)),
                ),
            ),
        )

        store.stream(StoreReadRequest.fresh(TMDB_ID)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val tvshow = database.tvShowQueries.tvshowByTmdbId(Id<TmdbId>(TMDB_ID)).executeAsOneOrNull()
        tvshow.shouldNotBeNull()
        tvshow.name shouldBe SHOW_NAME
        tvshow.overview shouldBe "An overview"
        tvshow.language shouldBe "en"
        tvshow.status shouldBe "Ended"
        tvshow.ratings shouldBe 8.1
        tvshow.vote_count shouldBe 5000L

        val traktExternalId = database.tvshowExternalIdQueries.showIdForExternalId(
            provider = Provider.TRAKT,
            externalId = TRAKT_ID.toString(),
        ).executeAsOneOrNull()
        traktExternalId.shouldNotBeNull()

        val seasons = seasonsDao.fetchShowSeasons(TMDB_ID)
        seasons.size shouldBe 1
        seasons[0].season_number shouldBe 1L
    }

    @Test
    fun `should persist tvshow and seasons given no trakt id`() = runTest(testDispatcher) {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(TMDB_ID),
            name = SHOW_NAME,
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

        tmdbSource.setShowDetails(
            ApiResponse.Success(
                buildTmdbShowDetailsResponse(
                    id = TMDB_ID.toInt(),
                    name = SHOW_NAME,
                    seasons = arrayListOf(
                        buildSeasonsResponse(id = 1, seasonNumber = 1, episodeCount = 6),
                        buildSeasonsResponse(id = 2, seasonNumber = 2, episodeCount = 8),
                    ),
                ),
            ),
        )

        store.stream(StoreReadRequest.fresh(TMDB_ID)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val tvshow = database.tvShowQueries.tvshowByTmdbId(Id<TmdbId>(TMDB_ID)).executeAsOneOrNull()
        tvshow.shouldNotBeNull()
        tvshow.name shouldBe SHOW_NAME
        tvshow.status shouldBe "Ended"
        tvshow.ratings shouldBe 8.5
        tvshow.vote_count shouldBe 1200L
        tvshow.season_numbers shouldBe "2"
        tvshow.episode_numbers shouldBe "14"

        val traktExternalId = database.tvshowExternalIdQueries.showIdForExternalId(
            provider = Provider.TRAKT,
            externalId = TRAKT_ID.toString(),
        ).executeAsOneOrNull()
        traktExternalId shouldBe null

        val seasons = seasonsDao.fetchShowSeasons(TMDB_ID)
        seasons.size shouldBe 2
    }

    @Test
    fun `should persist season poster given tmdb response includes it`() = runTest(testDispatcher) {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(TMDB_ID),
            name = SHOW_NAME,
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

        tmdbSource.setShowDetails(
            ApiResponse.Success(
                buildTmdbShowDetailsResponse(
                    id = TMDB_ID.toInt(),
                    name = SHOW_NAME,
                    seasons = arrayListOf(
                        buildSeasonsResponse(id = 1, seasonNumber = 1, episodeCount = 10).copy(posterPath = "/season1.jpg"),
                    ),
                ),
            ),
        )

        store.stream(StoreReadRequest.fresh(TMDB_ID)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val internalShowId = showIdResolver.showIdForTmdbId(TMDB_ID).shouldNotBeNull()
        val season = database.seasonsQueries.getSeasonByShowAndNumber(showId = internalShowId, seasonNumber = 1L).executeAsOne()
        season.image_url.shouldNotBeNull()
    }

    private fun buildStore(): ShowDetailsStore = ShowDetailsStore(
        traktRemoteDataSource = traktSource,
        tmdbRemoteDataSource = tmdbSource,
        tvShowsDao = tvShowsDao,
        showDetailsDao = showDetailsDao,
        seasonDao = seasonsDao,
        showIdResolver = showIdResolver,
        formatterUtil = FakeFormatterUtil(),
        dateTimeProvider = FakeDateTimeProvider(),
        requestManagerRepository = requestManager,
        databaseTransactionRunner = ImmediateTransactionRunner,
        dispatchers = dispatchers,
    )

    private companion object {
        private const val TMDB_ID = 9001L
        private const val TRAKT_ID = 1001L
        private const val SHOW_NAME = "Simkl Show"
    }
}

private object ImmediateTransactionRunner : DatabaseTransactionRunner {
    override fun <T> invoke(block: () -> T): T = block()
}

private class FakeTraktShowsSource : TraktShowsRemoteDataSource {
    private var showDetailsResponse: ApiResponse<TraktShowResponse>? = null

    fun setShowDetails(response: ApiResponse<TraktShowResponse>) {
        showDetailsResponse = response
    }

    override suspend fun getShowDetails(showId: Long): ApiResponse<TraktShowResponse> =
        showDetailsResponse ?: error("FakeTraktShowsSource: getShowDetails not configured")
    override suspend fun getTrendingShows(page: Int, limit: Int, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getGenres(): ApiResponse<List<TraktGenreResponse>> = error("not configured")
    override suspend fun getPopularShows(page: Int, limit: Int, genres: String?): ApiResponse<List<TraktShowResponse>> = error("not configured")
    override suspend fun getFavoritedShows(page: Int, limit: Int, period: TimePeriod, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getMostWatchedShows(page: Int, limit: Int, period: TimePeriod, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getRelatedShows(showId: Long, page: Int, limit: Int): ApiResponse<List<TraktShowResponse>> = error("not configured")
    override suspend fun getShowByTmdbId(tmdbId: Long): ApiResponse<List<TraktSearchResult>> = error("not configured")
    override suspend fun searchShows(query: String, page: Int, limit: Int): ApiResponse<List<TraktSearchResult>> = error("not configured")
    override suspend fun getShowPeople(showId: Long): ApiResponse<TraktShowPeopleResponse> = error("not configured")
    override suspend fun getShowVideos(showId: Long): ApiResponse<List<TraktVideosResponse>> = error("not configured")
    override suspend fun getWatchedProgress(showId: Long): ApiResponse<TraktWatchedProgressResponse> = error("not configured")
}

private class FakeTmdbShowDetailsSource : TmdbShowDetailsNetworkDataSource {
    private var showDetailsResponse: ApiResponse<TmdbShowDetailsResponse>? = null

    fun setShowDetails(response: ApiResponse<TmdbShowDetailsResponse>) {
        showDetailsResponse = response
    }

    override suspend fun getShowDetails(id: Long): ApiResponse<TmdbShowDetailsResponse> =
        showDetailsResponse ?: error("FakeTmdbShowDetailsSource: getShowDetails not configured")
    override suspend fun getSimilarShows(id: Long, page: Long): ApiResponse<TmdbShowResult> = error("not configured")
    override suspend fun getRecommendedShows(id: Long, page: Long): ApiResponse<TmdbShowResult> = error("not configured")
    override suspend fun getShowWatchProviders(id: Long): ApiResponse<WatchProvidersResult> = error("not configured")
}

private fun buildTmdbShowDetailsResponse(
    id: Int,
    name: String,
    seasons: ArrayList<SeasonsResponse> = arrayListOf(),
): TmdbShowDetailsResponse = TmdbShowDetailsResponse(
    adult = false,
    backdropPath = "/backdrop.jpg",
    episodeRunTime = arrayListOf(45),
    firstAirDate = "2020-03-01",
    genres = arrayListOf(GenreResponse(id = 1, name = "Drama")),
    id = id,
    lastAirDate = null,
    lastEpisodeToAir = null,
    name = name,
    nextEpisodeToAir = null,
    networks = arrayListOf(NetworksResponse(id = 1, name = "Netflix")),
    numberOfEpisodes = seasons.sumOf { it.episodeCount },
    numberOfSeasons = seasons.size,
    overview = "A TMDB overview",
    popularity = 100.0,
    posterPath = "/poster.jpg",
    seasons = seasons,
    status = "Ended",
    voteAverage = 8.5,
    voteCount = 1200,
    videos = VideosResponse(results = arrayListOf()),
    credits = CreditsResponse(cast = arrayListOf()),
    originalLanguage = "en",
)

private fun buildSeasonsResponse(
    id: Int,
    seasonNumber: Int,
    episodeCount: Int,
): SeasonsResponse = SeasonsResponse(
    id = id,
    seasonNumber = seasonNumber,
    episodeCount = episodeCount,
    name = "Season $seasonNumber",
    overview = null,
    posterPath = null,
    voteAverage = 8.0,
)
