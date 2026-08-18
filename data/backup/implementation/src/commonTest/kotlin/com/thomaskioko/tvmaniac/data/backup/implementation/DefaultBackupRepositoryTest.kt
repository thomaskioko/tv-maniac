package com.thomaskioko.tvmaniac.data.backup.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.backup.api.BackupFailure
import com.thomaskioko.tvmaniac.data.backup.api.BackupFormat
import com.thomaskioko.tvmaniac.data.backup.api.BackupResult
import com.thomaskioko.tvmaniac.data.backup.testing.FakeBackupDestination
import com.thomaskioko.tvmaniac.data.backup.testing.FakeBackupDestinationBuilder
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.DbTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.WatchStatus
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.util.testing.FakeAppMetadata
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultBackupRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val datastoreRepository = FakeDatastoreRepository()
    private lateinit var repository: DefaultBackupRepository
    private var showId: Id<ShowId> = Id(0L)

    @BeforeTest
    fun setUp() {
        repository = DefaultBackupRepository(
            database = database,
            datastoreRepository = datastoreRepository,
            dateTimeProvider = FakeDateTimeProvider(),
            appMetadata = FakeAppMetadata.DEFAULT,
            dispatchers = dispatchers,
            destinationBuilder = FakeBackupDestinationBuilder(),
            syncObserver = FakeSyncObserver(),
            transactionRunner = DbTransactionRunner(database),
        )
        showId = insertShow()
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should carry the format version given a backup is created`() = runTest(testDispatcher) {
        val backup = repository.createBackup()

        backup.version shouldBe BackupFormat.VERSION
    }

    @Test
    fun `should include a followed show given it is followed`() = runTest(testDispatcher) {
        followShow()

        val backup = repository.createBackup()

        backup.shows shouldHaveSize 1
        backup.shows.first().tmdbId shouldBe BREAKING_BAD_TMDB_ID
        backup.shows.first().title shouldBe SHOW_TITLE
        backup.shows.first().followedAt.shouldNotBeNull()
    }

    @Test
    fun `should nest watched episodes under their show given episodes are watched`() = runTest(testDispatcher) {
        watchEpisode(season = 1, episode = 1)
        watchEpisode(season = 1, episode = 2)

        val backup = repository.createBackup()

        val episodes = backup.shows.single().watchedEpisodes
        episodes shouldHaveSize 2
        episodes.map { it.episode } shouldBe listOf(1L, 2L)
    }

    @Test
    fun `should exclude a show given its only follow is pending removal`() = runTest(testDispatcher) {
        followShow(pendingAction = PendingAction.DELETE)

        val backup = repository.createBackup()

        backup.shows.shouldBeEmpty()
    }

    @Test
    fun `should include watch status and rating given both are set`() = runTest(testDispatcher) {
        followShow()
        database.showWatchStatusQueries.upsert(
            showId = showId,
            status = WatchStatus.COMPLETED,
            lastWatchedAt = NOW,
            lastSyncedAt = NOW,
        )
        database.ratingsQueries.upsertShowUserRating(showId, 9L, NOW, PendingAction.NOTHING.value)

        val backup = repository.createBackup()

        backup.shows.single().watchStatus shouldBe "COMPLETED"
        backup.shows.single().rating.shouldNotBeNull().value shouldBe 9L
    }

    @Test
    fun `should write no local row id given a backup is serialized`() = runTest(testDispatcher) {
        followShow()
        watchEpisode(season = 1, episode = 1)
        database.showWatchStatusQueries.upsert(showId, WatchStatus.WATCHING, NOW, NOW)

        val contents = BackupJson.encode(repository.createBackup())

        contents shouldContain "\"tmdbId\": $BREAKING_BAD_TMDB_ID"
        contents shouldNotContain "showId"
        contents shouldNotContain "episodeId"
        contents shouldNotContain "seasonId"
    }

    @Test
    fun `should omit device state and subscription state given preferences are exported`() = runTest(testDispatcher) {
        val contents = BackupJson.encode(repository.createBackup())

        contents shouldNotContain "accountType"
        contents shouldNotContain "lastSyncTimestamp"
        contents shouldNotContain "lastTokenRefresh"
        contents shouldNotContain "notificationPermissionAsked"
        contents shouldNotContain "lastTraktUserId"
        contents shouldNotContain "debugMenuEnabled"
    }

    @Test
    fun `should round trip given a backup is encoded and decoded`() = runTest(testDispatcher) {
        followShow()
        watchEpisode(season = 2, episode = 5)

        val backup = repository.createBackup()

        BackupJson.decode(BackupJson.encode(backup)) shouldBe backup
    }

    @Test
    fun `should report counts given the file is written and read back`() = runTest(testDispatcher) {
        followShow()
        watchEpisode(season = 1, episode = 1)
        val destination = FakeBackupDestination()

        val result = repository.writeBackup(destination)

        result shouldBe BackupResult.Written(showCount = 1, episodeCount = 1)
        destination.written.shouldNotBeNull()
    }

    @Test
    fun `should report failure given the destination cannot be written`() = runTest(testDispatcher) {
        val destination = FakeBackupDestination()
        destination.setWriteException(IllegalStateException("no permission"))

        val result = repository.writeBackup(destination)

        result.shouldBeInstanceOf<BackupResult.Failed>().reason shouldBe BackupFailure.WriteFailed
    }

    @Test
    fun `should report failure given the file reads back as something else`() = runTest(testDispatcher) {
        val destination = FakeBackupDestination()
        destination.setReadException(IllegalStateException("truncated"))

        val result = repository.writeBackup(destination)

        result.shouldBeInstanceOf<BackupResult.Failed>().reason shouldBe BackupFailure.VerificationFailed
    }

    @Test
    fun `should exclude a rating given it is pending removal`() = runTest(testDispatcher) {
        followShow()
        database.ratingsQueries.upsertShowUserRating(showId, 7L, NOW, PendingAction.DELETE.value)

        val backup = repository.createBackup()

        backup.shows.single().rating.shouldBeNull()
    }

    private fun insertShow(): Id<ShowId> {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(BREAKING_BAD_TMDB_ID),
            name = SHOW_TITLE,
            overview = "overview",
            language = "en",
            year = "2008",
            ratings = 9.0,
            vote_count = 100,
            genres = emptyList(),
            status = "Ended",
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
        return database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(BREAKING_BAD_TMDB_ID)).executeAsOne()
    }

    private fun followShow(pendingAction: PendingAction = PendingAction.NOTHING) {
        database.followedShowsQueries.upsert(
            showId = showId,
            tmdbId = Id<TmdbId>(BREAKING_BAD_TMDB_ID),
            followedAt = NOW,
            pendingAction = pendingAction.value,
        )
    }

    private fun watchEpisode(season: Long, episode: Long) {
        database.watchedEpisodesQueries.upsert(
            showId,
            null,
            season,
            episode,
            NOW,
            PendingAction.NOTHING.value,
        )
    }

    private companion object {
        private const val BREAKING_BAD_TMDB_ID = 1396L
        private const val SHOW_TITLE = "Breaking Bad"
        private const val NOW = 1_700_000_000_000L
    }
}
