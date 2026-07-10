package com.thomaskioko.tvmaniac.startwatching.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.implementation.DefaultFollowedShowsDao
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.implementation.DefaultTvShowsDao
import com.thomaskioko.tvmaniac.startwatching.api.RemotePlanToWatchShow
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingRemoteDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.CreditsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.NetworksResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.VideosResponse
import com.thomaskioko.tvmaniac.tmdb.testing.FakeTmdbShowDetailsNetworkDataSource
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
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
internal class StartWatchingWatchlistStoreTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val fakeRequestManager = FakeRequestManagerRepository(initialRequestValid = false)
    private val fakeTmdbDetailsSource = FakeTmdbShowDetailsNetworkDataSource()
    private val fakeFormatterUtil = FakeFormatterUtil()
    private val transactionRunner = ImmediateTransactionRunner()

    private lateinit var tvShowsDao: TvShowsDao
    private lateinit var followedShowsDao: FollowedShowsDao

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        tvShowsDao = DefaultTvShowsDao(database = database, dispatchers = dispatchers)
        followedShowsDao = DefaultFollowedShowsDao(
            database = database,
            showIdResolver = showIdResolver,
            dispatchers = dispatchers,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    private fun buildStore(activeSource: () -> StartWatchingRemoteDataSource?): StartWatchingWatchlistStore =
        StartWatchingWatchlistStore(
            activeSource = activeSource,
            tmdbDataSource = fakeTmdbDetailsSource,
            followedShowsDao = followedShowsDao,
            tvShowsDao = tvShowsDao,
            requestManagerRepository = fakeRequestManager,
            transactionRunner = transactionRunner,
            formatterUtil = fakeFormatterUtil,
            dispatchers = dispatchers,
        )

    @Test
    fun `should write followed show given trakt plan-to-watch show with tmdb id`() = runTest(testDispatcher) {
        val source = FakeStartWatchingSource(provider = SyncProviderSource.TRAKT)
        source.setPlanToWatch(
            listOf(
                RemotePlanToWatchShow(
                    tmdbId = TMDB_ID,
                    imdbId = IMDB_ID,
                    providerShowId = TRAKT_ID.toString(),
                    provider = SyncProviderSource.TRAKT,
                    title = SHOW_TITLE,
                    year = 2022,
                    followedAt = FOLLOWED_AT,
                ),
            ),
        )
        fakeTmdbDetailsSource.setDefaultShowDetails(
            ApiResponse.Success(
                buildTmdbDetailsResponse(
                    id = TMDB_ID.toInt(),
                    name = SHOW_TITLE,
                    posterPath = "/poster.jpg",
                    backdropPath = "/backdrop.jpg",
                ),
            ),
        )
        val store = buildStore { source }

        store.stream(StoreReadRequest.fresh(Unit)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val tvshow = database.tvShowQueries.tvshowByTmdbId(Id<TmdbId>(TMDB_ID)).executeAsOneOrNull()
        tvshow.shouldNotBeNull()
        tvshow.name shouldBe SHOW_TITLE

        val followedCount = database.followedShowsQueries.countEntries().executeAsOne()
        followedCount shouldBe 1L
    }

    @Test
    fun `should write followed show given simkl plan-to-watch show with tmdb id`() = runTest(testDispatcher) {
        val source = FakeStartWatchingSource(provider = SyncProviderSource.SIMKL)
        source.setPlanToWatch(
            listOf(
                RemotePlanToWatchShow(
                    tmdbId = TMDB_ID,
                    imdbId = IMDB_ID,
                    providerShowId = SIMKL_ID,
                    provider = SyncProviderSource.SIMKL,
                    title = SHOW_TITLE,
                    year = 2021,
                    followedAt = FOLLOWED_AT,
                ),
            ),
        )
        fakeTmdbDetailsSource.setDefaultShowDetails(
            ApiResponse.Success(
                buildTmdbDetailsResponse(
                    id = TMDB_ID.toInt(),
                    name = SHOW_TITLE,
                    posterPath = null,
                    backdropPath = null,
                ),
            ),
        )
        val store = buildStore { source }

        store.stream(StoreReadRequest.fresh(Unit)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val followedCount = database.followedShowsQueries.countEntries().executeAsOne()
        followedCount shouldBe 1L
    }

    @Test
    fun `should skip show given plan-to-watch show has no tmdb id`() = runTest(testDispatcher) {
        val source = FakeStartWatchingSource(provider = SyncProviderSource.SIMKL)
        source.setPlanToWatch(
            listOf(
                RemotePlanToWatchShow(
                    tmdbId = null,
                    imdbId = IMDB_ID,
                    providerShowId = SIMKL_ID,
                    provider = SyncProviderSource.SIMKL,
                    title = SHOW_TITLE,
                    year = 2021,
                    followedAt = FOLLOWED_AT,
                ),
            ),
        )
        val store = buildStore { source }

        store.stream(StoreReadRequest.fresh(Unit)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val followedCount = database.followedShowsQueries.countEntries().executeAsOne()
        followedCount shouldBe 0L
    }

    @Test
    fun `should keep existing followed entries given show no longer in plan-to-watch`() = runTest(testDispatcher) {
        val source = FakeStartWatchingSource(provider = SyncProviderSource.TRAKT)
        source.setPlanToWatch(
            listOf(
                RemotePlanToWatchShow(
                    tmdbId = TMDB_ID,
                    imdbId = IMDB_ID,
                    providerShowId = TRAKT_ID.toString(),
                    provider = SyncProviderSource.TRAKT,
                    title = SHOW_TITLE,
                    year = 2022,
                    followedAt = FOLLOWED_AT,
                ),
            ),
        )
        fakeTmdbDetailsSource.setDefaultShowDetails(
            ApiResponse.Success(
                buildTmdbDetailsResponse(id = TMDB_ID.toInt(), name = SHOW_TITLE, posterPath = null, backdropPath = null),
            ),
        )
        val store = buildStore { source }

        store.stream(StoreReadRequest.fresh(Unit)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        var followedCount = database.followedShowsQueries.countEntries().executeAsOne()
        followedCount shouldBe 1L

        source.setPlanToWatch(emptyList())
        fakeRequestManager.requestValid = false

        store.stream(StoreReadRequest.fresh(Unit)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        followedCount = database.followedShowsQueries.countEntries().executeAsOne()
        followedCount shouldBe 1L
    }

    @Test
    fun `should not write any followed shows given no active provider`() = runTest(testDispatcher) {
        val store = buildStore { null }

        store.stream(StoreReadRequest.fresh(Unit)).test {
            cancelAndConsumeRemainingEvents()
        }

        val followedCount = database.followedShowsQueries.countEntries().executeAsOne()
        followedCount shouldBe 0L
    }

    private companion object {
        private const val TMDB_ID = 42L
        private const val TRAKT_ID = 1001L
        private const val SIMKL_ID = "583436"
        private const val IMDB_ID = "tt1234567"
        private const val SHOW_TITLE = "Test Show"
        private val FOLLOWED_AT = Instant.parse("2025-03-01T00:00:00Z")
    }
}

private class ImmediateTransactionRunner : DatabaseTransactionRunner {
    override fun <T> invoke(block: () -> T): T = block()
}

private class FakeStartWatchingSource(
    override val provider: SyncProviderSource,
) : StartWatchingRemoteDataSource {

    private var shows: List<RemotePlanToWatchShow> = emptyList()

    fun setPlanToWatch(shows: List<RemotePlanToWatchShow>) {
        this.shows = shows
    }

    override suspend fun getPlanToWatch(): ApiResponse<List<RemotePlanToWatchShow>> =
        ApiResponse.Success(shows)
}

private fun buildTmdbDetailsResponse(
    id: Int,
    name: String,
    posterPath: String?,
    backdropPath: String?,
): TmdbShowDetailsResponse = TmdbShowDetailsResponse(
    adult = false,
    backdropPath = backdropPath,
    episodeRunTime = arrayListOf(),
    firstAirDate = null,
    genres = arrayListOf(),
    id = id,
    lastAirDate = null,
    lastEpisodeToAir = null,
    name = name,
    nextEpisodeToAir = null,
    networks = arrayListOf(NetworksResponse(id = 1, name = "Test Network")),
    numberOfEpisodes = 0,
    numberOfSeasons = 0,
    overview = "",
    popularity = 0.0,
    posterPath = posterPath,
    seasons = arrayListOf(),
    status = "Returning Series",
    voteAverage = 0.0,
    voteCount = 0,
    videos = VideosResponse(results = arrayListOf()),
    credits = CreditsResponse(cast = arrayListOf()),
    originalLanguage = "en",
)
