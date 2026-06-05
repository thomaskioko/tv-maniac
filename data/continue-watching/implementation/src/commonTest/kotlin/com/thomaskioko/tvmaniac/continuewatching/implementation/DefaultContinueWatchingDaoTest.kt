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
        insertTvShow(traktId = BREAKING_BAD_TRAKT_ID, tmdbId = BREAKING_BAD_TMDB_ID)
        insertTvShow(traktId = THE_WIRE_TRAKT_ID, tmdbId = THE_WIRE_TMDB_ID)
        insertTvShow(traktId = ORPHAN_TRAKT_ID, tmdbId = ORPHAN_TMDB_ID)
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
        val orphan = breakingBadEntry.copy(traktId = ORPHAN_TRAKT_ID, tmdbId = null)
        dao.upsert(orphan)

        val stored = dao.entries().first()
        stored.tmdbId.shouldBeNull()
        stored.traktId shouldBe ORPHAN_TRAKT_ID
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

        dao.deleteByTraktId(breakingBadEntry.traktId)

        dao.entries() shouldContainExactlyInAnyOrder listOf(theWireEntry)
    }

    @Test
    fun `should clear all entries given delete all`() {
        dao.upsert(breakingBadEntry)
        dao.upsert(theWireEntry)

        dao.deleteAll()

        dao.entries().shouldBeEmpty()
    }

    private fun insertTvShow(traktId: Long, tmdbId: Long) {
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
        showIdForTraktId(traktId = traktId, tmdbId = tmdbId)
    }

    private companion object {
        private const val BREAKING_BAD_TRAKT_ID = 1388L
        private const val BREAKING_BAD_TMDB_ID = 1396L
        private const val THE_WIRE_TRAKT_ID = 1429L
        private const val THE_WIRE_TMDB_ID = 1438L
        private const val ORPHAN_TRAKT_ID = 9999L
        private const val ORPHAN_TMDB_ID = 9998L
    }
}

private val breakingBadEntry = ContinueWatchingEntry(
    traktId = 1388L,
    tmdbId = 1396L,
    airedEpisodes = 62L,
    completedCount = 56L,
    lastWatchedAt = 1_778_962_500_000L,
    lastUpdatedAt = 1_778_962_500_000L,
)

private val theWireEntry = ContinueWatchingEntry(
    traktId = 1429L,
    tmdbId = 1438L,
    airedEpisodes = 60L,
    completedCount = 12L,
    lastWatchedAt = 1_776_222_000_000L,
    lastUpdatedAt = 1_776_222_000_000L,
)
