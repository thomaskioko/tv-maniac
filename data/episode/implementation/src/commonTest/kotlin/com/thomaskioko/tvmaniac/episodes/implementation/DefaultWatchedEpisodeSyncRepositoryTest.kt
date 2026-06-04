package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.episodes.api.EpisodeWatchesDataSource
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultEpisodesDao
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultWatchedEpisodeDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeActivitySyncRepository
import com.thomaskioko.tvmaniac.traktauth.api.AuthError
import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TokenRefreshResult
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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
    private val traktAuthRepository = AuthorizedFakeTraktAuthRepository()
    private val recordingDataSource = RecordingEpisodeWatchesDataSource()
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
            dataSource = recordingDataSource,
            datastoreRepository = datastoreRepository,
            lastRequestStore = EpisodeWatchesLastRequestStore(requestManagerRepository),
            syncRepository = syncRepository,
            traktAuthRepository = traktAuthRepository,
            dateTimeProvider = fakeDateTimeProvider,
            logger = NoOpLogger,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should hard-delete pending DELETE row that was never synced to Trakt`() = runTest {
        seedDeletePending(seasonNumber = 1L, episodeNumber = 1L, traktId = null)

        defaultWatchedEpisodeSyncRepository.syncPendingEpisodes()

        recordingDataSource.removedTraktIds.shouldBeEmpty()
        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldBeNull()
    }

    @Test
    fun `should mark pending DELETE row as SYNCED_DELETE when trakt_id is present`() = runTest {
        seedEpisode(seasonId = 100L, seasonNumber = 1L, episodeNumber = 1L, episodeTraktId = 999L)
        seedDeletePending(seasonNumber = 1L, episodeNumber = 1L, traktId = 999L)

        defaultWatchedEpisodeSyncRepository.syncPendingEpisodes()

        recordingDataSource.removedTraktIds shouldContainExactly listOf(999L)
        val row = readRow(seasonNumber = 1L, episodeNumber = 1L)
        row.shouldNotBeNull()
        row.pending_action shouldBe PendingAction.SYNCED_DELETE.value
    }

    @Test
    fun `should hard-delete unsynced rows and tombstone synced rows in same batch`() = runTest {
        seedEpisode(seasonId = 100L, seasonNumber = 1L, episodeNumber = 1L, episodeTraktId = 555L)
        seedEpisode(seasonId = 101L, seasonNumber = 1L, episodeNumber = 2L, episodeTraktId = null)
        seedDeletePending(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L)
        seedDeletePending(seasonNumber = 1L, episodeNumber = 2L, traktId = null)

        defaultWatchedEpisodeSyncRepository.syncPendingEpisodes()

        recordingDataSource.removedTraktIds shouldContainExactly listOf(555L)
        readRow(seasonNumber = 1L, episodeNumber = 1L)
            .shouldNotBeNull().pending_action shouldBe PendingAction.SYNCED_DELETE.value
        readRow(seasonNumber = 1L, episodeNumber = 2L).shouldBeNull()
    }

    @Test
    fun `should fetch per-show watches on open when bulk ran recently but show was never per-show synced`() = runTest {
        requestManagerRepository.requestValid = true
        requestManagerRepository.requestExpired = true
        recordingDataSource.showWatchesToReturn = listOf(
            WatchedEpisodeEntry(
                showTraktId = SHOW_ID,
                episodeId = null,
                seasonNumber = 1L,
                episodeNumber = 1L,
                watchedAt = Instant.fromEpochMilliseconds(fakeDateTimeProvider.nowMillis()),
                pendingAction = PendingAction.NOTHING,
            ),
        )

        defaultWatchedEpisodeSyncRepository.syncShowEpisodeWatches(showTraktId = SHOW_ID, forceRefresh = false)

        recordingDataSource.getShowEpisodeWatchesCalls shouldContainExactly listOf(SHOW_ID)
        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldNotBeNull()
    }

    private data class WatchedRow(
        val pending_action: String,
        val trakt_id: Long?,
    )

    private fun readRow(seasonNumber: Long, episodeNumber: Long): WatchedRow? =
        database.watchedEpisodesQueries.getWatchedEpisodes(showId)
            .executeAsList()
            .firstOrNull { it.season_number == seasonNumber && it.episode_number == episodeNumber }
            ?.let { WatchedRow(pending_action = it.pending_action, trakt_id = it.trakt_id) }

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
            trakt_id = episodeTraktId,
            first_aired = null,
        )
    }

    private fun seedShow() {
        database.tvShowQueries.upsert(
            trakt_id = Id(SHOW_ID),
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
        showId = seedExternalId(SHOW_ID)
    }

    private companion object {
        private const val SHOW_ID = 1L
    }
}

private class RecordingEpisodeWatchesDataSource : EpisodeWatchesDataSource {
    private val _removedTraktIds = mutableListOf<Long>()
    val removedTraktIds: List<Long> get() = _removedTraktIds.toList()

    private val _getShowEpisodeWatchesCalls = mutableListOf<Long>()
    val getShowEpisodeWatchesCalls: List<Long> get() = _getShowEpisodeWatchesCalls.toList()
    var showWatchesToReturn: List<WatchedEpisodeEntry> = emptyList()

    override suspend fun getShowEpisodeWatches(showTraktId: Long): List<WatchedEpisodeEntry> {
        _getShowEpisodeWatchesCalls += showTraktId
        return showWatchesToReturn
    }
    override suspend fun getAllWatchedShows(
        page: Int,
        limit: Int,
    ): List<com.thomaskioko.tvmaniac.episodes.api.WatchedShowBatch> = emptyList()
    override suspend fun addEpisodeWatches(watches: List<WatchedEpisodeEntry>) {}
    override suspend fun removeEpisodeWatches(episodeTraktIds: List<Long>) {
        _removedTraktIds += episodeTraktIds
    }
}

private object NoOpLogger : Logger {
    override fun error(message: String, throwable: Throwable) {}
    override fun error(tag: String, message: String) {}
}

private class AuthorizedFakeTraktAuthRepository : TraktAuthRepository {
    private val _state = MutableStateFlow(TraktAuthState.LOGGED_IN)
    private val _authState = MutableStateFlow<AuthState?>(
        AuthState(accessToken = "test-access", refreshToken = "test-refresh", isAuthorized = true),
    )
    private val _authError = MutableStateFlow<AuthError?>(null)
    private val _loginEvents = kotlinx.coroutines.flow.MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override val state: Flow<TraktAuthState> = _state.asStateFlow()
    override val authState: Flow<AuthState?> = _authState.asStateFlow()
    override val authError: Flow<AuthError?> = _authError.asStateFlow()
    override val loginEvents: kotlinx.coroutines.flow.SharedFlow<Unit> = _loginEvents

    override fun isLoggedIn(): Boolean = true
    override suspend fun getAuthState(): AuthState? = _authState.value
    override suspend fun refreshTokens(): TokenRefreshResult = TokenRefreshResult.NotLoggedIn
    override suspend fun logout() {}
    override suspend fun saveTokens(accessToken: String, refreshToken: String, expiresAtSeconds: Long) {}
    override suspend fun setAuthError(error: AuthError?) {}
}
