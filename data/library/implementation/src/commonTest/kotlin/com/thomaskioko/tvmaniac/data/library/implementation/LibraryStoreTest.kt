package com.thomaskioko.tvmaniac.data.library.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.library.model.LibrarySortOption
import com.thomaskioko.tvmaniac.data.library.model.RemoteFollowedShow
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.implementation.DefaultFollowedShowsDao
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.shows.implementation.DefaultShowReconciler
import com.thomaskioko.tvmaniac.shows.implementation.DefaultTvShowsDao
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeActivitySyncRepository
import com.thomaskioko.tvmaniac.tmdb.api.model.CreditsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.NetworksResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.VideosResponse
import com.thomaskioko.tvmaniac.tmdb.testing.FakeTmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.testing.FakeTmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.nulls.shouldBeNull
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
internal class LibraryStoreTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val accountManager = FakeAccountManager()
    private val fakeRequestManager = FakeRequestManagerRepository(initialRequestValid = false)
    private val fakeActivitySync = FakeActivitySyncRepository()
    private val fakeTmdbDetailsSource = FakeTmdbShowDetailsNetworkDataSource()
    private val fakeTmdbShowsSource = FakeTmdbShowsNetworkDataSource()
    private val fakeFormatterUtil = FakeFormatterUtil()

    private lateinit var tvShowsDao: TvShowsDao
    private lateinit var followedShowsDao: FollowedShowsDao
    private lateinit var showReconciler: DefaultShowReconciler

    private val traktSource = FakeLibrarySource(provider = AccountProvider.TRAKT)
    private val simklSource = FakeLibrarySource(provider = AccountProvider.SIMKL)
    private val transactionRunner = ImmediateTransactionRunner()

    private lateinit var store: LibraryStore

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        tvShowsDao = DefaultTvShowsDao(database = database, dispatchers = dispatchers)
        followedShowsDao = DefaultFollowedShowsDao(
            database = database,
            showIdResolver = showIdResolver,
            dispatchers = dispatchers,
        )
        showReconciler = DefaultShowReconciler(
            tmdbDataSource = fakeTmdbShowsSource,
            database = database,
            logger = FakeLogger(),
        )
        store = LibraryStore(
            activeSource = { setOf(traktSource, simklSource).firstOrNull { it.provider == accountManager.getActiveProvider() } },
            tmdbDataSource = fakeTmdbDetailsSource,
            followedShowsDao = followedShowsDao,
            tvShowsDao = tvShowsDao,
            requestManagerRepository = fakeRequestManager,
            syncRepository = fakeActivitySync,
            transactionRunner = transactionRunner,
            showReconciler = showReconciler,
            formatterUtil = fakeFormatterUtil,
            dispatchers = dispatchers,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should write followed shows and trakt external id given trakt watchlist show with tmdb id`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(AccountProvider.TRAKT)
        traktSource.setWatchlist(
            listOf(
                RemoteFollowedShow(
                    tmdbId = TMDB_ID,
                    imdbId = IMDB_ID,
                    providerShowId = TRAKT_ID.toString(),
                    provider = AccountProvider.TRAKT,
                    title = SHOW_TITLE,
                    year = 2022,
                    followedAt = FOLLOWED_AT,
                ),
            ),
        )
        fakeTmdbDetailsSource.setDefaultShowDetails(
            ApiResponse.Success(
                buildMinimalTmdbDetailsResponse(
                    id = TMDB_ID.toInt(),
                    name = SHOW_TITLE,
                    posterPath = "/poster.jpg",
                    backdropPath = "/backdrop.jpg",
                ),
            ),
        )

        store.stream(StoreReadRequest.fresh(LibrarySortOption.ADDED_DESC)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val tvshow = database.tvShowQueries.tvshowByTmdbId(Id<TmdbId>(TMDB_ID)).executeAsOneOrNull()
        tvshow.shouldNotBeNull()
        tvshow.name shouldBe SHOW_TITLE

        val followedEntry = followedShowsDao.entryWithTmdbId(TMDB_ID)
        followedEntry.shouldNotBeNull()
        followedEntry.showId shouldBe TMDB_ID
        followedEntry.tmdbId shouldBe TMDB_ID

        val traktExternalId = database.tvshowExternalIdQueries.showIdForExternalId(
            provider = Provider.TRAKT,
            externalId = TRAKT_ID.toString(),
        ).executeAsOneOrNull()
        traktExternalId.shouldNotBeNull()
    }

    @Test
    fun `should write followed shows and simkl external id given simkl show resolved via imdb id`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(AccountProvider.SIMKL)
        fakeTmdbShowsSource.setFindShowByExternalId(ApiResponse.Success(TMDB_ID))
        simklSource.setWatchlist(
            listOf(
                RemoteFollowedShow(
                    tmdbId = null,
                    imdbId = IMDB_ID,
                    providerShowId = SIMKL_ID,
                    provider = AccountProvider.SIMKL,
                    title = SHOW_TITLE,
                    year = 2021,
                    followedAt = FOLLOWED_AT,
                ),
            ),
        )
        fakeTmdbDetailsSource.setDefaultShowDetails(
            ApiResponse.Success(
                buildMinimalTmdbDetailsResponse(
                    id = TMDB_ID.toInt(),
                    name = SHOW_TITLE,
                    posterPath = null,
                    backdropPath = null,
                ),
            ),
        )

        store.stream(StoreReadRequest.fresh(LibrarySortOption.ADDED_DESC)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val tvshow = database.tvShowQueries.tvshowByTmdbId(Id<TmdbId>(TMDB_ID)).executeAsOneOrNull()
        tvshow.shouldNotBeNull()

        val showId = database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(TMDB_ID)).executeAsOneOrNull()
        showId.shouldNotBeNull()
        val followedCount = database.followedShowsQueries.countEntries().executeAsOne()
        followedCount shouldBe 1L

        val simklExternalId = database.tvshowExternalIdQueries.showIdForExternalId(
            provider = Provider.SIMKL,
            externalId = SIMKL_ID,
        ).executeAsOneOrNull()
        simklExternalId.shouldNotBeNull()

        val traktExternalId = database.tvshowExternalIdQueries.showIdForExternalId(
            provider = Provider.TRAKT,
            externalId = TRAKT_ID.toString(),
        ).executeAsOneOrNull()
        traktExternalId.shouldBeNull()
    }

    @Test
    fun `should skip show given no tmdb id and imdb lookup returns null`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(AccountProvider.SIMKL)
        fakeTmdbShowsSource.setFindShowByExternalId(ApiResponse.Success(null))
        simklSource.setWatchlist(
            listOf(
                RemoteFollowedShow(
                    tmdbId = null,
                    imdbId = IMDB_ID,
                    providerShowId = SIMKL_ID,
                    provider = AccountProvider.SIMKL,
                    title = SHOW_TITLE,
                    year = 2021,
                    followedAt = FOLLOWED_AT,
                ),
            ),
        )

        store.stream(StoreReadRequest.fresh(LibrarySortOption.ADDED_DESC)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val tvshow = database.tvShowQueries.tvshowByTmdbId(Id<TmdbId>(TMDB_ID)).executeAsOneOrNull()
        tvshow.shouldBeNull()

        val followedCount = database.followedShowsQueries.countEntries().executeAsOne()
        followedCount shouldBe 0L
    }

    @Test
    fun `should not attach trakt external id given simkl show resolved via tmdb id`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(AccountProvider.SIMKL)
        simklSource.setWatchlist(
            listOf(
                RemoteFollowedShow(
                    tmdbId = TMDB_ID,
                    imdbId = IMDB_ID,
                    providerShowId = SIMKL_ID,
                    provider = AccountProvider.SIMKL,
                    title = SHOW_TITLE,
                    year = 2021,
                    followedAt = FOLLOWED_AT,
                ),
            ),
        )
        fakeTmdbDetailsSource.setDefaultShowDetails(
            ApiResponse.Success(
                buildMinimalTmdbDetailsResponse(
                    id = TMDB_ID.toInt(),
                    name = SHOW_TITLE,
                    posterPath = null,
                    backdropPath = null,
                ),
            ),
        )

        store.stream(StoreReadRequest.fresh(LibrarySortOption.ADDED_DESC)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val followedCount = database.followedShowsQueries.countEntries().executeAsOne()
        followedCount shouldBe 1L

        val simklExternalId = database.tvshowExternalIdQueries.showIdForExternalId(
            provider = Provider.SIMKL,
            externalId = SIMKL_ID,
        ).executeAsOneOrNull()
        simklExternalId.shouldNotBeNull()

        val traktExternalId = database.tvshowExternalIdQueries.showIdForExternalId(
            provider = Provider.TRAKT,
            externalId = TRAKT_ID.toString(),
        ).executeAsOneOrNull()
        traktExternalId.shouldBeNull()
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

private class FakeLibrarySource(
    override val provider: AccountProvider,
) : com.thomaskioko.tvmaniac.data.library.LibraryRemoteDataSource {

    private var watchlistShows: List<RemoteFollowedShow> = emptyList()

    fun setWatchlist(shows: List<RemoteFollowedShow>) {
        watchlistShows = shows
    }

    override suspend fun getWatchlist(): ApiResponse<List<RemoteFollowedShow>> =
        ApiResponse.Success(watchlistShows)

    override suspend fun addToWatchlist(showIds: List<Long>): ApiResponse<com.thomaskioko.tvmaniac.data.library.model.WatchlistSyncResult> =
        ApiResponse.Success(com.thomaskioko.tvmaniac.data.library.model.WatchlistSyncResult(notFoundCount = 0))

    override suspend fun removeFromWatchlist(showIds: List<Long>): ApiResponse<com.thomaskioko.tvmaniac.data.library.model.WatchlistSyncResult> =
        ApiResponse.Success(com.thomaskioko.tvmaniac.data.library.model.WatchlistSyncResult(notFoundCount = 0))
}

private fun buildMinimalTmdbDetailsResponse(
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
