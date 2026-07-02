package com.thomaskioko.tvmaniac.data.ratings.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import com.thomaskioko.tvmaniac.data.ratings.api.CommunityRating
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRemoteDataSource
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.impl.extensions.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
internal class RatingsStoreTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val remoteDataSource = FakeRatingsRemoteDataSource()
    private var activeRemoteSource: FakeRatingsRemoteDataSource? = remoteDataSource
    private val requestManagerRepository = FakeRequestManagerRepository(initialRequestValid = false)
    private val dateTimeProvider = FakeDateTimeProvider()

    private lateinit var providerMetaDao: DefaultProviderMetaDao

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        providerMetaDao = DefaultProviderMetaDao(database = database, dispatchers = dispatchers)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    private fun buildStore(): RatingsStore = RatingsStore(
        activeSource = { activeRemoteSource },
        providerMetaDao = providerMetaDao,
        database = database,
        requestManagerRepository = requestManagerRepository,
        dateTimeProvider = dateTimeProvider,
        dispatchers = dispatchers,
    )

    @Test
    fun `should persist community rating given trakt fetch succeeds`() = runTest(testDispatcher) {
        val showId = seedShow(tmdbId = TMDB_ID)
        database.tvshowExternalIdQueries.insert(showId = showId, provider = Provider.TRAKT, externalId = TRAKT_ID.toString())
        remoteDataSource.provider = AccountProvider.TRAKT
        remoteDataSource.setCommunityRatingResponse(ApiResponse.Success(CommunityRating(rating = 8.5, votes = 100)))
        val store = buildStore()

        store.stream(StoreReadRequest.fresh(showId.id)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val row = database.tvshowProviderMetaQueries.providerRating(showId, Provider.TRAKT).executeAsOneOrNull()
        row.shouldNotBeNull()
        row.rating shouldBe 8.5
        row.vote_count shouldBe 100
    }

    @Test
    fun `should persist community rating given simkl fetch succeeds`() = runTest(testDispatcher) {
        val showId = seedShow(tmdbId = TMDB_ID)
        database.tvshowExternalIdQueries.insert(showId = showId, provider = Provider.SIMKL, externalId = SIMKL_ID)
        remoteDataSource.provider = AccountProvider.SIMKL
        remoteDataSource.setCommunityRatingResponse(ApiResponse.Success(CommunityRating(rating = 7.2, votes = 42)))
        val store = buildStore()

        store.stream(StoreReadRequest.fresh(showId.id)).test {
            awaitItem()
            awaitItem()
            cancelAndConsumeRemainingEvents()
        }

        val row = database.tvshowProviderMetaQueries.providerRating(showId, Provider.SIMKL).executeAsOneOrNull()
        row.shouldNotBeNull()
        row.rating shouldBe 7.2
        row.vote_count shouldBe 42
    }

    @Test
    fun `should throw given show has no provider id for the active provider`() = runTest(testDispatcher) {
        val showId = seedShow(tmdbId = TMDB_ID)
        remoteDataSource.provider = AccountProvider.SIMKL
        val store = buildStore()

        assertFailsWith<AuthenticationException> {
            store.get(showId.id)
        }
    }

    @Test
    fun `should throw given no active sync provider`() = runTest(testDispatcher) {
        val showId = seedShow(tmdbId = TMDB_ID)
        activeRemoteSource = null
        val store = buildStore()

        assertFailsWith<AuthenticationException> {
            store.get(showId.id)
        }
    }

    private fun seedShow(tmdbId: Long): Id<ShowId> {
        val _ = database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(tmdbId),
            name = "Test Show",
            overview = "Overview",
            language = "en",
            year = "2023-01-01",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
        return database.tvShowQueries.getShowIdByTmdbId(Id(tmdbId)).executeAsOne()
    }

    private companion object {
        private const val TMDB_ID = 555L
        private const val TRAKT_ID = 1001L
        private const val SIMKL_ID = "39687"
    }
}
