package com.thomaskioko.tvmaniac.data.ratings.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.toDbProvider
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRemoteDataSource
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.ShowToPersist
import com.thomaskioko.tvmaniac.shows.testing.FakeTvShowsDao
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultRatingsRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val dateTimeProvider = FakeDateTimeProvider()
    private val remoteDataSource = FakeRatingsRemoteDataSource()
    private var activeRemoteSource: FakeRatingsRemoteDataSource? = remoteDataSource
    private val tvShowsDao = FakeTvShowsDao()
    private val requestManagerRepository = FakeRequestManagerRepository()
    private lateinit var ratingsDao: DefaultRatingsDao
    private lateinit var providerMetaDao: DefaultProviderMetaDao
    private lateinit var ratingsStore: RatingsStore

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        ratingsDao = DefaultRatingsDao(database, dispatchers)
        providerMetaDao = DefaultProviderMetaDao(database, dispatchers)
        ratingsStore = RatingsStore(
            activeSource = { activeRemoteSource },
            providerMetaDao = providerMetaDao,
            ratingsDao = ratingsDao,
            database = database,
            requestManagerRepository = requestManagerRepository,
            dateTimeProvider = dateTimeProvider,
            dispatchers = dispatchers,
        )
        seedShow(showId = SHOW_ID)
        tvShowsDao.upsert(
            ShowToPersist(
                showId = Id(SHOW_ID),
                tmdbId = Id(TMDB_ID),
                name = "Test Show",
                overview = "",
                ratings = 0.0,
                voteCount = 0,
            ),
        )
        tvShowsDao.setTmdbIdForLocalShowId(showId = SHOW_ID, tmdbId = TMDB_ID)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should persist pending action as upload immediately after rating a show`() = runTest {
        val repository = buildRepository(FakeSyncObserver())

        repository.rateShow(showId = SHOW_ID, rating = 8)

        val entries = ratingsDao.showRatingsWithUploadPendingAction()
        entries.size shouldBe 1
        entries.first().userRating shouldBe 8L
        entries.first().pendingAction shouldBe PendingAction.UPLOAD
    }

    @Test
    fun `should flip pending action to nothing given syncPendingRatings succeeds`() = runTest {
        val repository = buildRepository(FakeSyncObserver())
        ratingsDao.upsertShowUserRating(
            showId = SHOW_ID,
            userRating = 9L,
            ratedAt = dateTimeProvider.nowMillis(),
            pendingAction = PendingAction.UPLOAD,
        )
        remoteDataSource.setAddShowRatingResponse(ApiResponse.Success(Unit))

        repository.syncPendingRatings()

        ratingsDao.observeShowRating(SHOW_ID).test {
            val entry = awaitItem()
            entry?.pendingAction shouldBe PendingAction.NOTHING
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should push the tmdb id resolved from the local show id given syncPendingRatings succeeds`() = runTest {
        val repository = buildRepository(FakeSyncObserver())
        val secondShowId = seedShow(showId = SECOND_TMDB_ID)
        tvShowsDao.setTmdbIdForLocalShowId(showId = secondShowId, tmdbId = DISTINCT_TMDB_ID)
        remoteDataSource.setAddShowRatingResponse(ApiResponse.Success(Unit))

        repository.rateShow(showId = secondShowId, rating = 8)
        repository.syncPendingRatings()

        remoteDataSource.lastAddShowRatingTmdbId shouldBe DISTINCT_TMDB_ID
        ratingsDao.showRatingsWithUploadPendingAction().shouldBeEmpty()
        ratingsDao.observeShowRating(secondShowId).test {
            val entry = awaitItem()
            entry?.pendingAction shouldBe PendingAction.NOTHING
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should leave pending action as upload and log background sync failure given syncPendingRatings fails`() = runTest {
        val syncObserver = FakeSyncObserver()
        val repository = buildRepository(syncObserver)
        ratingsDao.upsertShowUserRating(
            showId = SHOW_ID,
            userRating = 7L,
            ratedAt = dateTimeProvider.nowMillis(),
            pendingAction = PendingAction.UPLOAD,
        )
        remoteDataSource.setAddShowRatingResponse(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "boom"),
        )

        syncObserver.errors.test {
            repository.syncPendingRatings()

            val event = awaitItem()
            event.shouldBeInstanceOf<SyncError.BackgroundSyncFailed>()
        }

        val entries = ratingsDao.showRatingsWithUploadPendingAction()
        entries.size shouldBe 1
        entries.first().pendingAction shouldBe PendingAction.UPLOAD
    }

    @Test
    fun `should emit combined user and community rating given both are cached`() = runTest {
        val repository = buildRepository(FakeSyncObserver())
        remoteDataSource.provider = AccountProvider.TRAKT
        ratingsDao.upsertShowUserRating(
            showId = SHOW_ID,
            userRating = 9L,
            ratedAt = dateTimeProvider.nowMillis(),
            pendingAction = PendingAction.NOTHING,
        )
        providerMetaDao.upsertProviderRating(
            showId = SHOW_ID,
            provider = Provider.TRAKT,
            rating = 8.4,
            voteCount = 500,
            lastSyncedAt = dateTimeProvider.nowMillis(),
        )

        repository.observeShowRating(SHOW_ID).test {
            val rating = awaitItem()
            rating.userRating shouldBe 9
            rating.communityRating shouldBe 8.4
            rating.communityVotes shouldBe 500
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should emit null community rating given no active sync provider`() = runTest {
        val repository = buildRepository(FakeSyncObserver())
        activeRemoteSource = null
        ratingsDao.upsertShowUserRating(
            showId = SHOW_ID,
            userRating = 6L,
            ratedAt = dateTimeProvider.nowMillis(),
            pendingAction = PendingAction.NOTHING,
        )

        repository.observeShowRating(SHOW_ID).test {
            val rating = awaitItem()
            rating.userRating shouldBe 6
            rating.communityRating.shouldBeNull()
            rating.communityVotes.shouldBeNull()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should not throw given refreshCommunityRating cannot resolve a provider show id`() = runTest {
        val repository = buildRepository(FakeSyncObserver())
        remoteDataSource.provider = AccountProvider.TRAKT

        repository.refreshCommunityRating(SHOW_ID, forceRefresh = true)
    }

    @Test
    fun `should save the provider user rating given refreshCommunityRating fetches it`() = runTest {
        val repository = buildRepository(FakeSyncObserver())
        remoteDataSource.provider = AccountProvider.TRAKT
        seedProviderShowId(SHOW_ID, AccountProvider.TRAKT, externalId = 555L)
        remoteDataSource.setUserRatingResponse(ApiResponse.Success(7))

        repository.refreshCommunityRating(SHOW_ID, forceRefresh = true)

        ratingsDao.observeShowRating(SHOW_ID).test {
            val entry = awaitItem()
            entry?.userRating shouldBe 7L
            entry?.pendingAction shouldBe PendingAction.NOTHING
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should keep the pending local rating given a provider user rating is fetched`() = runTest {
        val repository = buildRepository(FakeSyncObserver())
        remoteDataSource.provider = AccountProvider.TRAKT
        seedProviderShowId(SHOW_ID, AccountProvider.TRAKT, externalId = 555L)
        ratingsDao.upsertShowUserRating(
            showId = SHOW_ID,
            userRating = 5L,
            ratedAt = dateTimeProvider.nowMillis(),
            pendingAction = PendingAction.UPLOAD,
        )
        remoteDataSource.setUserRatingResponse(ApiResponse.Success(7))

        repository.refreshCommunityRating(SHOW_ID, forceRefresh = true)

        ratingsDao.observeShowRating(SHOW_ID).test {
            val entry = awaitItem()
            entry?.userRating shouldBe 5L
            entry?.pendingAction shouldBe PendingAction.UPLOAD
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should persist pending action as upload immediately after rating a season`() = runTest {
        seedSeason(seasonId = SEASON_ID)
        val repository = buildRepository(FakeSyncObserver())

        repository.rateSeason(seasonId = SEASON_ID, rating = 7)

        val entries = ratingsDao.seasonRatingsWithUploadPendingAction()
        entries.size shouldBe 1
        entries.first().userRating shouldBe 7L
        entries.first().pendingAction shouldBe PendingAction.UPLOAD
    }

    @Test
    fun `should flip season pending action to nothing given syncPendingRatings succeeds`() = runTest {
        seedSeason(seasonId = SEASON_ID)
        val repository = buildRepository(FakeSyncObserver())
        ratingsDao.upsertSeasonUserRating(
            seasonId = SEASON_ID,
            userRating = 8L,
            ratedAt = dateTimeProvider.nowMillis(),
            pendingAction = PendingAction.UPLOAD,
        )
        remoteDataSource.setAddSeasonRatingResponse(ApiResponse.Success(Unit))

        repository.syncPendingRatings()

        ratingsDao.observeSeasonRating(SEASON_ID).test {
            val entry = awaitItem()
            entry?.pendingAction shouldBe PendingAction.NOTHING
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should leave season pending action as upload and log background sync failure given syncPendingRatings fails`() = runTest {
        seedSeason(seasonId = SEASON_ID)
        val syncObserver = FakeSyncObserver()
        val repository = buildRepository(syncObserver)
        ratingsDao.upsertSeasonUserRating(
            seasonId = SEASON_ID,
            userRating = 6L,
            ratedAt = dateTimeProvider.nowMillis(),
            pendingAction = PendingAction.UPLOAD,
        )
        remoteDataSource.setAddSeasonRatingResponse(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "boom"),
        )

        syncObserver.errors.test {
            repository.syncPendingRatings()

            val event = awaitItem()
            event.shouldBeInstanceOf<SyncError.BackgroundSyncFailed>()
        }

        val entries = ratingsDao.seasonRatingsWithUploadPendingAction()
        entries.size shouldBe 1
        entries.first().pendingAction shouldBe PendingAction.UPLOAD
    }

    @Test
    fun `should emit season user rating given observeSeasonRating is collected`() = runTest {
        seedSeason(seasonId = SEASON_ID)
        val repository = buildRepository(FakeSyncObserver())
        ratingsDao.upsertSeasonUserRating(
            seasonId = SEASON_ID,
            userRating = 9L,
            ratedAt = dateTimeProvider.nowMillis(),
            pendingAction = PendingAction.NOTHING,
        )

        repository.observeSeasonRating(SEASON_ID).test {
            val rating = awaitItem()
            rating.userRating shouldBe 9
            rating.pendingAction shouldBe PendingAction.NOTHING
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should persist locally and sync as no-op given active provider has no season rating api`() = runTest {
        seedSeason(seasonId = SEASON_ID)
        val repository = buildRepository(FakeSyncObserver())
        remoteDataSource.setAddSeasonRatingResponse(ApiResponse.Success(Unit))

        repository.rateSeason(seasonId = SEASON_ID, rating = 5)
        repository.syncPendingRatings()

        ratingsDao.observeSeasonRating(SEASON_ID).test {
            val entry = awaitItem()
            entry?.userRating shouldBe 5L
            entry?.pendingAction shouldBe PendingAction.NOTHING
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should persist pending action as upload immediately after rating an episode`() = runTest {
        seedSeason(seasonId = SEASON_ID)
        seedEpisode(episodeId = EPISODE_ID, seasonId = SEASON_ID)
        val repository = buildRepository(FakeSyncObserver())

        repository.rateEpisode(episodeId = EPISODE_ID, rating = 6)

        val entries = ratingsDao.episodeRatingsWithUploadPendingAction()
        entries.size shouldBe 1
        entries.first().userRating shouldBe 6L
        entries.first().pendingAction shouldBe PendingAction.UPLOAD
    }

    @Test
    fun `should flip episode pending action to nothing given syncPendingRatings succeeds`() = runTest {
        seedSeason(seasonId = SEASON_ID)
        seedEpisode(episodeId = EPISODE_ID, seasonId = SEASON_ID)
        val repository = buildRepository(FakeSyncObserver())
        ratingsDao.upsertEpisodeUserRating(
            episodeId = EPISODE_ID,
            userRating = 8L,
            ratedAt = dateTimeProvider.nowMillis(),
            pendingAction = PendingAction.UPLOAD,
        )
        remoteDataSource.setAddEpisodeRatingResponse(ApiResponse.Success(Unit))

        repository.syncPendingRatings()

        ratingsDao.observeEpisodeRating(EPISODE_ID).test {
            val entry = awaitItem()
            entry?.pendingAction shouldBe PendingAction.NOTHING
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should leave episode pending action as upload and log background sync failure given syncPendingRatings fails`() = runTest {
        seedSeason(seasonId = SEASON_ID)
        seedEpisode(episodeId = EPISODE_ID, seasonId = SEASON_ID)
        val syncObserver = FakeSyncObserver()
        val repository = buildRepository(syncObserver)
        ratingsDao.upsertEpisodeUserRating(
            episodeId = EPISODE_ID,
            userRating = 4L,
            ratedAt = dateTimeProvider.nowMillis(),
            pendingAction = PendingAction.UPLOAD,
        )
        remoteDataSource.setAddEpisodeRatingResponse(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "boom"),
        )

        syncObserver.errors.test {
            repository.syncPendingRatings()

            val event = awaitItem()
            event.shouldBeInstanceOf<SyncError.BackgroundSyncFailed>()
        }

        val entries = ratingsDao.episodeRatingsWithUploadPendingAction()
        entries.size shouldBe 1
        entries.first().pendingAction shouldBe PendingAction.UPLOAD
    }

    @Test
    fun `should emit episode user rating given observeEpisodeRating is collected`() = runTest {
        seedSeason(seasonId = SEASON_ID)
        seedEpisode(episodeId = EPISODE_ID, seasonId = SEASON_ID)
        val repository = buildRepository(FakeSyncObserver())
        ratingsDao.upsertEpisodeUserRating(
            episodeId = EPISODE_ID,
            userRating = 3L,
            ratedAt = dateTimeProvider.nowMillis(),
            pendingAction = PendingAction.NOTHING,
        )

        repository.observeEpisodeRating(EPISODE_ID).test {
            val rating = awaitItem()
            rating.userRating shouldBe 3
            rating.pendingAction shouldBe PendingAction.NOTHING
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun buildRepository(syncObserver: FakeSyncObserver): DefaultRatingsRepository =
        DefaultRatingsRepository(
            ratingsDao = ratingsDao,
            tvShowsDao = tvShowsDao,
            providerMetaDao = providerMetaDao,
            ratingsStore = ratingsStore,
            activeSource = { activeRemoteSource },
            syncObserver = syncObserver,
            dateTimeProvider = dateTimeProvider,
            logger = FakeLogger(),
        )

    private fun seedProviderShowId(showId: Long, provider: AccountProvider, externalId: Long) {
        database.tvshowExternalIdQueries.insert(
            showId = Id(showId),
            provider = provider.toDbProvider(),
            externalId = externalId.toString(),
        )
    }

    private fun seedShow(showId: Long): Long {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(showId),
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
        return database.tvShowQueries.getShowIdByTmdbId(Id(showId)).executeAsOne().id
    }

    private fun seedSeason(seasonId: Long) {
        val _ = database.seasonsQueries.upsert(
            id = Id(seasonId),
            show_id = Id(SHOW_ID),
            season_number = 1L,
            episode_count = 10L,
            title = "Season $seasonId",
            overview = "Overview",
            image_url = null,
        )
    }

    private fun seedEpisode(episodeId: Long, seasonId: Long) {
        val _ = database.episodesQueries.upsert(
            id = Id(episodeId),
            season_id = Id(seasonId),
            show_id = Id(SHOW_ID),
            title = "Episode $episodeId",
            overview = "Overview",
            runtime = 40L,
            vote_count = 10L,
            ratings = 8.0,
            episode_number = 1L,
            image_url = null,
            first_aired = null,
        )
    }

    private companion object {
        private const val SHOW_ID = 1L
        private const val TMDB_ID = 555L
        private const val SECOND_TMDB_ID = 777L
        private const val DISTINCT_TMDB_ID = 909L
        private const val SEASON_ID = 100L
        private const val EPISODE_ID = 200L
    }
}
