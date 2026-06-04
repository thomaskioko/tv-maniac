package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultWatchedEpisodeDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Instant

private fun LocalDate.toEpochMillis(): Long =
    atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

@OptIn(ExperimentalCoroutinesApi::class)
internal class WatchedEpisodeTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val now = LocalDate(2024, 6, 1).toEpochMillis()
    private val fakeDateTimeProvider = FakeDateTimeProvider()

    private lateinit var dao: WatchedEpisodeDao
    private var showId: Id<ShowId> = Id(0L)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeDateTimeProvider.setCurrentTimeMillis(now)
        dao = DefaultWatchedEpisodeDao(
            database = database,
            showIdResolver = showIdResolver,
            dispatchers = dispatchers,
            dateTimeProvider = fakeDateTimeProvider,
        )
        seedShow()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should preserve UPLOAD-pending row when upsertBatchFromTrakt has same key`() = runTest {
        dao.markAsWatched(
            showTraktId = SHOW_ID,
            episodeId = EPISODE_1_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            includeSpecials = false,
        )

        dao.upsertBatchFromTrakt(
            showTraktId = SHOW_ID,
            entries = listOf(traktEntry(seasonNumber = 1L, episodeNumber = 1L, traktId = 999L)),
            includeSpecials = false,
        )

        val row = readRow(seasonNumber = 1L, episodeNumber = 1L)
        row.shouldNotBeNull()
        row.pending_action shouldBe PendingAction.UPLOAD.value
        row.trakt_id.shouldBeNull()
    }

    @Test
    fun `should preserve SYNCED_DELETE when upsertBatchFromTrakt has same key`() = runTest {
        seedSyncedRow(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L)
        val rowId = readRow(seasonNumber = 1L, episodeNumber = 1L)!!.watched_id

        dao.markAsSyncedDelete(rowId)

        dao.upsertBatchFromTrakt(
            showTraktId = SHOW_ID,
            entries = listOf(traktEntry(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L)),
            includeSpecials = false,
        )

        val row = readRow(seasonNumber = 1L, episodeNumber = 1L)
        row.shouldNotBeNull()
        row.pending_action shouldBe PendingAction.SYNCED_DELETE.value
    }

    @Test
    fun `should write SYNCED_DELETE with current timestamp via markAsSyncedDelete`() = runTest {
        seedSyncedRow(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L)
        val rowId = readRow(seasonNumber = 1L, episodeNumber = 1L)!!.watched_id

        dao.markAsSyncedDelete(rowId)

        val row = readRow(seasonNumber = 1L, episodeNumber = 1L)
        row.shouldNotBeNull()
        row.pending_action shouldBe PendingAction.SYNCED_DELETE.value
        row.synced_at shouldBe now
    }

    @Test
    fun `should remove only synced deletes older than threshold`() = runTest {
        val freshSyncedDelete = now - 1L.daysMillis()
        val oldSyncedDelete = now - 14L.daysMillis()
        seedSyncedDelete(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L, syncedAt = freshSyncedDelete)
        seedSyncedDelete(seasonNumber = 1L, episodeNumber = 2L, traktId = 556L, syncedAt = oldSyncedDelete)
        seedSyncedRow(seasonNumber = 1L, episodeNumber = 3L, traktId = 557L)

        dao.purgeSyncedDeletesOlderThan(thresholdMillis = now - 7L.daysMillis())

        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldNotBeNull()
        readRow(seasonNumber = 1L, episodeNumber = 2L).shouldBeNull()
        readRow(seasonNumber = 1L, episodeNumber = 3L).shouldNotBeNull()
    }

    @Test
    fun `shows_last_watched view should exclude SYNCED_DELETE rows`() = runTest {
        seedSyncedRow(
            seasonNumber = 1L,
            episodeNumber = 1L,
            traktId = 901L,
            episodeId = EPISODE_1_ID,
            watchedAt = now - 10_000L,
        )
        seedSyncedRow(
            seasonNumber = 1L,
            episodeNumber = 2L,
            traktId = 902L,
            episodeId = EPISODE_2_ID,
            watchedAt = now - 5_000L,
        )
        val newerId = readRow(seasonNumber = 1L, episodeNumber = 2L)!!.watched_id

        dao.markAsSyncedDelete(newerId)

        val lastWatched = database.showsLastWatchedQueries.lastWatchedEpisodeForShow(Id(SHOW_ID))
            .executeAsOneOrNull()
        lastWatched.shouldNotBeNull()
        lastWatched.last_watched_episode shouldBe 1L
    }

    @Test
    fun `should hard-delete UPLOAD-pending unsynced row when marked unwatched`() = runTest {
        dao.markAsWatched(
            showTraktId = SHOW_ID,
            episodeId = EPISODE_1_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            includeSpecials = false,
        )
        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldNotBeNull()

        dao.markAsUnwatched(
            showTraktId = SHOW_ID,
            episodeId = EPISODE_1_ID,
            includeSpecials = false,
        )

        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldBeNull()
    }

    @Test
    fun `should flip DELETE to UPLOAD when re-marked watched`() = runTest {
        seedSyncedRow(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L)
        dao.markAsUnwatched(
            showTraktId = SHOW_ID,
            episodeId = EPISODE_1_ID,
            includeSpecials = false,
        )
        readRow(seasonNumber = 1L, episodeNumber = 1L)!!.pending_action shouldBe PendingAction.DELETE.value

        dao.markAsWatched(
            showTraktId = SHOW_ID,
            episodeId = EPISODE_1_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            includeSpecials = false,
        )

        val row = readRow(seasonNumber = 1L, episodeNumber = 1L)
        row.shouldNotBeNull()
        row.pending_action shouldBe PendingAction.UPLOAD.value
    }

    @Test
    fun `should flip SYNCED_DELETE to UPLOAD when re-marked watched`() = runTest {
        seedSyncedRow(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L, watchedAt = now - 100_000L)
        val rowId = readRow(seasonNumber = 1L, episodeNumber = 1L)!!.watched_id
        dao.markAsSyncedDelete(rowId)
        readRow(seasonNumber = 1L, episodeNumber = 1L)!!.pending_action shouldBe PendingAction.SYNCED_DELETE.value

        dao.markAsWatched(
            showTraktId = SHOW_ID,
            episodeId = EPISODE_1_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            includeSpecials = false,
        )

        val row = readRow(seasonNumber = 1L, episodeNumber = 1L)
        row.shouldNotBeNull()
        row.pending_action shouldBe PendingAction.UPLOAD.value
        row.watched_at shouldNotBe now - 100_000L
    }

    @Test
    fun `should preserve trakt_id when synced row is re-marked watched`() = runTest {
        seedSyncedRow(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L, watchedAt = now - 100_000L)
        readRow(seasonNumber = 1L, episodeNumber = 1L)!!.trakt_id shouldBe 555L

        dao.markAsWatched(
            showTraktId = SHOW_ID,
            episodeId = EPISODE_1_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            includeSpecials = false,
        )

        val row = readRow(seasonNumber = 1L, episodeNumber = 1L)
        row.shouldNotBeNull()
        row.trakt_id shouldBe 555L
        row.synced_at.shouldNotBeNull()
        row.pending_action shouldBe PendingAction.UPLOAD.value
    }

    @Test
    fun `markSeasonAsUnwatched should flip synced rows to DELETE and hard-delete unsynced rows`() = runTest {
        seedSyncedRow(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L)
        dao.markAsWatched(
            showTraktId = SHOW_ID,
            episodeId = EPISODE_2_ID,
            seasonNumber = 1L,
            episodeNumber = 2L,
            includeSpecials = false,
        )

        dao.markSeasonAsUnwatched(
            showTraktId = SHOW_ID,
            seasonNumber = 1L,
            includeSpecials = false,
        )

        val syncedRow = readRow(seasonNumber = 1L, episodeNumber = 1L)
        syncedRow.shouldNotBeNull()
        syncedRow.pending_action shouldBe PendingAction.DELETE.value
        syncedRow.trakt_id shouldBe 555L

        readRow(seasonNumber = 1L, episodeNumber = 2L).shouldBeNull()
    }

    private data class WatchedRow(
        val watched_id: Long,
        val pending_action: String,
        val trakt_id: Long?,
        val synced_at: Long?,
        val watched_at: Long,
    )

    private fun readRow(seasonNumber: Long, episodeNumber: Long): WatchedRow? =
        database.watchedEpisodesQueries.getWatchedEpisodes(Id(SHOW_ID))
            .executeAsList()
            .firstOrNull { it.season_number == seasonNumber && it.episode_number == episodeNumber }
            ?.let {
                WatchedRow(
                    watched_id = it.watched_id,
                    pending_action = it.pending_action,
                    trakt_id = it.trakt_id,
                    synced_at = it.synced_at,
                    watched_at = it.watched_at,
                )
            }

    private fun seedSyncedRow(
        seasonNumber: Long,
        episodeNumber: Long,
        traktId: Long,
        episodeId: Long? = if (seasonNumber == 1L && episodeNumber == 1L) EPISODE_1_ID else null,
        watchedAt: Long = now,
        syncedAt: Long = now,
    ) {
        database.watchedEpisodesQueries.upsertFromTrakt(
            show_id = showId,
            episode_id = episodeId?.let { Id(it) },
            season_number = seasonNumber,
            episode_number = episodeNumber,
            watched_at = watchedAt,
            trakt_id = traktId,
            synced_at = syncedAt,
            pending_action = PendingAction.NOTHING.value,
        )
    }

    private fun seedSyncedDelete(
        seasonNumber: Long,
        episodeNumber: Long,
        traktId: Long,
        syncedAt: Long,
    ) {
        database.watchedEpisodesQueries.upsertFromTrakt(
            show_id = showId,
            episode_id = null,
            season_number = seasonNumber,
            episode_number = episodeNumber,
            watched_at = syncedAt,
            trakt_id = traktId,
            synced_at = syncedAt,
            pending_action = PendingAction.SYNCED_DELETE.value,
        )
    }

    private fun traktEntry(
        seasonNumber: Long,
        episodeNumber: Long,
        traktId: Long,
    ): WatchedEpisodeEntry = WatchedEpisodeEntry(
        showTraktId = SHOW_ID,
        episodeId = null,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        watchedAt = Instant.fromEpochMilliseconds(now),
        traktId = traktId,
        pendingAction = PendingAction.NOTHING,
    )

    private fun seedShow() {
        database.tvShowQueries.upsert(
            trakt_id = Id(SHOW_ID),
            tmdb_id = Id(SHOW_ID),
            name = "Synced Delete Test",
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
        showId = showIdForTraktId(SHOW_ID)
        database.followedShowsQueries.upsert(
            id = null,
            traktId = Id<TraktId>(SHOW_ID),
            tmdbId = Id<TmdbId>(SHOW_ID),
            followedAt = now,
            pendingAction = PendingAction.NOTHING.value,
        )
        database.seasonsQueries.upsert(
            id = Id(SEASON_ID),
            show_id = showId,
            season_number = 1L,
            title = "Season 1",
            overview = null,
            episode_count = 5L,
            image_url = null,
        )
        database.episodesQueries.upsert(
            id = Id(EPISODE_1_ID),
            season_id = Id(SEASON_ID),
            show_id = showId,
            title = "Episode 1",
            overview = "",
            episode_number = 1L,
            runtime = 45L,
            image_url = null,
            ratings = 8.0,
            vote_count = 100L,
            trakt_id = null,
            first_aired = now - 86_400_000L,
        )
        database.episodesQueries.upsert(
            id = Id(EPISODE_2_ID),
            season_id = Id(SEASON_ID),
            show_id = showId,
            title = "Episode 2",
            overview = "",
            episode_number = 2L,
            runtime = 45L,
            image_url = null,
            ratings = 8.0,
            vote_count = 100L,
            trakt_id = null,
            first_aired = now - 86_400_000L,
        )
        database.showMetadataQueries.upsert(
            show_id = showId,
            season_count = 1,
            episode_count = 5,
            status = "Returning Series",
        )
    }

    private fun Long.daysMillis(): Long = this * 86_400_000L

    private companion object {
        private const val SHOW_ID = 1L
        private const val SEASON_ID = 11L
        private const val EPISODE_1_ID = 101L
        private const val EPISODE_2_ID = 102L
    }
}
