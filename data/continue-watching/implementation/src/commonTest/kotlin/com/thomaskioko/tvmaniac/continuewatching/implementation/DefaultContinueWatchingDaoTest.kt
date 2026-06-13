package com.thomaskioko.tvmaniac.continuewatching.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultContinueWatchingDaoTest : BaseDatabaseTest() {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var dao: ContinueWatchingDao

    @BeforeTest
    fun setUp() {
        dao = DefaultContinueWatchingDao(database, showIdResolver, dispatchers)
        insertTvShow(showId = BREAKING_BAD_TRAKT_ID, tmdbId = BREAKING_BAD_TMDB_ID)
        insertTvShow(showId = THE_WIRE_TRAKT_ID, tmdbId = THE_WIRE_TMDB_ID)
        insertTvShow(showId = ORPHAN_TRAKT_ID, tmdbId = ORPHAN_TMDB_ID)
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should upsert entry given new trakt id`() {
        dao.upsert(breakingBadEntry)

        val entries = dao.entries()
        entries shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
    }

    @Test
    fun `should replace existing entry given newer server timestamp`() {
        dao.upsert(breakingBadEntry)
        val updated = breakingBadEntry.copy(
            completedCount = 99,
            lastWatchedAt = breakingBadEntry.lastWatchedAt + 1_000L,
        )

        dao.upsert(updated)

        val entries = dao.entries()
        entries shouldContainExactlyInAnyOrder listOf(updated)
    }

    @Test
    fun `should preserve newer local last watched at given older server entry`() {
        dao.upsert(breakingBadEntry)
        val olderServerEntry = breakingBadEntry.copy(
            completedCount = 99,
            lastWatchedAt = breakingBadEntry.lastWatchedAt - 1_000L,
        )

        dao.upsert(olderServerEntry)

        val stored = dao.entries().first()
        stored.completedCount shouldBe 99
        stored.lastWatchedAt shouldBe breakingBadEntry.lastWatchedAt
    }

    @Test
    fun `should preserve null tmdb id`() {
        val orphan = breakingBadEntry.copy(showId = ORPHAN_TMDB_ID, tmdbId = null)
        dao.upsert(orphan)

        val stored = dao.entries().first()
        stored.tmdbId.shouldBeNull()
        stored.showId shouldBe ORPHAN_TMDB_ID
    }

    @Test
    fun `should return all entries given multiple upserts`() {
        dao.upsert(breakingBadEntry)
        dao.upsert(theWireEntry)

        val entries = dao.entries()
        entries shouldContainExactlyInAnyOrder listOf(breakingBadEntry, theWireEntry)
    }

    @Test
    fun `should emit on upsert given entries are observed`() = runTest(testDispatcher) {
        dao.entriesObservable().test {
            awaitItem().shouldBeEmpty()

            dao.upsert(breakingBadEntry)
            awaitItem() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)

            dao.upsert(theWireEntry)
            awaitItem() shouldContainExactlyInAnyOrder listOf(breakingBadEntry, theWireEntry)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should delete entry given delete by trakt id`() {
        dao.upsert(breakingBadEntry)
        dao.upsert(theWireEntry)

        dao.deleteByShowId(breakingBadEntry.showId)

        dao.entries() shouldContainExactlyInAnyOrder listOf(theWireEntry)
    }

    @Test
    fun `should clear all entries given delete all`() {
        dao.upsert(breakingBadEntry)
        dao.upsert(theWireEntry)

        dao.deleteAll()

        dao.entries().shouldBeEmpty()
    }

    @Test
    fun `should seed continue watching membership from live watched episodes`() {
        insertWatchedEpisode(tmdbId = BREAKING_BAD_TMDB_ID, season = 1L, episode = 1L, watchedAt = EARLIER)
        insertWatchedEpisode(tmdbId = BREAKING_BAD_TMDB_ID, season = 1L, episode = 2L, watchedAt = LATER)

        dao.insertMembershipFromWatchedEpisodes()

        val entries = dao.entries()
        entries.map { it.showId } shouldContainExactlyInAnyOrder listOf(BREAKING_BAD_TMDB_ID)
        val entry = entries.first()
        entry.lastWatchedAt shouldBe LATER
        entry.completedCount shouldBe 0L
        entry.airedEpisodes shouldBe 0L
    }

    @Test
    fun `should not regress an existing row given membership derivation`() {
        dao.upsert(breakingBadEntry)
        insertWatchedEpisode(tmdbId = BREAKING_BAD_TMDB_ID, season = 1L, episode = 1L, watchedAt = EARLIER)

        dao.insertMembershipFromWatchedEpisodes()

        dao.entries() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
    }

    @Test
    fun `should skip soft-deleted watched episodes given membership derivation`() {
        insertWatchedEpisode(
            tmdbId = THE_WIRE_TMDB_ID,
            season = 1L,
            episode = 1L,
            watchedAt = EARLIER,
            pendingAction = "DELETE",
        )

        dao.insertMembershipFromWatchedEpisodes()

        dao.entries().shouldBeEmpty()
    }

    private fun insertWatchedEpisode(
        tmdbId: Long,
        season: Long,
        episode: Long,
        watchedAt: Long,
        pendingAction: String = "NOTHING",
    ) {
        val internalShowId = showIdResolver.showIdForTmdbId(tmdbId) ?: error("no show for tmdb $tmdbId")
        database.watchedEpisodesQueries.upsert(
            show_id = internalShowId,
            episode_id = null,
            season_number = season,
            episode_number = episode,
            watched_at = watchedAt,
            pending_action = pendingAction,
        )
    }

    private fun insertTvShow(showId: Long, tmdbId: Long) {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(tmdbId),
            name = "show-$showId",
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
        showIdForTraktId(traktId = showId, tmdbId = tmdbId)
    }

    private companion object {
        private const val BREAKING_BAD_TRAKT_ID = 1388L
        private const val BREAKING_BAD_TMDB_ID = 1396L
        private const val THE_WIRE_TRAKT_ID = 1429L
        private const val THE_WIRE_TMDB_ID = 1438L
        private const val ORPHAN_TRAKT_ID = 9999L
        private const val ORPHAN_TMDB_ID = 9998L
        private const val EARLIER = 1_700_000_000_000L
        private const val LATER = 1_700_000_100_000L
    }
}

private val breakingBadEntry = ContinueWatchingEntry(
    showId = 1396L,
    tmdbId = 1396L,
    airedEpisodes = 62L,
    completedCount = 56L,
    lastWatchedAt = 1_778_962_500_000L,
    lastUpdatedAt = 1_778_962_500_000L,
)

private val theWireEntry = ContinueWatchingEntry(
    showId = 1438L,
    tmdbId = 1438L,
    airedEpisodes = 60L,
    completedCount = 12L,
    lastWatchedAt = 1_776_222_000_000L,
    lastUpdatedAt = 1_776_222_000_000L,
)
