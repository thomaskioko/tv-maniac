package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.episodes.api.EpisodeWatchesDataSource
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.episodes.api.WatchedShowBatch
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultEpisodesDao
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultWatchedEpisodeDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.api.ReconciliationResult
import com.thomaskioko.tvmaniac.shows.api.ShowReconciler
import com.thomaskioko.tvmaniac.shows.api.ShowResolveOutcome
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeActivitySyncRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.watchstatus.testing.FakeShowWatchStatusRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.yield
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultWatchedEpisodeSyncRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val fakeDateTimeProvider = FakeDateTimeProvider()
    private val datastoreRepository = FakeDatastoreRepository()
    private val requestManagerRepository = FakeRequestManagerRepository()
    private val recordingDataSource = RecordingEpisodeWatchesDataSource()
    private val fakeShowReconciler = PassthroughShowReconciler()
    private val accountManager = FakeAccountManager().apply {
        setActiveProvider(AccountProvider.TRAKT)
    }
    private val syncRepository = FakeActivitySyncRepository()

    private lateinit var dao: DefaultWatchedEpisodeDao
    private lateinit var defaultWatchedEpisodeSyncRepository: DefaultWatchedEpisodeSyncRepository
    private var showId: Id<ShowId> = Id(0L)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        seedShow()
        dao = DefaultWatchedEpisodeDao(database, showIdResolver, dispatchers, fakeDateTimeProvider)
        defaultWatchedEpisodeSyncRepository = DefaultWatchedEpisodeSyncRepository(
            dao = dao,
            episodesDao = DefaultEpisodesDao(database, showIdResolver, dispatchers, fakeDateTimeProvider),
            activeSource = { recordingDataSource },
            accountManager = accountManager,
            datastoreRepository = datastoreRepository,
            lastRequestStore = EpisodeWatchesLastRequestStore(requestManagerRepository),
            syncRepository = syncRepository,
            logger = NoOpLogger,
            watchStatusRepository = FakeShowWatchStatusRepository(),
            showReconciler = fakeShowReconciler,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should hard-delete pending DELETE row after pushing to Trakt`() = runTest {
        seedDeletePending(seasonNumber = 1L, episodeNumber = 1L, traktId = 999L)

        defaultWatchedEpisodeSyncRepository.syncPendingEpisodes()

        recordingDataSource.removed shouldContainExactly listOf(1L to 1L)
        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldBeNull()
    }

    @Test
    fun `should hard-delete both synced and unsynced rows after pushing deletes`() = runTest {
        seedDeletePending(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L)
        seedDeletePending(seasonNumber = 1L, episodeNumber = 2L, traktId = null)

        defaultWatchedEpisodeSyncRepository.syncPendingEpisodes()

        recordingDataSource.removed shouldContainExactlyInAnyOrder listOf(1L to 1L, 1L to 2L)
        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldBeNull()
        readRow(seasonNumber = 1L, episodeNumber = 2L).shouldBeNull()
    }

    @Test
    fun `should upload pending entries exactly once given concurrent pending pushes`() = runTest {
        seedUploadPending(seasonNumber = 1L, episodeNumber = 1L)

        launch { defaultWatchedEpisodeSyncRepository.syncPendingEpisodes() }
        launch { defaultWatchedEpisodeSyncRepository.syncPendingEpisodes() }
        advanceUntilIdle()

        recordingDataSource.uploaded.size shouldBe 1
        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldNotBeNull().pending_action shouldBe PendingAction.NOTHING.value
    }

    @Test
    fun `should persist watched episodes in db given bulk sync batch carries tmdb boundary id`() = runTest {
        recordingDataSource.batchesToReturn = listOf(
            WatchedShowBatch(
                tmdbId = SHOW_ID,
                imdbId = null,
                title = null,
                providerShowId = null,
                episodes = listOf(
                    WatchedEpisodeEntry(
                        showId = SHOW_ID,
                        episodeId = null,
                        seasonNumber = 1L,
                        episodeNumber = 1L,
                        watchedAt = Instant.fromEpochMilliseconds(fakeDateTimeProvider.nowMillis()),
                        pendingAction = PendingAction.NOTHING,
                    ),
                    WatchedEpisodeEntry(
                        showId = SHOW_ID,
                        episodeId = null,
                        seasonNumber = 1L,
                        episodeNumber = 2L,
                        watchedAt = Instant.fromEpochMilliseconds(fakeDateTimeProvider.nowMillis()),
                        pendingAction = PendingAction.NOTHING,
                    ),
                ),
            ),
        )
        requestManagerRepository.requestValid = false
        syncRepository.setRemoteTimestamp(
            activityType = ActivityType.EPISODES_WATCHED,
            instant = kotlin.time.Clock.System.now(),
        )

        defaultWatchedEpisodeSyncRepository.syncAllWatchedEpisodes(forceRefresh = true)

        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldNotBeNull()
        readRow(seasonNumber = 1L, episodeNumber = 2L).shouldNotBeNull()
    }

    @Test
    fun `should drop bulk sync batch given show has no tmdb id in the boundary`() = runTest {
        recordingDataSource.batchesToReturn = listOf(
            WatchedShowBatch(
                tmdbId = null,
                imdbId = null,
                title = null,
                providerShowId = null,
                episodes = listOf(
                    WatchedEpisodeEntry(
                        showId = 0L,
                        episodeId = null,
                        seasonNumber = 1L,
                        episodeNumber = 1L,
                        watchedAt = Instant.fromEpochMilliseconds(fakeDateTimeProvider.nowMillis()),
                        pendingAction = PendingAction.NOTHING,
                    ),
                ),
            ),
        )
        requestManagerRepository.requestValid = false
        syncRepository.setRemoteTimestamp(
            activityType = ActivityType.EPISODES_WATCHED,
            instant = kotlin.time.Clock.System.now(),
        )

        defaultWatchedEpisodeSyncRepository.syncAllWatchedEpisodes(forceRefresh = true)

        database.watchedEpisodesQueries.getWatchedEpisodes(showId).executeAsList().size shouldBe 0
    }

    @Test
    fun `should skip unchanged show given bulk sync batch carries same last updated timestamp`() = runTest {
        requestManagerRepository.requestValid = false
        syncRepository.setRemoteTimestamp(
            activityType = ActivityType.EPISODES_WATCHED,
            instant = kotlin.time.Clock.System.now(),
        )
        val remoteUpdatedAt = Instant.fromEpochMilliseconds(PAST_MILLIS)
        recordingDataSource.batchesToReturn = listOf(
            watchedBatch(tmdbId = SHOW_ID, episodeNumbers = listOf(1L), lastUpdatedAt = remoteUpdatedAt),
        )
        defaultWatchedEpisodeSyncRepository.syncAllWatchedEpisodes(forceRefresh = true)

        recordingDataSource.batchesToReturn = listOf(
            watchedBatch(tmdbId = SHOW_ID, episodeNumbers = listOf(1L, 2L), lastUpdatedAt = remoteUpdatedAt),
        )
        defaultWatchedEpisodeSyncRepository.syncAllWatchedEpisodes(forceRefresh = true)

        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldNotBeNull()
        readRow(seasonNumber = 1L, episodeNumber = 2L).shouldBeNull()
    }

    @Test
    fun `should process show given bulk sync batch carries newer last updated timestamp`() = runTest {
        requestManagerRepository.requestValid = false
        syncRepository.setRemoteTimestamp(
            activityType = ActivityType.EPISODES_WATCHED,
            instant = kotlin.time.Clock.System.now(),
        )
        recordingDataSource.batchesToReturn = listOf(
            watchedBatch(
                tmdbId = SHOW_ID,
                episodeNumbers = listOf(1L),
                lastUpdatedAt = Instant.fromEpochMilliseconds(PAST_MILLIS),
            ),
        )
        defaultWatchedEpisodeSyncRepository.syncAllWatchedEpisodes(forceRefresh = true)

        recordingDataSource.batchesToReturn = listOf(
            watchedBatch(
                tmdbId = SHOW_ID,
                episodeNumbers = listOf(1L, 2L),
                lastUpdatedAt = Instant.fromEpochMilliseconds(PAST_MILLIS + 1_000L),
            ),
        )
        defaultWatchedEpisodeSyncRepository.syncAllWatchedEpisodes(forceRefresh = true)

        readRow(seasonNumber = 1L, episodeNumber = 2L).shouldNotBeNull()
    }

    @Test
    fun `should always process show given bulk sync batch carries no last updated timestamp`() = runTest {
        requestManagerRepository.requestValid = false
        syncRepository.setRemoteTimestamp(
            activityType = ActivityType.EPISODES_WATCHED,
            instant = kotlin.time.Clock.System.now(),
        )
        recordingDataSource.batchesToReturn = listOf(
            watchedBatch(tmdbId = SHOW_ID, episodeNumbers = listOf(1L)),
        )
        defaultWatchedEpisodeSyncRepository.syncAllWatchedEpisodes(forceRefresh = true)

        recordingDataSource.batchesToReturn = listOf(
            watchedBatch(tmdbId = SHOW_ID, episodeNumbers = listOf(1L, 2L)),
        )
        defaultWatchedEpisodeSyncRepository.syncAllWatchedEpisodes(forceRefresh = true)

        readRow(seasonNumber = 1L, episodeNumber = 2L).shouldNotBeNull()
    }

    @Test
    fun `should keep syncing remaining shows and hold checkpoint given one show fails during bulk sync`() = runTest {
        val thirdShowId = seedAdditionalShow(tmdbId = THIRD_SHOW_ID, traktId = THIRD_SHOW_TRAKT_ID)
        val throwingRepository = DefaultWatchedEpisodeSyncRepository(
            dao = dao,
            episodesDao = DefaultEpisodesDao(database, showIdResolver, dispatchers, fakeDateTimeProvider),
            activeSource = { recordingDataSource },
            accountManager = accountManager,
            datastoreRepository = datastoreRepository,
            lastRequestStore = EpisodeWatchesLastRequestStore(requestManagerRepository),
            syncRepository = syncRepository,
            logger = NoOpLogger,
            watchStatusRepository = FakeShowWatchStatusRepository(),
            showReconciler = ThrowingShowReconciler(failForTmdbId = POISONED_SHOW_ID),
        )
        recordingDataSource.batchesToReturn = listOf(
            watchedBatch(tmdbId = SHOW_ID),
            watchedBatch(tmdbId = POISONED_SHOW_ID),
            watchedBatch(tmdbId = THIRD_SHOW_ID),
        )
        requestManagerRepository.requestValid = false
        syncRepository.setRemoteTimestamp(
            activityType = ActivityType.EPISODES_WATCHED,
            instant = kotlin.time.Clock.System.now(),
        )

        shouldThrow<IllegalStateException> {
            throwingRepository.syncAllWatchedEpisodes(forceRefresh = true)
        }

        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldNotBeNull()
        readRow(seasonNumber = 1L, episodeNumber = 1L, forShowId = thirdShowId).shouldNotBeNull()
        syncRepository.markSyncedToCalls().size shouldBe 0
    }

    @Test
    fun `should fetch per-show watches on open when bulk ran recently but show was never per-show synced`() = runTest {
        requestManagerRepository.requestValid = true
        requestManagerRepository.requestExpired = true
        recordingDataSource.showWatchesToReturn = listOf(
            WatchedEpisodeEntry(
                showId = SHOW_ID,
                episodeId = null,
                seasonNumber = 1L,
                episodeNumber = 1L,
                watchedAt = Instant.fromEpochMilliseconds(fakeDateTimeProvider.nowMillis()),
                pendingAction = PendingAction.NOTHING,
            ),
        )

        defaultWatchedEpisodeSyncRepository.syncShowEpisodeWatches(showId = SHOW_ID, forceRefresh = false)

        recordingDataSource.getShowEpisodeWatchesCalls shouldContainExactly listOf(SHOW_ID)
        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldNotBeNull()
    }

    @Test
    fun `should remove a locally-watched episode the provider no longer reports on per-show sync`() = runTest {
        seedEpisode(seasonId = 100L, seasonNumber = 1L, episodeNumber = 1L, episodeTraktId = 555L)
        seedEpisode(seasonId = 100L, seasonNumber = 1L, episodeNumber = 2L, episodeTraktId = 556L)
        seedSynced(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L)
        seedSynced(seasonNumber = 1L, episodeNumber = 2L, traktId = 556L)
        recordingDataSource.showWatchesToReturn = listOf(
            WatchedEpisodeEntry(
                showId = SHOW_ID,
                episodeId = null,
                seasonNumber = 1L,
                episodeNumber = 1L,
                watchedAt = Instant.fromEpochMilliseconds(PAST_MILLIS),
                pendingAction = PendingAction.NOTHING,
            ),
        )

        defaultWatchedEpisodeSyncRepository.syncShowEpisodeWatches(showId = SHOW_ID, forceRefresh = true)

        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldNotBeNull()
        readRow(seasonNumber = 1L, episodeNumber = 2L).shouldBeNull()
    }

    private data class WatchedRow(
        val pending_action: String,
        val trakt_id: Long?,
    )

    private fun readRow(seasonNumber: Long, episodeNumber: Long, forShowId: Id<ShowId> = showId): WatchedRow? =
        database.watchedEpisodesQueries.getWatchedEpisodes(forShowId)
            .executeAsList()
            .firstOrNull { it.season_number == seasonNumber && it.episode_number == episodeNumber }
            ?.let { WatchedRow(pending_action = it.pending_action, trakt_id = it.trakt_id) }

    private fun watchedBatch(
        tmdbId: Long,
        episodeNumbers: List<Long> = listOf(1L),
        lastUpdatedAt: Instant? = null,
    ) = WatchedShowBatch(
        tmdbId = tmdbId,
        imdbId = null,
        title = null,
        providerShowId = null,
        episodes = episodeNumbers.map { episodeNumber ->
            WatchedEpisodeEntry(
                showId = tmdbId,
                episodeId = null,
                seasonNumber = 1L,
                episodeNumber = episodeNumber,
                watchedAt = Instant.fromEpochMilliseconds(fakeDateTimeProvider.nowMillis()),
                pendingAction = PendingAction.NOTHING,
            )
        },
        lastUpdatedAt = lastUpdatedAt,
    )

    private fun seedUploadPending(seasonNumber: Long, episodeNumber: Long) {
        database.watchedEpisodesQueries.upsertFromTrakt(
            show_id = showId,
            episode_id = null,
            season_number = seasonNumber,
            episode_number = episodeNumber,
            watched_at = fakeDateTimeProvider.nowMillis(),
            trakt_id = null,
            synced_at = null,
            pending_action = PendingAction.UPLOAD.value,
        )
    }

    private fun seedDeletePending(
        seasonNumber: Long,
        episodeNumber: Long,
        traktId: Long?,
    ) {
        database.watchedEpisodesQueries.upsertFromTrakt(
            show_id = showId,
            episode_id = null,
            season_number = seasonNumber,
            episode_number = episodeNumber,
            watched_at = fakeDateTimeProvider.nowMillis(),
            trakt_id = traktId,
            synced_at = traktId?.let { fakeDateTimeProvider.nowMillis() },
            pending_action = PendingAction.DELETE.value,
        )
    }

    private fun seedSynced(seasonNumber: Long, episodeNumber: Long, traktId: Long) {
        database.watchedEpisodesQueries.upsertFromTrakt(
            show_id = showId,
            episode_id = null,
            season_number = seasonNumber,
            episode_number = episodeNumber,
            watched_at = PAST_MILLIS,
            trakt_id = traktId,
            synced_at = PAST_MILLIS,
            pending_action = PendingAction.NOTHING.value,
        )
    }

    private fun seedEpisode(
        seasonId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        episodeTraktId: Long?,
    ) {
        database.seasonsQueries.upsert(
            id = Id(seasonId),
            show_id = showId,
            season_number = seasonNumber,
            episode_count = 12L,
            title = "Season $seasonNumber",
            overview = "",
            image_url = null,
        )
        database.episodesQueries.upsert(
            id = Id(seasonId * 100 + episodeNumber),
            season_id = Id(seasonId),
            show_id = showId,
            title = "Episode $episodeNumber",
            overview = "",
            runtime = null,
            vote_count = 100L,
            ratings = 8.0,
            episode_number = episodeNumber,
            image_url = null,
            first_aired = null,
        )
    }

    private fun seedShow() {
        database.tvShowQueries.upsert(
            tmdb_id = Id(SHOW_ID),
            name = "Test Show",
            overview = "",
            language = "en",
            year = "2024",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
        showId = showIdForTraktId(traktId = SHOW_TRAKT_ID, tmdbId = SHOW_ID)
    }

    private fun seedAdditionalShow(tmdbId: Long, traktId: Long): Id<ShowId> {
        database.tvShowQueries.upsert(
            tmdb_id = Id(tmdbId),
            name = "Test Show $tmdbId",
            overview = "",
            language = "en",
            year = "2024",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
        return showIdForTraktId(traktId = traktId, tmdbId = tmdbId)
    }

    @Test
    fun `should populate watched episodes and tvshow stub given simkl active sync with distinct ids`() = runTest {
        val simklSource = SimklEpisodeWatchesDataSourceStub()
        accountManager.setActiveProvider(AccountProvider.SIMKL)
        val simklReconciler = PassthroughShowReconciler()
        val simklRepository = DefaultWatchedEpisodeSyncRepository(
            dao = dao,
            episodesDao = DefaultEpisodesDao(database, showIdResolver, dispatchers, fakeDateTimeProvider),
            activeSource = { simklSource },
            accountManager = accountManager,
            datastoreRepository = datastoreRepository,
            lastRequestStore = EpisodeWatchesLastRequestStore(requestManagerRepository),
            syncRepository = syncRepository,
            logger = NoOpLogger,
            watchStatusRepository = FakeShowWatchStatusRepository(),
            showReconciler = simklReconciler,
        )

        simklSource.batchesToReturn = listOf(
            WatchedShowBatch(
                tmdbId = SHOW_ID,
                imdbId = null,
                title = "Emerald City",
                providerShowId = SIMKL_SHOW_ID.toString(),
                episodes = listOf(
                    WatchedEpisodeEntry(
                        showId = SHOW_ID,
                        episodeId = null,
                        seasonNumber = 1L,
                        episodeNumber = 1L,
                        watchedAt = Instant.fromEpochMilliseconds(fakeDateTimeProvider.nowMillis()),
                        pendingAction = PendingAction.NOTHING,
                    ),
                ),
            ),
        )
        requestManagerRepository.requestValid = false
        syncRepository.setRemoteTimestamp(
            activityType = ActivityType.EPISODES_WATCHED,
            instant = kotlin.time.Clock.System.now(),
        )

        simklRepository.syncAllWatchedEpisodes(forceRefresh = true)

        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldNotBeNull()

        val tvshow = database.tvShowQueries.tvshowByTmdbId(
            com.thomaskioko.tvmaniac.db.Id<com.thomaskioko.tvmaniac.db.TmdbId>(SHOW_ID),
        ).executeAsOneOrNull()
        tvshow.shouldNotBeNull()
    }

    private companion object {
        private const val SHOW_ID = 1L
        private const val SHOW_TRAKT_ID = 500L
        private const val POISONED_SHOW_ID = 2L
        private const val THIRD_SHOW_ID = 3L
        private const val THIRD_SHOW_TRAKT_ID = 502L
        private const val SIMKL_SHOW_ID = 583436L
        private const val PAST_MILLIS = 1_600_000_000_000L
    }
}

private class RecordingEpisodeWatchesDataSource : EpisodeWatchesDataSource {
    override val provider: AccountProvider = AccountProvider.TRAKT
    private val _removed = mutableListOf<Pair<Long, Long>>()
    val removed: List<Pair<Long, Long>> get() = _removed.toList()

    private val _uploaded = mutableListOf<List<WatchedEpisodeEntry>>()
    val uploaded: List<List<WatchedEpisodeEntry>> get() = _uploaded.toList()

    private val _getShowEpisodeWatchesCalls = mutableListOf<Long>()
    val getShowEpisodeWatchesCalls: List<Long> get() = _getShowEpisodeWatchesCalls.toList()
    var showWatchesToReturn: List<WatchedEpisodeEntry> = emptyList()
    var batchesToReturn: List<WatchedShowBatch> = emptyList()

    override suspend fun getShowEpisodeWatches(showId: Long): List<WatchedEpisodeEntry> {
        _getShowEpisodeWatchesCalls += showId
        return showWatchesToReturn
    }

    override suspend fun getAllWatchedShows(page: Int, limit: Int): List<WatchedShowBatch> {
        val offset = (page - 1) * limit
        return batchesToReturn.drop(offset).take(limit)
    }

    override suspend fun addEpisodeEntries(entries: List<WatchedEpisodeEntry>) {
        yield()
        _uploaded += entries
    }
    override suspend fun removeEpisodeEntries(entries: List<WatchedEpisodeEntry>) {
        _removed += entries.map { it.seasonNumber to it.episodeNumber }
    }
}

private object NoOpLogger : Logger {
    override fun error(message: String, throwable: Throwable) {}
    override fun error(tag: String, message: String) {}
}

private class ThrowingShowReconciler(private val failForTmdbId: Long) : ShowReconciler {
    override suspend fun reconcile(
        tmdbId: Long?,
        imdbId: String?,
        title: String?,
        providerShowId: String?,
        provider: AccountProvider,
        result: ReconciliationResult,
    ): Pair<ShowResolveOutcome, ReconciliationResult> {
        check(tmdbId != failForTmdbId) { "Reconciliation failed for show $tmdbId" }
        if (tmdbId == null) return ShowResolveOutcome.Skipped to result.copy(tmdbMissing = result.tmdbMissing + 1)
        return ShowResolveOutcome.Resolved(tmdbId) to result.copy(matched = result.matched + 1)
    }
}

private class PassthroughShowReconciler : ShowReconciler {
    override suspend fun reconcile(
        tmdbId: Long?,
        imdbId: String?,
        title: String?,
        providerShowId: String?,
        provider: AccountProvider,
        result: ReconciliationResult,
    ): Pair<ShowResolveOutcome, ReconciliationResult> {
        if (tmdbId == null) return ShowResolveOutcome.Skipped to result.copy(tmdbMissing = result.tmdbMissing + 1)
        return ShowResolveOutcome.Resolved(tmdbId) to result.copy(matched = result.matched + 1)
    }
}

private class SimklEpisodeWatchesDataSourceStub : EpisodeWatchesDataSource {
    override val provider: AccountProvider = AccountProvider.SIMKL
    var batchesToReturn: List<WatchedShowBatch> = emptyList()

    override suspend fun getShowEpisodeWatches(showId: Long): List<WatchedEpisodeEntry> = emptyList()

    override suspend fun getAllWatchedShows(page: Int, limit: Int): List<WatchedShowBatch> {
        val offset = (page - 1) * limit
        return batchesToReturn.drop(offset).take(limit)
    }

    override suspend fun addEpisodeEntries(entries: List<WatchedEpisodeEntry>) {}
    override suspend fun removeEpisodeEntries(entries: List<WatchedEpisodeEntry>) {}
}
