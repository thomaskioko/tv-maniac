package com.thomaskioko.tvmaniac.data.logout.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.ratings.implementation.DefaultProviderMetaDao
import com.thomaskioko.tvmaniac.data.ratings.implementation.DefaultRatingsDao
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
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private lateinit var cleaner: DefaultLogoutHandler
    private lateinit var ratingsDao: DefaultRatingsDao
    private lateinit var providerMetaDao: DefaultProviderMetaDao
    private var showIdForBreakingBad: Id<ShowId> = Id(0L)
    private var showIdForTheWire: Id<ShowId> = Id(0L)

    @BeforeTest
    fun setUp() {
        ratingsDao = DefaultRatingsDao(database, dispatchers)
        providerMetaDao = DefaultProviderMetaDao(database, dispatchers)
        cleaner = DefaultLogoutHandler(
            userRepository = fakeUserRepository,
            traktActivityRepository = fakeTraktActivityRepository,
            syncRepository = fakeActivitySyncRepository,
            requestManagerRepository = fakeRequestManagerRepository,
            ratingsDao = ratingsDao,
            providerMetaDao = providerMetaDao,
            database = database,
            transactionRunner = DbTransactionRunner(database),
        )

        showIdForBreakingBad = insertTvShow(traktId = BREAKING_BAD_TRAKT_ID, tmdbId = BREAKING_BAD_TMDB_ID)
        showIdForTheWire = insertTvShow(traktId = THE_WIRE_TRAKT_ID, tmdbId = THE_WIRE_TMDB_ID)
        seedUserState()
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should empty watched_episodes given clear called`() = runTest(testDispatcher) {
        cleaner.clear()

        database.watchedEpisodesQueries.getWatchedEpisodes(showIdForBreakingBad).executeAsList().shouldBeEmpty()
    }

    @Test
    fun `should empty watched_show_sync_log given clear called`() = runTest(testDispatcher) {
        cleaner.clear()

        database.watchedShowSyncLogQueries
            .getRemoteUpdatedAt(showIdForBreakingBad, "TRAKT")
            .executeAsOneOrNull()
            .shouldBeNull()
    }

    @Test
    fun `should empty followed_shows given clear called`() = runTest(testDispatcher) {
        cleaner.clear()

        database.followedShowsQueries.countEntries().executeAsOne() shouldBe 0L
    }

    @Test
    fun `should empty continue_watching given clear called`() = runTest(testDispatcher) {
        cleaner.clear()

        database.continueWatchingQueries.entries().executeAsList().shouldBeEmpty()
    }

    @Test
    fun `should empty favorite_shows given clear called`() = runTest(testDispatcher) {
        cleaner.clear()

        database.favoritesQueries.favoriteShows().executeAsList().shouldBeEmpty()
    }

    @Test
    fun `should empty trakt_list_shows given clear called`() = runTest(testDispatcher) {
        cleaner.clear()

        database.traktListShowsQueries.countActiveByListId().executeAsList().shouldBeEmpty()
    }

    @Test
    fun `should empty trakt_lists given clear called`() = runTest(testDispatcher) {
        cleaner.clear()

        database.traktListsQueries.selectAll().executeAsList().shouldBeEmpty()
    }

    @Test
    fun `should empty show_watch_status given clear called`() = runTest(testDispatcher) {
        cleaner.clear()

        database.showWatchStatusQueries.statusForShow(showIdForBreakingBad).executeAsOneOrNull().shouldBeNull()
    }

    @Test
    fun `should empty calendar_entry given clear called`() = runTest(testDispatcher) {
        cleaner.clear()

        database.calendarQueries.hasEntriesInRange(0L, Long.MAX_VALUE).executeAsOne() shouldBe false
    }

    @Test
    fun `should empty show_ratings given clear called`() = runTest(testDispatcher) {
        cleaner.clear()

        database.ratingsQueries.showRatingsWithUploadPendingAction().executeAsList().shouldBeEmpty()
        database.ratingsQueries.observeShowRating(showIdForBreakingBad).executeAsOneOrNull().shouldBeNull()
    }

    @Test
    fun `should empty season_ratings and episode_ratings given clear called`() = runTest(testDispatcher) {
        cleaner.clear()

        database.ratingsQueries.seasonRatingsWithUploadPendingAction().executeAsList().shouldBeEmpty()
        database.ratingsQueries.episodeRatingsWithUploadPendingAction().executeAsList().shouldBeEmpty()
    }

    @Test
    fun `should empty tvshow_provider_meta given clear called`() = runTest(testDispatcher) {
        cleaner.clear()

        database.tvshowProviderMetaQueries.providerRating(showIdForBreakingBad, Provider.TRAKT).executeAsOneOrNull().shouldBeNull()
    }

    @Test
    fun `should preserve tvshow catalog rows given clear called`() = runTest(testDispatcher) {
        cleaner.clear()

        database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(BREAKING_BAD_TMDB_ID)).executeAsOneOrNull() shouldBe showIdForBreakingBad
        database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(THE_WIRE_TMDB_ID)).executeAsOneOrNull() shouldBe showIdForTheWire
    }

    @Test
    fun `should clear user data given clear called`() = runTest(testDispatcher) {
        cleaner.clear()

        fakeUserRepository.getCurrentUser().shouldBeNull()
    }

    @Test
    fun `should clear trakt activity given clear called`() = runTest(testDispatcher) {
        cleaner.clear()

        fakeTraktActivityRepository.clearAllInvocationCount() shouldBe 1
    }

    @Test
    fun `should clear activity sync given clear called`() = runTest(testDispatcher) {
        cleaner.clear()

        fakeActivitySyncRepository.clearAllCallCount() shouldBe 1
    }

    private fun seedUserState() {
        val now = Clock.System.now().toEpochMilliseconds()

        database.watchedEpisodesQueries.upsert(
            showIdForBreakingBad,
            null,
            1L,
            1L,
            now,
            PendingAction.NOTHING.value,
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
    }
}
