package com.thomaskioko.tvmaniac.data.backup.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.backup.api.BackupEpisode
import com.thomaskioko.tvmaniac.data.backup.api.BackupFile
import com.thomaskioko.tvmaniac.data.backup.api.BackupFormat
import com.thomaskioko.tvmaniac.data.backup.api.BackupRating
import com.thomaskioko.tvmaniac.data.backup.api.BackupSeason
import com.thomaskioko.tvmaniac.data.backup.api.BackupSeasonRating
import com.thomaskioko.tvmaniac.data.backup.api.BackupShow
import com.thomaskioko.tvmaniac.data.backup.api.BackupWatchedEpisode
import com.thomaskioko.tvmaniac.data.backup.api.RestoreFailure
import com.thomaskioko.tvmaniac.data.backup.api.RestoreResult
import com.thomaskioko.tvmaniac.data.backup.testing.FakeBackupDestination
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.DbTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SeasonId
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
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultBackupRepositoryRestoreTest : BaseDatabaseTest() {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val datastoreRepository = FakeDatastoreRepository()
    private val syncObserver = FakeSyncObserver()
    private val destination = FakeBackupDestination()

    private lateinit var repository: DefaultBackupRepository

    @BeforeTest
    fun setUp() {
        repository = buildRepository(DbTransactionRunner(database))
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should decline given a sync is in flight`() = runTest(testDispatcher) {
        syncObserver.setSyncing(true)

        val result = repository.restoreBackup(fileWith(breakingBad()))

        result.shouldBeInstanceOf<RestoreResult.Failed>().reason shouldBe RestoreFailure.SyncInProgress
    }

    @Test
    fun `should change nothing given a sync is in flight`() = runTest(testDispatcher) {
        val showId = insertShow(BREAKING_BAD_TMDB_ID)
        followShow(showId)
        syncObserver.setSyncing(true)

        repository.restoreBackup(fileWith(BackupShow(tmdbId = OTHER_TMDB_ID, title = "Better Call Saul")))

        database.followedShowsQueries.entries().executeAsList() shouldHaveSize 1
    }

    @Test
    fun `should restore a followed show and its watched episodes`() = runTest(testDispatcher) {
        val result = repository.restoreBackup(fileWith(breakingBad()))

        val summary = result.shouldBeInstanceOf<RestoreResult.Restored>().summary
        summary.showCount shouldBe 1
        summary.episodeCount shouldBe 2
        database.followedShowsQueries.entries().executeAsList() shouldHaveSize 1
        watchedEpisodes() shouldHaveSize 2
    }

    @Test
    fun `should replace existing data given a restore runs`() = runTest(testDispatcher) {
        val showId = insertShow(BREAKING_BAD_TMDB_ID)
        followShow(showId)
        watchEpisode(showId, season = 9, episode = 9)

        repository.restoreBackup(fileWith(breakingBad()))

        val watched = watchedEpisodes()
        watched shouldHaveSize 2
        watched.none { it.season_number == 9L } shouldBe true
    }

    @Test
    fun `should mark every restored row as not pending upload`() = runTest(testDispatcher) {
        repository.restoreBackup(fileWith(breakingBad()))

        database.followedShowsQueries.entries().executeAsList()
            .all { it.pending_action == PendingAction.NOTHING.value } shouldBe true
        watchedEpisodes()
            .all { it.pending_action == PendingAction.NOTHING.value } shouldBe true
    }

    @Test
    fun `should mark a restored followed show for upload given user is signed in`() = runTest(testDispatcher) {
        repository.restoreBackup(fileWith(breakingBad()), syncWithConnectedAccount = true)

        database.followedShowsQueries.entries().executeAsList()
            .all { it.pending_action == PendingAction.UPLOAD.value } shouldBe true
    }

    @Test
    fun `should survive a library sync given user is signed in`() = runTest(testDispatcher) {
        repository.restoreBackup(fileWith(breakingBad()), syncWithConnectedAccount = true)

        val survivors = database.followedShowsQueries.entriesWithNoPendingAction().executeAsList()

        survivors shouldHaveSize 0
        database.followedShowsQueries.entries().executeAsList() shouldHaveSize 1
    }

    @Test
    fun `should be removed by a library sync given user is signed out`() = runTest(testDispatcher) {
        repository.restoreBackup(fileWith(breakingBad()), syncWithConnectedAccount = false)

        val exposed = database.followedShowsQueries.entriesWithNoPendingAction().executeAsList()

        exposed shouldHaveSize 1
    }

    @Test
    fun `should leave no provider id on a restored watched episode`() = runTest(testDispatcher) {
        repository.restoreBackup(fileWith(breakingBad()))

        watchedEpisodes()
            .all { it.trakt_id == null && it.synced_at == null } shouldBe true
    }

    @Test
    fun `should create a stub show given the tmdb id is not cached`() = runTest(testDispatcher) {
        val result = repository.restoreBackup(
            fileWith(BackupShow(tmdbId = OTHER_TMDB_ID, title = "Better Call Saul", followedAt = NOW)),
        )

        result.shouldBeInstanceOf<RestoreResult.Restored>().summary.skippedShows.shouldBeEmpty()
        database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(OTHER_TMDB_ID)).executeAsOneOrNull().shouldNotBeNull()
    }

    @Test
    fun `should count a show skipped given it has no tmdb id`() = runTest(testDispatcher) {
        val result = repository.restoreBackup(
            fileWith(BackupShow(tmdbId = 0L, title = "Nameless", followedAt = NOW)),
        )

        val summary = result.shouldBeInstanceOf<RestoreResult.Restored>().summary
        summary.showCount shouldBe 0
        summary.skippedShows shouldBe listOf("Nameless")
    }

    @Test
    fun `should count a season rating skipped given the season is not cached`() = runTest(testDispatcher) {
        val show = breakingBad().copy(seasonRatings = listOf(BackupSeasonRating(season = 1, value = 8)))

        val result = repository.restoreBackup(fileWith(show))

        result.shouldBeInstanceOf<RestoreResult.Restored>().summary.skippedSeasonRatings shouldBe 1
    }

    @Test
    fun `should restore a season rating given the season is cached`() = runTest(testDispatcher) {
        val showId = insertShow(BREAKING_BAD_TMDB_ID)
        insertSeason(showId, seasonNumber = 1)
        val show = breakingBad().copy(seasonRatings = listOf(BackupSeasonRating(season = 1, value = 8)))

        val result = repository.restoreBackup(fileWith(show))

        result.shouldBeInstanceOf<RestoreResult.Restored>().summary.skippedSeasonRatings shouldBe 0
        database.ratingsQueries.observeSeasonRating(SEASON_ID).executeAsOne().user_rating shouldBe 8L
    }

    @Test
    fun `should skip a rating given its value is out of range`() = runTest(testDispatcher) {
        val show = breakingBad().copy(rating = BackupRating(value = 99))

        repository.restoreBackup(fileWith(show))

        val showId = database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(BREAKING_BAD_TMDB_ID)).executeAsOne()
        database.ratingsQueries.observeShowRating(showId).executeAsOneOrNull().shouldBeNull()
    }

    @Test
    fun `should report rewatch sessions as kept`() = runTest(testDispatcher) {
        val showId = insertShow(BREAKING_BAD_TMDB_ID)
        database.rewatchQueries.openSession(showId = showId, startedAt = NOW)

        val result = repository.restoreBackup(fileWith(breakingBad()))

        result.shouldBeInstanceOf<RestoreResult.Restored>().summary.rewatchSessionsKept shouldBe 1
    }

    @Test
    fun `should write a safety copy before replacing data`() = runTest(testDispatcher) {
        val showId = insertShow(BREAKING_BAD_TMDB_ID)
        followShow(showId)
        watchEpisode(showId, season = 9, episode = 9)

        repository.restoreBackup(fileWith(BackupShow(tmdbId = OTHER_TMDB_ID, title = "Better Call Saul")))

        safetyCopy().shouldNotBeNull() shouldContain "\"season\":9"
    }

    @Test
    fun `should keep the safety copy and the previous data given the import fails`() = runTest(testDispatcher) {
        repository = buildRepository(FailingTransactionRunner(DbTransactionRunner(database)))
        val showId = insertShow(BREAKING_BAD_TMDB_ID)
        followShow(showId)
        watchEpisode(showId, season = 9, episode = 9)

        val result = repository.restoreBackup(fileWith(breakingBad()))

        result.shouldBeInstanceOf<RestoreResult.Failed>().reason shouldBe RestoreFailure.ImportFailed
        safetyCopy().shouldNotBeNull() shouldContain "\"season\":9"
        watchedEpisodes() shouldHaveSize 1
    }

    @Test
    fun `should fail given the safety copy cannot be written`() = runTest(testDispatcher) {
        val showId = insertShow(BREAKING_BAD_TMDB_ID)
        followShow(showId)
        destination.setWriteException(IllegalStateException("no space"))

        val result = repository.restoreBackup(fileWith(BackupShow(tmdbId = OTHER_TMDB_ID, title = "Better Call Saul")))

        result.shouldBeInstanceOf<RestoreResult.Failed>().reason shouldBe RestoreFailure.SafetyCopyFailed
        database.followedShowsQueries.entries().executeAsList() shouldHaveSize 1
    }

    @Test
    fun `should fail given the file was written by a newer release`() = runTest(testDispatcher) {
        val source = fileWith(
            """{"version": ${BackupFormat.VERSION + 1}, "createdAt": "now", "appVersion": "9.0", "shows": []}""",
        )

        val result = repository.restoreBackup(source)

        result.shouldBeInstanceOf<RestoreResult.Failed>().reason shouldBe RestoreFailure.VersionTooNew
    }

    @Test
    fun `should import given the file was written by the current version`() = runTest(testDispatcher) {
        val source = fileWith(
            """{"version": 1, "createdAt": "now", "appVersion": "1.0", "shows": [], "preferences": {}}""",
        )

        repository.restoreBackup(source).shouldBeInstanceOf<RestoreResult.Restored>()
    }

    @Test
    fun `should import given the file has an unknown field`() = runTest(testDispatcher) {
        val source = fileWith(
            """{"version": 1, "createdAt": "now", "appVersion": "1.0", "lists": [], "shows": []}""",
        )

        repository.restoreBackup(source).shouldBeInstanceOf<RestoreResult.Restored>()
    }

    @Test
    fun `should keep existing data given the file cannot be parsed`() = runTest(testDispatcher) {
        val showId = insertShow(BREAKING_BAD_TMDB_ID)
        followShow(showId)

        val result = repository.restoreBackup(fileWith("not json"))

        result.shouldBeInstanceOf<RestoreResult.Failed>().reason shouldBe RestoreFailure.ReadFailed
        database.followedShowsQueries.entries().executeAsList() shouldHaveSize 1
    }

    @Test
    fun `should restore the watch status given the file has one`() = runTest(testDispatcher) {
        repository.restoreBackup(fileWith(breakingBad().copy(watchStatus = "COMPLETED")))

        val showId = database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(BREAKING_BAD_TMDB_ID)).executeAsOne()
        database.showWatchStatusQueries.statusForShow(showId).executeAsOne().status shouldBe WatchStatus.COMPLETED
    }

    @Test
    fun `should ignore a watch status the app does not know`() = runTest(testDispatcher) {
        repository.restoreBackup(fileWith(breakingBad().copy(watchStatus = "ABANDONED")))

        val showId = database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(BREAKING_BAD_TMDB_ID)).executeAsOne()
        database.showWatchStatusQueries.statusForShow(showId).executeAsOneOrNull().shouldBeNull()
    }

    @Test
    fun `should rebuild the continue watching list given episodes are restored`() = runTest(testDispatcher) {
        repository.restoreBackup(fileWith(breakingBad()))

        database.continueWatchingQueries.entries().executeAsList() shouldHaveSize 1
    }

    @Test
    fun `should restore seasons and episodes given the file has them`() = runTest(testDispatcher) {
        val show = breakingBad().copy(
            seasons = listOf(
                BackupSeason(
                    tmdbId = 3572,
                    seasonNumber = 1,
                    title = "Season 1",
                    episodeCount = 2,
                    episodes = listOf(
                        BackupEpisode(tmdbId = 62085, episodeNumber = 1, title = "Pilot"),
                        BackupEpisode(tmdbId = 62086, episodeNumber = 2, title = "Cat in the Bag"),
                    ),
                ),
            ),
        )

        repository.restoreBackup(fileWith(show))

        val showId = database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(BREAKING_BAD_TMDB_ID)).executeAsOne()
        database.seasonsQueries.getSeasonByShowAndNumber(showId, 1).executeAsOneOrNull().shouldNotBeNull()
        database.episodesQueries.countEpisodesForShow(showId, 1).executeAsOne() shouldBe 2
    }

    @Test
    fun `should link watch history to the restored episodes`() = runTest(testDispatcher) {
        val show = breakingBad().copy(
            seasons = listOf(
                BackupSeason(
                    tmdbId = 3572,
                    seasonNumber = 1,
                    title = "Season 1",
                    episodeCount = 2,
                    episodes = listOf(
                        BackupEpisode(tmdbId = 62085, episodeNumber = 1, title = "Pilot"),
                        BackupEpisode(tmdbId = 62086, episodeNumber = 2, title = "Cat in the Bag"),
                    ),
                ),
            ),
        )

        repository.restoreBackup(fileWith(show))

        watchedEpisodes().all { it.episode_id != null } shouldBe true
    }

    @Test
    fun `should restore the show details given the file has them`() = runTest(testDispatcher) {
        val show = breakingBad().copy(overview = "A chemistry teacher", posterPath = "/poster.jpg", year = "2008")

        repository.restoreBackup(fileWith(show))

        val backup = repository.createBackup()
        val restored = backup.shows.single()
        restored.overview shouldBe "A chemistry teacher"
        restored.posterPath shouldBe "/poster.jpg"
    }

    @Test
    fun `should import a file written before seasons were added`() = runTest(testDispatcher) {
        val result = repository.restoreBackup(fileWith(VERSION_ONE_FILE))

        val summary = result.shouldBeInstanceOf<RestoreResult.Restored>().summary
        summary.showCount shouldBe 1
        summary.episodeCount shouldBe 1
        watchedEpisodes() shouldHaveSize 1
    }

    @Test
    fun `should leave a show from an older file for the refill`() = runTest(testDispatcher) {
        repository.restoreBackup(fileWith(VERSION_ONE_FILE))

        repository.showsNeedingMetadata() shouldHaveSize 1
    }

    private fun buildRepository(transactionRunner: DatabaseTransactionRunner) = DefaultBackupRepository(
        database = database,
        datastoreRepository = datastoreRepository,
        dateTimeProvider = FakeDateTimeProvider(),
        appMetadata = FakeAppMetadata.DEFAULT,
        dispatchers = dispatchers,
        destination = destination,
        syncObserver = syncObserver,
        transactionRunner = transactionRunner,
    )

    private fun fileWith(vararg shows: BackupShow): String {
        destination.setContents(
            location = SOURCE,
            contents = BackupJson.encode(
                BackupFile(
                    version = BackupFormat.VERSION,
                    createdAt = "2026-01-01T00:00:00Z",
                    appVersion = "1.0.0",
                    shows = shows.toList(),
                ),
            ),
        )
        return SOURCE
    }

    private fun fileWith(contents: String): String {
        destination.setContents(location = SOURCE, contents = contents)
        return SOURCE
    }

    private fun breakingBad() = BackupShow(
        tmdbId = BREAKING_BAD_TMDB_ID,
        title = SHOW_TITLE,
        followedAt = NOW,
        watchedEpisodes = listOf(
            BackupWatchedEpisode(season = 1, episode = 1, watchedAt = NOW),
            BackupWatchedEpisode(season = 1, episode = 2, watchedAt = NOW),
        ),
    )

    private fun safetyCopy(): String? = destination.contentsAt(FakeBackupDestination.SAFETY_COPY_LOCATION)

    private fun watchedEpisodes() = database.watchedEpisodesQueries
        .getWatchedEpisodes(database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(BREAKING_BAD_TMDB_ID)).executeAsOne())
        .executeAsList()

    private fun insertShow(tmdbId: Long): Id<ShowId> {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(tmdbId),
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
        return database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(tmdbId)).executeAsOne()
    }

    private fun insertSeason(showId: Id<ShowId>, seasonNumber: Long) {
        database.seasonsQueries.upsert(
            id = SEASON_ID,
            show_id = showId,
            season_number = seasonNumber,
            episode_count = 7,
            title = "Season $seasonNumber",
            overview = null,
            image_url = null,
        )
    }

    private fun followShow(showId: Id<ShowId>) {
        database.followedShowsQueries.upsert(
            showId = showId,
            tmdbId = Id<TmdbId>(BREAKING_BAD_TMDB_ID),
            followedAt = NOW,
            pendingAction = PendingAction.NOTHING.value,
        )
    }

    private fun watchEpisode(showId: Id<ShowId>, season: Long, episode: Long) {
        database.watchedEpisodesQueries.upsert(
            showId,
            null,
            season,
            episode,
            NOW,
            PendingAction.NOTHING.value,
        )
    }

    private class FailingTransactionRunner(
        private val delegate: DatabaseTransactionRunner,
    ) : DatabaseTransactionRunner {
        override fun <T> invoke(block: () -> T): T = delegate {
            block()
            throw IllegalStateException("disk full")
        }
    }

    private companion object {
        private const val BREAKING_BAD_TMDB_ID = 1396L
        private const val OTHER_TMDB_ID = 60059L
        private const val SHOW_TITLE = "Breaking Bad"
        private const val NOW = 1_700_000_000_000L
        private const val SOURCE = "content://downloads/backup.json"
        private val VERSION_ONE_FILE = """
            {"version":1,"createdAt":"2026-01-01T00:00:00Z","appVersion":"1.0.0","shows":[
              {"tmdbId":$BREAKING_BAD_TMDB_ID,"title":"$SHOW_TITLE","followedAt":$NOW,
               "watchedEpisodes":[{"season":1,"episode":1,"watchedAt":$NOW}]}
            ]}
        """.trimIndent()
        private val SEASON_ID = Id<SeasonId>(3572L)
    }
}
