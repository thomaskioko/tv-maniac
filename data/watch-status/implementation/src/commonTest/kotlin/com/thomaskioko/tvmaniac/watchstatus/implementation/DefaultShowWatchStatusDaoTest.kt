package com.thomaskioko.tvmaniac.watchstatus.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.WatchStatus
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
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
import kotlin.time.Instant

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

    private val dateTimeProvider = FakeDateTimeProvider(currentTime = Instant.fromEpochMilliseconds(NOW_MILLIS))
    private lateinit var dao: ShowWatchStatusDao
    private var showId: Id<ShowId> = Id(0L)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dao = DefaultShowWatchStatusDao(database, dispatchers, dateTimeProvider)
        showId = addShow(SHOW_TRAKT_ID)
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
        val otherShowId = addShow(OTHER_TRAKT_ID)
        dao.upsert(showId, WatchStatus.WATCHING, null, null)
        dao.upsert(otherShowId, WatchStatus.COMPLETED, null, null)

        dao.deleteAll()

        dao.getStatus(showId).shouldBeNull()
        dao.getStatus(otherShowId).shouldBeNull()
    }

    @Test
    fun `should count a followed show with no status as watchlist`() = runTest {
        follow(showId)

        dao.observeStatusCounts().test {
            awaitItem() shouldBe mapOf(WatchStatus.WATCHLIST to 1L)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should count a followed show once given it also has a watchlist status`() = runTest {
        follow(showId)
        dao.upsert(showId, WatchStatus.WATCHLIST, null, null)

        dao.observeStatusCounts().test {
            awaitItem() shouldBe mapOf(WatchStatus.WATCHLIST to 1L)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should leave out a watchlist status given the show is being unfollowed`() = runTest {
        follow(showId, pendingAction = "DELETE")
        dao.upsert(showId, WatchStatus.WATCHLIST, null, null)

        dao.observeStatusCounts().test {
            awaitItem() shouldBe emptyMap()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should leave out a watchlist status given the show is no longer followed`() = runTest {
        dao.upsert(showId, WatchStatus.WATCHLIST, null, null)

        dao.observeStatusCounts().test {
            awaitItem() shouldBe emptyMap()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should keep a completed status given the show is no longer followed`() = runTest {
        dao.upsert(showId, WatchStatus.COMPLETED, null, null)

        dao.observeStatusCounts().test {
            awaitItem() shouldBe mapOf(WatchStatus.COMPLETED to 1L)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should count a followed show once given it is part way through`() = runTest {
        follow(showId)
        dao.upsert(showId, WatchStatus.WATCHING, null, null)

        dao.observeStatusCounts().test {
            awaitItem() shouldBe mapOf(WatchStatus.WATCHING to 1L)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should return zero watch progress for a show with no episodes`() {
        dao.getWatchProgress(showId) shouldBe ShowWatchProgress(watchedCount = 0, totalCount = 0)
    }

    @Test
    fun `should return null watch progress for an unknown show`() {
        dao.getWatchProgress(Id(999_999L)).shouldBeNull()
    }

    @Test
    fun `should exclude deleted rows from the watched count`() {
        addSeason()
        addEpisode(episodeId = 1L, episodeNumber = 1L, firstAired = PAST_AIR_DATE)
        addEpisode(episodeId = 2L, episodeNumber = 2L, firstAired = PAST_AIR_DATE)
        addEpisode(episodeId = 3L, episodeNumber = 3L, firstAired = PAST_AIR_DATE)
        markWatched(episodeId = 1L, episodeNumber = 1L, pendingAction = "NOTHING")
        markWatched(episodeId = 2L, episodeNumber = 2L, pendingAction = "DELETE")
        markWatched(episodeId = 3L, episodeNumber = 3L, pendingAction = "SYNCED_DELETE")

        dao.getWatchProgress(showId) shouldBe ShowWatchProgress(watchedCount = 1, totalCount = 3)
    }

    @Test
    fun `should exclude null-aired and future-aired episodes from the total count`() {
        addSeason()
        addEpisode(episodeId = 1L, episodeNumber = 1L, firstAired = PAST_AIR_DATE)
        addEpisode(episodeId = 2L, episodeNumber = 2L, firstAired = null)
        addEpisode(episodeId = 3L, episodeNumber = 3L, firstAired = FUTURE_AIR_DATE)

        dao.getWatchProgress(showId) shouldBe ShowWatchProgress(watchedCount = 0, totalCount = 1)
    }

    @Test
    fun `should count an episode once it airs under the injected clock`() {
        addSeason()
        addEpisode(episodeId = 1L, episodeNumber = 1L, firstAired = FUTURE_AIR_DATE)

        dao.getWatchProgress(showId) shouldBe ShowWatchProgress(watchedCount = 0, totalCount = 0)

        dateTimeProvider.setCurrentTimeMillis(FUTURE_AIR_DATE)

        dao.getWatchProgress(showId) shouldBe ShowWatchProgress(watchedCount = 0, totalCount = 1)
    }

    private fun addSeason() {
        database.seasonsQueries.upsert(
            id = Id(SEASON_ID),
            show_id = showId,
            season_number = 1L,
            episode_count = 3L,
            title = "Season 1",
            overview = "overview",
            image_url = null,
        )
    }

    private fun addEpisode(episodeId: Long, episodeNumber: Long, firstAired: Long?) {
        database.episodesQueries.upsert(
            id = Id(episodeId),
            season_id = Id(SEASON_ID),
            show_id = showId,
            title = "Episode $episodeNumber",
            overview = "overview",
            runtime = 40L,
            vote_count = 10L,
            ratings = 8.0,
            episode_number = episodeNumber,
            image_url = null,
            first_aired = firstAired,
        )
    }

    private fun markWatched(episodeId: Long, episodeNumber: Long, pendingAction: String) {
        database.watchedEpisodesQueries.upsert(
            show_id = showId,
            episode_id = Id(episodeId),
            season_number = 1L,
            episode_number = episodeNumber,
            watched_at = PAST_AIR_DATE,
            pending_action = pendingAction,
        )
    }

    private fun follow(showId: Id<ShowId>, pendingAction: String = "NOTHING") {
        database.followedShowsQueries.upsert(
            showId = showId,
            tmdbId = null,
            followedAt = 1_000L,
            pendingAction = pendingAction,
        )
    }

    private fun addShow(traktId: Long): Id<ShowId> {
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
        private const val SEASON_ID = 10L
        private const val NOW_MILLIS = 1_700_000_000_000L
        private const val PAST_AIR_DATE = 1_600_000_000_000L
        private const val FUTURE_AIR_DATE = 1_800_000_000_000L
    }
}
