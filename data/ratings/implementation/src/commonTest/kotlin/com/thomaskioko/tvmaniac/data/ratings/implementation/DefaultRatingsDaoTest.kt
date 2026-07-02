package com.thomaskioko.tvmaniac.data.ratings.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsDao
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultRatingsDaoTest : BaseDatabaseTest() {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var dao: RatingsDao

    @BeforeTest
    fun setup() {
        dao = DefaultRatingsDao(database, coroutineDispatcher)
        seedShow(showId = 1L)
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should return show rating given user rating is upserted`() = runTest {
        dao.upsertShowUserRating(showId = 1L, userRating = 8L, ratedAt = 1_700_000_000L, pendingAction = PendingAction.UPLOAD)

        dao.observeShowRating(1L).test {
            val entry = awaitItem()
            entry.shouldNotBeNull()
            entry.userRating shouldBe 8L
            entry.ratedAt shouldBe 1_700_000_000L
            entry.pendingAction shouldBe PendingAction.UPLOAD
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should transition pending action to nothing given upload pending action`() = runTest {
        dao.upsertShowUserRating(showId = 1L, userRating = 7L, ratedAt = 1_700_000_000L, pendingAction = PendingAction.UPLOAD)

        dao.updateShowRatingPendingAction(1L, PendingAction.NOTHING)

        dao.observeShowRating(1L).test {
            val entry = awaitItem()
            entry.shouldNotBeNull()
            entry.pendingAction shouldBe PendingAction.NOTHING
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should mark pending action as delete given clear user rating`() = runTest {
        dao.upsertShowUserRating(showId = 1L, userRating = 6L, ratedAt = 1_700_000_000L, pendingAction = PendingAction.NOTHING)

        dao.clearShowUserRating(1L)

        dao.observeShowRating(1L).test {
            val entry = awaitItem()
            entry.shouldNotBeNull()
            entry.pendingAction shouldBe PendingAction.DELETE
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should emit null given rating row is deleted`() = runTest {
        dao.upsertShowUserRating(showId = 1L, userRating = 6L, ratedAt = 1_700_000_000L, pendingAction = PendingAction.NOTHING)
        dao.clearShowUserRating(1L)

        dao.deleteShowRating(1L)

        dao.observeShowRating(1L).test {
            val entry = awaitItem()
            entry.shouldBeNull()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return only entries with upload pending action given mixed pending actions`() = runTest {
        seedShow(showId = 2L)
        dao.upsertShowUserRating(showId = 1L, userRating = 8L, ratedAt = 1_700_000_000L, pendingAction = PendingAction.UPLOAD)
        dao.upsertShowUserRating(showId = 2L, userRating = 5L, ratedAt = 1_700_000_000L, pendingAction = PendingAction.NOTHING)

        val entries = dao.showRatingsWithUploadPendingAction()

        entries.size shouldBe 1
        entries.first().showId shouldBe 1L
    }

    @Test
    fun `should return season rating given user rating is upserted`() = runTest {
        seedSeason(seasonId = 100L, showId = 1L)

        dao.upsertSeasonUserRating(seasonId = 100L, userRating = 7L, ratedAt = 1_700_000_000L, pendingAction = PendingAction.UPLOAD)

        dao.observeSeasonRating(100L).test {
            val entry = awaitItem()
            entry.shouldNotBeNull()
            entry.userRating shouldBe 7L
            entry.pendingAction shouldBe PendingAction.UPLOAD
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return only season entries with delete pending action given mixed pending actions`() = runTest {
        seedSeason(seasonId = 100L, showId = 1L)
        seedSeason(seasonId = 101L, showId = 1L)
        dao.upsertSeasonUserRating(seasonId = 100L, userRating = 7L, ratedAt = 1_700_000_000L, pendingAction = PendingAction.NOTHING)
        dao.upsertSeasonUserRating(seasonId = 101L, userRating = 4L, ratedAt = 1_700_000_000L, pendingAction = PendingAction.NOTHING)

        dao.clearSeasonUserRating(100L)

        val entries = dao.seasonRatingsWithDeletePendingAction()
        entries.size shouldBe 1
        entries.first().seasonId shouldBe 100L
    }

    @Test
    fun `should return episode rating given user rating is upserted`() = runTest {
        seedSeason(seasonId = 100L, showId = 1L)
        seedEpisode(episodeId = 200L, seasonId = 100L, showId = 1L)

        dao.upsertEpisodeUserRating(episodeId = 200L, userRating = 9L, ratedAt = 1_700_000_000L, pendingAction = PendingAction.UPLOAD)

        dao.observeEpisodeRating(200L).test {
            val entry = awaitItem()
            entry.shouldNotBeNull()
            entry.userRating shouldBe 9L
            entry.pendingAction shouldBe PendingAction.UPLOAD
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return only episode entries with delete pending action given mixed pending actions`() = runTest {
        seedSeason(seasonId = 100L, showId = 1L)
        seedEpisode(episodeId = 200L, seasonId = 100L, showId = 1L)
        seedEpisode(episodeId = 201L, seasonId = 100L, showId = 1L)
        dao.upsertEpisodeUserRating(episodeId = 200L, userRating = 9L, ratedAt = 1_700_000_000L, pendingAction = PendingAction.NOTHING)
        dao.upsertEpisodeUserRating(episodeId = 201L, userRating = 3L, ratedAt = 1_700_000_000L, pendingAction = PendingAction.NOTHING)

        dao.clearEpisodeUserRating(200L)

        val entries = dao.episodeRatingsWithDeletePendingAction()
        entries.size shouldBe 1
        entries.first().episodeId shouldBe 200L
    }

    private fun seedSeason(seasonId: Long, showId: Long) {
        val _ = database.seasonsQueries.upsert(
            id = Id(seasonId),
            show_id = Id(showId),
            season_number = 1L,
            episode_count = 10L,
            title = "Season $seasonId",
            overview = "Overview",
            image_url = null,
        )
    }

    private fun seedEpisode(episodeId: Long, seasonId: Long, showId: Long) {
        val _ = database.episodesQueries.upsert(
            id = Id(episodeId),
            season_id = Id(seasonId),
            show_id = Id(showId),
            title = "Episode $episodeId",
            overview = "Overview",
            runtime = 40L,
            vote_count = 10L,
            ratings = 8.0,
            episode_number = 1L,
            image_url = null,
            first_aired = null,
        )
    }

    private fun seedShow(showId: Long) {
        val _ = database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(showId),
            name = "Test Show $showId",
            overview = "Overview $showId",
            language = "en",
            year = "2023-01-01",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
    }
}
