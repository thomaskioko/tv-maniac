package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultWatchedEpisodeDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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
            showId = SHOW_ID,
            episodeId = EPISODE_1_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            includeSpecials = false,
        )

        dao.upsertBatchFromTrakt(
            showId = SHOW_ID,
            entries = listOf(traktEntry(seasonNumber = 1L, episodeNumber = 1L, traktId = 999L)),
            includeSpecials = false,
        )

        val row = readRow(seasonNumber = 1L, episodeNumber = 1L)
        row.shouldNotBeNull()
        row.pending_action shouldBe PendingAction.UPLOAD.value
        row.trakt_id.shouldBeNull()
    }

    @Test
    fun `should remove a synced row absent from a fresh pull`() = runTest {
        seedSyncedRow(seasonNumber = 1L, episodeNumber = 1L, traktId = 901L)
        seedSyncedRow(seasonNumber = 1L, episodeNumber = 2L, traktId = 902L)

        dao.upsertBatchFromTrakt(
            showId = SHOW_ID,
            entries = listOf(traktEntry(seasonNumber = 1L, episodeNumber = 1L, traktId = 901L)),
            includeSpecials = false,
        )

        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldNotBeNull()
        readRow(seasonNumber = 1L, episodeNumber = 2L).shouldBeNull()
    }

    @Test
    fun `should keep an UPLOAD-pending row absent from a fresh pull`() = runTest {
        dao.markAsWatched(
            showId = SHOW_ID,
            episodeId = EPISODE_2_ID,
            seasonNumber = 1L,
            episodeNumber = 2L,
            includeSpecials = false,
        )

        dao.upsertBatchFromTrakt(
            showId = SHOW_ID,
            entries = listOf(traktEntry(seasonNumber = 1L, episodeNumber = 1L, traktId = 901L)),
            includeSpecials = false,
        )

        val row = readRow(seasonNumber = 1L, episodeNumber = 2L)
        row.shouldNotBeNull()
        row.pending_action shouldBe PendingAction.UPLOAD.value
    }

    @Test
    fun `should keep the later watched_at given a stale pull`() = runTest {
        seedSyncedRow(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L, watchedAt = now)

        dao.upsertBatchFromTrakt(
            showId = SHOW_ID,
            entries = listOf(traktEntry(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L, watchedAt = now - 50_000L)),
            includeSpecials = false,
        )

        readRow(seasonNumber = 1L, episodeNumber = 1L)!!.watched_at shouldBe now
    }

    @Test
    fun `should adopt a newer watched_at given a re-watch pull`() = runTest {
        seedSyncedRow(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L, watchedAt = now - 50_000L)

        dao.upsertBatchFromTrakt(
            showId = SHOW_ID,
            entries = listOf(traktEntry(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L, watchedAt = now)),
            includeSpecials = false,
        )

        readRow(seasonNumber = 1L, episodeNumber = 1L)!!.watched_at shouldBe now
    }

    @Test
    fun `should hard-delete UPLOAD-pending unsynced row when marked unwatched`() = runTest {
        dao.markAsWatched(
            showId = SHOW_ID,
            episodeId = EPISODE_1_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            includeSpecials = false,
        )
        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldNotBeNull()

        dao.markAsUnwatched(
            showId = SHOW_ID,
            episodeId = EPISODE_1_ID,
            includeSpecials = false,
        )

        readRow(seasonNumber = 1L, episodeNumber = 1L).shouldBeNull()
    }

    @Test
    fun `should flip DELETE to UPLOAD when re-marked watched`() = runTest {
        seedSyncedRow(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L)
        dao.markAsUnwatched(
            showId = SHOW_ID,
            episodeId = EPISODE_1_ID,
            includeSpecials = false,
        )
        readRow(seasonNumber = 1L, episodeNumber = 1L)!!.pending_action shouldBe PendingAction.DELETE.value

        dao.markAsWatched(
            showId = SHOW_ID,
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
    fun `should preserve trakt_id when synced row is re-marked watched`() = runTest {
        seedSyncedRow(seasonNumber = 1L, episodeNumber = 1L, traktId = 555L, watchedAt = now - 100_000L)
        readRow(seasonNumber = 1L, episodeNumber = 1L)!!.trakt_id shouldBe 555L

        dao.markAsWatched(
            showId = SHOW_ID,
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
            showId = SHOW_ID,
            episodeId = EPISODE_2_ID,
            seasonNumber = 1L,
            episodeNumber = 2L,
            includeSpecials = false,
        )

        dao.markSeasonAsUnwatched(
            showId = SHOW_ID,
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

    private fun traktEntry(
        seasonNumber: Long,
        episodeNumber: Long,
        traktId: Long,
        watchedAt: Long = now,
    ): WatchedEpisodeEntry = WatchedEpisodeEntry(
        showId = SHOW_ID,
        episodeId = null,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        watchedAt = Instant.fromEpochMilliseconds(watchedAt),
        traktId = traktId,
        pendingAction = PendingAction.NOTHING,
    )

    private fun seedShow() {
        database.tvShowQueries.upsert(
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
            showId = showId,
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
            first_aired = now - 86_400_000L,
        )
        database.showMetadataQueries.upsert(
            show_id = showId,
            season_count = 1,
            episode_count = 5,
            status = "Returning Series",
        )
    }

    private companion object {
        private const val SHOW_ID = 1L
        private const val SEASON_ID = 11L
        private const val EPISODE_1_ID = 101L
        private const val EPISODE_2_ID = 102L
    }
}
