package com.thomaskioko.tvmaniac.data.logout.implementation

import com.thomaskioko.tvmaniac.core.base.coroutines.SyncCoroutineScope
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.ratings.implementation.DefaultProviderMetaDao
import com.thomaskioko.tvmaniac.data.ratings.implementation.DefaultRatingsDao
import com.thomaskioko.tvmaniac.data.rewatch.implementation.DefaultRewatchSessionDao
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.DbTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Provider
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.WatchStatus
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultLogoutHandlerTest : BaseDatabaseTest() {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val fakeUserRepository = FakeUserRepository()
    private val fakeTraktActivityRepository = FakeTraktActivityRepository()
    private val fakeActivitySyncRepository = FakeActivitySyncRepository()
    private val fakeRequestManagerRepository = FakeRequestManagerRepository()
    private val syncCoroutineScope = SyncCoroutineScope(dispatchers)

    private lateinit var cleaner: DefaultLogoutHandler
    private lateinit var ratingsDao: DefaultRatingsDao
    private lateinit var providerMetaDao: DefaultProviderMetaDao
    private lateinit var rewatchSessionDao: DefaultRewatchSessionDao
    private var showIdForBreakingBad: Id<ShowId> = Id(0L)
    private var showIdForTheWire: Id<ShowId> = Id(0L)

    @BeforeTest
    fun setUp() {
        ratingsDao = DefaultRatingsDao(database, dispatchers)
        providerMetaDao = DefaultProviderMetaDao(database, dispatchers)
        rewatchSessionDao = DefaultRewatchSessionDao(database, dispatchers)
        cleaner = DefaultLogoutHandler(
            syncCoroutineScope = syncCoroutineScope,
            userRepository = fakeUserRepository,
            traktActivityRepository = fakeTraktActivityRepository,
            syncRepository = fakeActivitySyncRepository,
            requestManagerRepository = fakeRequestManagerRepository,
            ratingsDao = ratingsDao,
            providerMetaDao = providerMetaDao,
            rewatchSessionDao = rewatchSessionDao,
            database = database,
            transactionRunner = DbTransactionRunner(database),
        )

        showIdForBreakingBad = insertTvShow(traktId = BREAKING_BAD_TRAKT_ID, tmdbId = BREAKING_BAD_TMDB_ID)
        showIdForTheWire = insertTvShow(traktId = THE_WIRE_TRAKT_ID, tmdbId = THE_WIRE_TMDB_ID)
        addUserState()
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should keep watched episodes given the user logs out`() = runTest(testDispatcher) {
        cleaner.clearAccountData()

        database.watchedEpisodesQueries.getWatchedEpisodes(showIdForBreakingBad).executeAsList() shouldHaveSize 2
    }

    @Test
    fun `should keep pending uploads given the user logs out`() = runTest(testDispatcher) {
        cleaner.clearAccountData()

        database.watchedEpisodesQueries.countPendingActions().executeAsOne() shouldBe 1L
    }

    @Test
    fun `should keep followed shows given the user logs out`() = runTest(testDispatcher) {
        cleaner.clearAccountData()

        database.followedShowsQueries.countEntries().executeAsOne() shouldBe 1L
    }

    @Test
    fun `should keep continue watching given the user logs out`() = runTest(testDispatcher) {
        cleaner.clearAccountData()

        database.continueWatchingQueries.entries().executeAsList().shouldNotBeEmpty()
    }

    @Test
    fun `should keep watch status given the user logs out`() = runTest(testDispatcher) {
        cleaner.clearAccountData()

        database.showWatchStatusQueries.statusForShow(showIdForBreakingBad).executeAsOneOrNull().shouldNotBeNull()
    }

    @Test
    fun `should keep user ratings given the user logs out`() = runTest(testDispatcher) {
        cleaner.clearAccountData()

        database.ratingsQueries.observeShowRating(showIdForBreakingBad).executeAsOneOrNull().shouldNotBeNull()
    }

    @Test
    fun `should keep rewatch sessions given the user logs out`() = runTest(testDispatcher) {
        cleaner.clearAccountData()

        database.rewatchQueries.sessionsForShow(showIdForBreakingBad).executeAsList().shouldNotBeEmpty()
    }

    @Test
    fun `should clear watched show sync log given the user logs out`() = runTest(testDispatcher) {
        cleaner.clearAccountData()

        database.watchedShowSyncLogQueries
            .getRemoteUpdatedAt(showIdForBreakingBad, "TRAKT")
            .executeAsOneOrNull()
            .shouldBeNull()
    }

    @Test
    fun `should clear favorite shows given the user logs out`() = runTest(testDispatcher) {
        cleaner.clearAccountData()

        database.favoritesQueries.favoriteShows().executeAsList().shouldBeEmpty()
    }

    @Test
    fun `should clear trakt lists given the user logs out`() = runTest(testDispatcher) {
        cleaner.clearAccountData()

        database.traktListsQueries.selectAll().executeAsList().shouldBeEmpty()
        database.traktListShowsQueries.countActiveByListId().executeAsList().shouldBeEmpty()
    }

    @Test
    fun `should clear calendar entries given the user logs out`() = runTest(testDispatcher) {
        cleaner.clearAccountData()

        database.calendarQueries.hasEntriesInRange(0L, Long.MAX_VALUE).executeAsOne() shouldBe false
    }

    @Test
    fun `should clear provider metadata given the user logs out`() = runTest(testDispatcher) {
        cleaner.clearAccountData()

        database.tvshowProviderMetaQueries
            .providerRating(showIdForBreakingBad, Provider.TRAKT)
            .executeAsOneOrNull()
            .shouldBeNull()
    }

    @Test
    fun `should clear the current account given the user logs out`() = runTest(testDispatcher) {
        cleaner.clearAccountData()

        fakeUserRepository.getCurrentUser().shouldBeNull()
    }

    @Test
    fun `should clear trakt activity given the user logs out`() = runTest(testDispatcher) {
        cleaner.clearAccountData()

        fakeTraktActivityRepository.clearAllInvocationCount() shouldBe 1
    }

    @Test
    fun `should clear activity sync given the user logs out`() = runTest(testDispatcher) {
        cleaner.clearAccountData()

        fakeActivitySyncRepository.clearAllCallCount() shouldBe 1
    }

    @Test
    fun `should cancel in-flight sync work given the user logs out`() = runTest(testDispatcher) {
        val job = syncCoroutineScope.scope.launch { awaitCancellation() }
        job.isActive shouldBe true

        cleaner.clearAccountData()

        job.isCancelled shouldBe true
    }

    @Test
    fun `should clear watched episodes given the user switches accounts`() = runTest(testDispatcher) {
        cleaner.clearAccountAndTrackingData()

        database.watchedEpisodesQueries.getWatchedEpisodes(showIdForBreakingBad).executeAsList().shouldBeEmpty()
    }

    @Test
    fun `should clear followed shows given the user switches accounts`() = runTest(testDispatcher) {
        cleaner.clearAccountAndTrackingData()

        database.followedShowsQueries.countEntries().executeAsOne() shouldBe 0L
    }

    @Test
    fun `should clear continue watching given the user switches accounts`() = runTest(testDispatcher) {
        cleaner.clearAccountAndTrackingData()

        database.continueWatchingQueries.entries().executeAsList().shouldBeEmpty()
    }

    @Test
    fun `should clear watch status given the user switches accounts`() = runTest(testDispatcher) {
        cleaner.clearAccountAndTrackingData()

        database.showWatchStatusQueries.statusForShow(showIdForBreakingBad).executeAsOneOrNull().shouldBeNull()
    }

    @Test
    fun `should clear show ratings given the user switches accounts`() = runTest(testDispatcher) {
        cleaner.clearAccountAndTrackingData()

        database.ratingsQueries.showRatingsWithUploadPendingAction().executeAsList().shouldBeEmpty()
        database.ratingsQueries.observeShowRating(showIdForBreakingBad).executeAsOneOrNull().shouldBeNull()
    }

    @Test
    fun `should clear season and episode ratings given the user switches accounts`() = runTest(testDispatcher) {
        cleaner.clearAccountAndTrackingData()

        database.ratingsQueries.seasonRatingsWithUploadPendingAction().executeAsList().shouldBeEmpty()
        database.ratingsQueries.episodeRatingsWithUploadPendingAction().executeAsList().shouldBeEmpty()
    }

    @Test
    fun `should clear rewatch sessions given the user switches accounts`() = runTest(testDispatcher) {
        cleaner.clearAccountAndTrackingData()

        database.rewatchQueries.sessionsForShow(showIdForBreakingBad).executeAsList().shouldBeEmpty()
    }

    @Test
    fun `should reset the rewatch count given the user switches accounts`() = runTest(testDispatcher) {
        cleaner.clearAccountAndTrackingData()

        rewatchSessionDao.observeEpisodeRewatches(REWATCHED_EPISODE_ID).first() shouldBe 0L
    }

    @Test
    fun `should clear favorites lists and calendar given the user switches accounts`() = runTest(testDispatcher) {
        cleaner.clearAccountAndTrackingData()

        database.favoritesQueries.favoriteShows().executeAsList().shouldBeEmpty()
        database.traktListsQueries.selectAll().executeAsList().shouldBeEmpty()
        database.calendarQueries.hasEntriesInRange(0L, Long.MAX_VALUE).executeAsOne() shouldBe false
    }

    @Test
    fun `should keep tvshow catalog rows given the user switches accounts`() = runTest(testDispatcher) {
        cleaner.clearAccountAndTrackingData()

        database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(BREAKING_BAD_TMDB_ID)).executeAsOneOrNull() shouldBe showIdForBreakingBad
        database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(THE_WIRE_TMDB_ID)).executeAsOneOrNull() shouldBe showIdForTheWire
    }

    private fun addUserState() {
        val now = Clock.System.now().toEpochMilliseconds()

        database.watchedEpisodesQueries.upsert(
            showIdForBreakingBad,
            null,
            1L,
            1L,
            now,
            PendingAction.NOTHING.value,
        )

        database.watchedEpisodesQueries.upsert(
            showIdForBreakingBad,
            null,
            1L,
            2L,
            now,
            PendingAction.UPLOAD.value,
        )

        database.watchedShowSyncLogQueries.upsert(
            show_id = showIdForBreakingBad,
            provider = "TRAKT",
            remote_updated_at = now,
        )

        database.followedShowsQueries.upsert(
            showId = showIdForBreakingBad,
            tmdbId = Id<TmdbId>(BREAKING_BAD_TMDB_ID),
            followedAt = now,
            pendingAction = PendingAction.NOTHING.value,
        )

        database.continueWatchingQueries.upsert(
            showId = showIdForBreakingBad,
            tmdbId = Id<TmdbId>(BREAKING_BAD_TMDB_ID),
            airedEpisodes = 62L,
            completedCount = 56L,
            lastWatchedAt = now,
            lastUpdatedAt = now,
            title = "Breaking Bad",
            year = 2008L,
        )

        database.favoritesQueries.upsert(
            showIdForBreakingBad,
            1L,
            "2024-01-01T00:00:00Z",
        )

        database.traktListsQueries.upsert(
            TRAKT_LIST_ID,
            "my-list",
            "My List",
            null,
            1L,
            "2024-01-01T00:00:00Z",
        )

        database.traktListShowsQueries.upsert(
            TRAKT_LIST_ID,
            BREAKING_BAD_TRAKT_ID,
            "2024-01-01T00:00:00Z",
            PendingAction.NOTHING.value,
        )

        database.showWatchStatusQueries.upsert(
            showId = showIdForBreakingBad,
            status = WatchStatus.WATCHING,
            lastWatchedAt = now,
            lastSyncedAt = now,
        )

        database.calendarQueries.upsert(
            show_id = showIdForBreakingBad,
            trakt_id = null,
            season_number = 1L,
            episode_number = 1L,
            episode_title = "Pilot",
            air_date = now,
            show_title = "Breaking Bad",
            show_poster_path = null,
            network = null,
            runtime = null,
            overview = null,
            rating = null,
            votes = null,
        )

        ratingsDao.upsertShowUserRating(
            showId = showIdForBreakingBad.id,
            userRating = 9L,
            ratedAt = now,
            pendingAction = PendingAction.UPLOAD,
        )

        providerMetaDao.upsertProviderRating(
            showId = showIdForBreakingBad.id,
            provider = Provider.TRAKT,
            rating = 9.3,
            voteCount = 1000L,
            lastSyncedAt = now,
        )

        database.seasonsQueries.upsert(
            id = Id(REWATCHED_SEASON_ID),
            show_id = showIdForBreakingBad,
            season_number = 1L,
            episode_count = 10L,
            title = "Season 1",
            overview = "Overview",
            image_url = null,
        )

        database.episodesQueries.upsert(
            id = Id(REWATCHED_EPISODE_ID),
            season_id = Id(REWATCHED_SEASON_ID),
            show_id = showIdForBreakingBad,
            title = "Pilot",
            overview = "Overview",
            runtime = 40L,
            vote_count = 10L,
            ratings = 8.0,
            episode_number = 1L,
            image_url = null,
            first_aired = null,
        )

        val rewatchSessionId = rewatchSessionDao.openSession(showId = showIdForBreakingBad.id, startedAt = now)
        rewatchSessionDao.addEpisodeToSession(sessionId = rewatchSessionId, episodeId = REWATCHED_EPISODE_ID, watchedAt = now)
    }

    private fun insertTvShow(traktId: Long, tmdbId: Long): Id<ShowId> {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(tmdbId),
            name = "show-$traktId",
            overview = "overview",
            language = "en",
            year = "2020-01-01",
            ratings = 8.0,
            vote_count = 100,
            genres = emptyList(),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
        return showIdForTraktId(traktId = traktId, tmdbId = tmdbId)
    }

    private companion object {
        private const val BREAKING_BAD_TRAKT_ID = 1388L
        private const val BREAKING_BAD_TMDB_ID = 1396L
        private const val THE_WIRE_TRAKT_ID = 1429L
        private const val THE_WIRE_TMDB_ID = 1438L
        private const val TRAKT_LIST_ID = 101L
        private const val REWATCHED_SEASON_ID = 100L
        private const val REWATCHED_EPISODE_ID = 200L
    }
}
