package com.thomaskioko.tvmaniac.watchstatus.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.WatchStatus
import com.thomaskioko.tvmaniac.watchstatus.api.ShowWatchProgress
import com.thomaskioko.tvmaniac.watchstatus.api.ShowWatchStatusDao
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultShowWatchStatusDaoTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var dao: ShowWatchStatusDao
    private var showId: Id<ShowId> = Id(0L)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dao = DefaultShowWatchStatusDao(database, dispatchers)
        showId = seedShow(SHOW_TRAKT_ID)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should upsert and read back a status`() {
        dao.upsert(showId, WatchStatus.WATCHING, lastWatchedAt = 1_000L, lastSyncedAt = 2_000L)

        dao.getStatus(showId) shouldBe WatchStatus.WATCHING
    }

    @Test
    fun `should overwrite the status on a second upsert`() {
        dao.upsert(showId, WatchStatus.WATCHING, null, null)
        dao.upsert(showId, WatchStatus.COMPLETED, null, null)

        dao.getStatus(showId) shouldBe WatchStatus.COMPLETED
    }

    @Test
    fun `should return a null status given no row for the show`() {
        dao.getStatus(showId).shouldBeNull()
    }

    @Test
    fun `should emit status changes to observers`() = runTest {
        dao.observeStatus(showId).test {
            awaitItem().shouldBeNull()

            dao.upsert(showId, WatchStatus.WATCHING, null, null)
            awaitItem() shouldBe WatchStatus.WATCHING

            dao.upsert(showId, WatchStatus.COMPLETED, null, null)
            awaitItem() shouldBe WatchStatus.COMPLETED

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should delete the status for a show`() {
        dao.upsert(showId, WatchStatus.WATCHING, null, null)

        dao.delete(showId)

        dao.getStatus(showId).shouldBeNull()
    }

    @Test
    fun `should delete all statuses`() {
        val otherShowId = seedShow(OTHER_TRAKT_ID)
        dao.upsert(showId, WatchStatus.WATCHING, null, null)
        dao.upsert(otherShowId, WatchStatus.COMPLETED, null, null)

        dao.deleteAll()

        dao.getStatus(showId).shouldBeNull()
        dao.getStatus(otherShowId).shouldBeNull()
    }

    @Test
    fun `should return zero watch progress for a show with no episodes`() {
        dao.getWatchProgress(showId) shouldBe ShowWatchProgress(watchedCount = 0, totalCount = 0)
    }

    @Test
    fun `should return null watch progress for an unknown show`() {
        dao.getWatchProgress(Id(999_999L)).shouldBeNull()
    }

    private fun seedShow(traktId: Long): Id<ShowId> {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(traktId),
            name = "show-$traktId",
            overview = "overview",
            language = "en",
            year = "2020",
            ratings = 8.0,
            vote_count = 100,
            genres = emptyList(),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
        return showIdForTraktId(traktId = traktId, tmdbId = traktId)
    }

    private companion object {
        private const val SHOW_TRAKT_ID = 1388L
        private const val OTHER_TRAKT_ID = 1429L
    }
}
