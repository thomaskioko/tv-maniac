package com.thomaskioko.tvmaniac.data.cast.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.implementation.DefaultTvShowsDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.CastResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.CreditsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbGenreResult
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowResult
import com.thomaskioko.tvmaniac.trakt.api.TimePeriod
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCastMember
import com.thomaskioko.tvmaniac.trakt.api.model.TraktGenreResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPerson
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSearchResult
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowPeopleResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktVideosResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
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
internal class ShowCastStoreTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var tvShowsDao: DefaultTvShowsDao
    private lateinit var castDao: DefaultCastDao
    private lateinit var requestManager: FakeRequestManagerRepository
    private lateinit var traktSource: FakeTraktSource
    private lateinit var tmdbSource: FakeTmdbSource
    private lateinit var store: ShowCastStore

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        tvShowsDao = DefaultTvShowsDao(database = database, dispatchers = dispatchers)
        castDao = DefaultCastDao(
            database = database,
            showIdResolver = showIdResolver,
            dispatcher = dispatchers,
        )
        requestManager = FakeRequestManagerRepository(initialRequestValid = false)
        traktSource = FakeTraktSource()
        tmdbSource = FakeTmdbSource()
        store = buildStore()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should persist cast from trakt and tmdb given trakt id is present`() = runTest(testDispatcher) {
        seedShow(tmdbId = SHOW_TMDB_ID)
        showIdForTraktId(traktId = SHOW_TRAKT_ID, tmdbId = SHOW_TMDB_ID)

        traktSource.setPeople(
            ApiResponse.Success(
                TraktShowPeopleResponse(
                    cast = listOf(
                        TraktCastMember(
                            characters = listOf("Hero"),
                            person = TraktPerson(
                                name = "Actor One",
                                ids = TraktPersonIds(trakt = 10L, tmdb = CAST_TMDB_ID),
                            ),
                        ),
                    ),
                ),
            ),
        )
        tmdbSource.setCredits(
            ApiResponse.Success(
                CreditsResponse(
                    cast = arrayListOf(
                        CastResponse(
                            id = CAST_TMDB_ID.toInt(),
                            name = "Actor One",
                            character = "Hero",
                            popularity = 5.0,
                            profilePath = "/actor.jpg",
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

        val cast = castDao.getShowCast(SHOW_TMDB_ID)
        cast shouldHaveSize 1
        cast[0].name shouldBe "Actor One"
        cast[0].character_name shouldBe "Hero"
    }

    @Test
    fun `should persist cast from tmdb only given no trakt id`() = runTest(testDispatcher) {
        seedShow(tmdbId = SHOW_TMDB_ID)

        tmdbSource.setCredits(
            ApiResponse.Success(
                CreditsResponse(
                    cast = arrayListOf(
                        CastResponse(
                            id = CAST_TMDB_ID.toInt(),
                            name = "TMDB Actor",
                            character = "Sidekick",
                            popularity = 3.5,
                            profilePath = null,
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

        val cast = castDao.getShowCast(SHOW_TMDB_ID)
        cast shouldHaveSize 1
        cast[0].name shouldBe "TMDB Actor"
        cast[0].character_name shouldBe "Sidekick"
        cast[0].trakt_id shouldBe null
    }

    @Test
    fun `should persist empty cast given no trakt id and tmdb credits are empty`() = runTest(testDispatcher) {
        seedShow(tmdbId = SHOW_TMDB_ID)

        tmdbSource.setCredits(
            ApiResponse.Success(CreditsResponse(cast = arrayListOf())),
        )

        store.stream(StoreReadRequest.fresh(SHOW_TMDB_ID)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val cast = castDao.getShowCast(SHOW_TMDB_ID)
        cast shouldHaveSize 0
    }

    @Test
    fun `should persist empty cast given no trakt id and tmdb credits return error`() = runTest(testDispatcher) {
        seedShow(tmdbId = SHOW_TMDB_ID)

        tmdbSource.setCredits(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "error"),
        )

        store.stream(StoreReadRequest.fresh(SHOW_TMDB_ID)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val cast = castDao.getShowCast(SHOW_TMDB_ID)
        cast shouldHaveSize 0
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

    private fun buildStore(): ShowCastStore = ShowCastStore(
        traktRemoteDataSource = traktSource,
        tmdbNetworkDataSource = tmdbSource,
        tvShowsDao = tvShowsDao,
        castDao = castDao,
        showIdResolver = showIdResolver,
        formatterUtil = FakeFormatterUtil(),
        requestManagerRepository = requestManager,
        databaseTransactionRunner = ImmediateTransactionRunner,
        dispatchers = dispatchers,
    )

    private companion object {
        private const val SHOW_TMDB_ID = 4000L
        private const val SHOW_TRAKT_ID = 700L
        private const val CAST_TMDB_ID = 99L
    }
}

private object ImmediateTransactionRunner : DatabaseTransactionRunner {
    override fun <T> invoke(block: () -> T): T = block()
}

private class FakeTraktSource : TraktShowsRemoteDataSource {
    private var peopleResponse: ApiResponse<TraktShowPeopleResponse>? = null

    fun setPeople(response: ApiResponse<TraktShowPeopleResponse>) {
        peopleResponse = response
    }

    override suspend fun getShowPeople(showId: Long): ApiResponse<TraktShowPeopleResponse> =
        peopleResponse ?: error("FakeTraktSource: getShowPeople not configured")
    override suspend fun getTrendingShows(page: Int, limit: Int, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getGenres(): ApiResponse<List<TraktGenreResponse>> = error("not configured")
    override suspend fun getPopularShows(page: Int, limit: Int, genres: String?): ApiResponse<List<TraktShowResponse>> = error("not configured")
    override suspend fun getFavoritedShows(page: Int, limit: Int, period: TimePeriod, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getMostWatchedShows(page: Int, limit: Int, period: TimePeriod, genres: String?): ApiResponse<List<TraktShowsResponse>> = error("not configured")
    override suspend fun getRelatedShows(showId: Long, page: Int, limit: Int): ApiResponse<List<TraktShowResponse>> = error("not configured")
    override suspend fun getShowDetails(showId: Long): ApiResponse<TraktShowResponse> = error("not configured")
    override suspend fun getShowByTmdbId(tmdbId: Long): ApiResponse<List<TraktSearchResult>> = error("not configured")
    override suspend fun searchShows(query: String, page: Int, limit: Int): ApiResponse<List<TraktSearchResult>> = error("not configured")
    override suspend fun getShowVideos(showId: Long): ApiResponse<List<TraktVideosResponse>> = error("not configured")
    override suspend fun getWatchedProgress(showId: Long): ApiResponse<TraktWatchedProgressResponse> = error("not configured")
}

private class FakeTmdbSource : TmdbShowsNetworkDataSource {
    private var creditsResponse: ApiResponse<CreditsResponse> =
        ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "not configured")

    fun setCredits(response: ApiResponse<CreditsResponse>) {
        creditsResponse = response
    }

    override suspend fun getShowCredits(tmdbId: Long): ApiResponse<CreditsResponse> = creditsResponse
    override suspend fun getAiringToday(page: Long): ApiResponse<TmdbShowResult> = error("not configured")
    override suspend fun discoverShows(
        page: Long,
        sortBy: String,
        genres: String?,
        watchProviders: String?,
        screenedTheatrically: Boolean,
        voteAverageGte: Double?,
        voteCountGte: Int?,
        firstAirDateGte: String?,
        firstAirDateLte: String?,
    ): ApiResponse<TmdbShowResult> = error("not configured")
    override suspend fun getPopularShows(page: Long): ApiResponse<TmdbShowResult> = error("not configured")
    override suspend fun getTopRatedShows(page: Long): ApiResponse<TmdbShowResult> = error("not configured")
    override suspend fun getTrendingShows(timeWindow: String): ApiResponse<TmdbShowResult> = error("not configured")
    override suspend fun getUpComingShows(year: Int, page: Long, sortBy: String): ApiResponse<TmdbShowResult> = error("not configured")
    override suspend fun getUpComingShows(page: Long, firstAirDate: String, lastAirDate: String, sortBy: String): ApiResponse<TmdbShowResult> = error("not configured")
    override suspend fun searchShows(query: String): ApiResponse<TmdbShowResult> = error("not configured")
    override suspend fun getShowGenres(): ApiResponse<TmdbGenreResult> = error("not configured")
    override suspend fun findShowByExternalId(externalId: String, source: String): ApiResponse<Long?> = error("not configured")
}
