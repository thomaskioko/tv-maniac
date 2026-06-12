package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultNextEpisodeDao
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultWatchedEpisodeDao
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
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

private fun LocalDate.toEpochMillis(): Long =
    atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

@OptIn(ExperimentalCoroutinesApi::class)
internal class MarkWatchedFromDiscoverUpNextRefreshTest : BaseDatabaseTest() {

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

    private lateinit var watchedEpisodeDao: DefaultWatchedEpisodeDao
    private lateinit var nextEpisodeDao: DefaultNextEpisodeDao

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeDateTimeProvider.setCurrentTimeMillis(now)
        watchedEpisodeDao = DefaultWatchedEpisodeDao(database, showIdResolver, dispatchers, fakeDateTimeProvider)
        nextEpisodeDao = DefaultNextEpisodeDao(database, dispatchers, fakeDateTimeProvider)
        seedShowWithThreeAiredEpisodes()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should advance next episode and decrement remaining when watched marked locally`() = runTest {
        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val initial = awaitItem()
            initial.size shouldBe 1
            initial[0].episodeNumber shouldBe 1L
            initial[0].watchedCount shouldBe 0L
            initial[0].totalCount shouldBe 3L

            watchedEpisodeDao.markAsWatched(
                showId = SHOW_ID,
                episodeId = EPISODE_1_ID,
                seasonNumber = 1L,
                episodeNumber = 1L,
                includeSpecials = false,
            )

            val afterFirst = awaitItem()
            afterFirst.size shouldBe 1
            afterFirst[0].episodeNumber shouldBe 2L
            afterFirst[0].watchedCount shouldBe 1L
            afterFirst[0].totalCount shouldBe 3L

            watchedEpisodeDao.markAsWatched(
                showId = SHOW_ID,
                episodeId = EPISODE_2_ID,
                seasonNumber = 1L,
                episodeNumber = 2L,
                includeSpecials = false,
            )

            val afterSecond = awaitItem()
            afterSecond.size shouldBe 1
            afterSecond[0].episodeNumber shouldBe 3L
            afterSecond[0].watchedCount shouldBe 2L
            afterSecond[0].totalCount shouldBe 3L
        }
    }

    @Test
    fun `should drop show from list when last aired episode marked watched`() = runTest {
        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            awaitItem().size shouldBe 1

            watchedEpisodeDao.markAsWatched(
                showId = SHOW_ID,
                episodeId = EPISODE_1_ID,
                seasonNumber = 1L,
                episodeNumber = 1L,
                includeSpecials = false,
            )
            awaitItem()[0].episodeNumber shouldBe 2L

            watchedEpisodeDao.markAsWatched(
                showId = SHOW_ID,
                episodeId = EPISODE_2_ID,
                seasonNumber = 1L,
                episodeNumber = 2L,
                includeSpecials = false,
            )
            awaitItem()[0].episodeNumber shouldBe 3L

            watchedEpisodeDao.markAsWatched(
                showId = SHOW_ID,
                episodeId = EPISODE_3_ID,
                seasonNumber = 1L,
                episodeNumber = 3L,
                includeSpecials = false,
            )
            awaitItem().size shouldBe 0
        }
    }

    @Test
    fun `should advance trakt continue watching last watched at on subsequent local marks`() = runTest {
        val initialLastWatchedAt = database.continueWatchingQueries
            .entries()
            .executeAsList()
            .first { it.trakt_id.id == SHOW_ID }
            .last_watched_at

        fakeDateTimeProvider.setCurrentTimeMillis(now + 10_000L)
        watchedEpisodeDao.markAsWatched(
            showId = SHOW_ID,
            episodeId = EPISODE_1_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            includeSpecials = false,
        )

        val afterMark = database.continueWatchingQueries
            .entries()
            .executeAsList()
            .first { it.trakt_id.id == SHOW_ID }
            .last_watched_at
        afterMark shouldBe maxOf(initialLastWatchedAt, now + 10_000L)
    }

    @Test
    fun `should recompute trakt continue watching last watched at on local unmark`() = runTest {
        fakeDateTimeProvider.setCurrentTimeMillis(now + 1_000L)
        watchedEpisodeDao.markAsWatched(
            showId = SHOW_ID,
            episodeId = EPISODE_1_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
            includeSpecials = false,
        )
        fakeDateTimeProvider.setCurrentTimeMillis(now + 5_000L)
        watchedEpisodeDao.markAsWatched(
            showId = SHOW_ID,
            episodeId = EPISODE_2_ID,
            seasonNumber = 1L,
            episodeNumber = 2L,
            includeSpecials = false,
        )

        watchedEpisodeDao.markAsUnwatched(
            showId = SHOW_ID,
            episodeId = EPISODE_2_ID,
            includeSpecials = false,
        )

        val afterUnmark = database.continueWatchingQueries
            .entries()
            .executeAsList()
            .first { it.trakt_id.id == SHOW_ID }
            .last_watched_at
        afterUnmark shouldBe now + 1_000L
    }

    private fun seedShowWithThreeAiredEpisodes() {
        database.tvShowQueries.upsert(
            tmdb_id = Id(SHOW_ID),
            name = "Severance",
            overview = "",
            language = "en",
            year = "2024",
            ratings = 8.5,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
        val showId = showIdForTraktId(SHOW_ID)
        database.followedShowsQueries.upsert(
            showId = showId,
            tmdbId = Id(SHOW_ID),
            followedAt = now,
            pendingAction = "NOTHING",
        )
        database.continueWatchingQueries.upsert(
            showId = showId,
            tmdbId = Id(SHOW_ID),
            airedEpisodes = 0L,
            completedCount = 1L,
            lastWatchedAt = now,
            lastUpdatedAt = now,
            title = null,
            year = null,
        )
        database.seasonsQueries.upsert(
            id = Id(SEASON_ID),
            show_id = showId,
            season_number = 1L,
            title = "Season 1",
            overview = null,
            episode_count = 3L,
            image_url = null,
        )
        listOf(
            EPISODE_1_ID to 1L,
            EPISODE_2_ID to 2L,
            EPISODE_3_ID to 3L,
        ).forEach { (episodeId, episodeNumber) ->
            database.episodesQueries.upsert(
                id = Id(episodeId),
                season_id = Id(SEASON_ID),
                show_id = showId,
                title = "Episode $episodeNumber",
                overview = "",
                episode_number = episodeNumber,
                runtime = 45L,
                image_url = null,
                ratings = 8.0,
                vote_count = 100L,
                first_aired = now - (4 - episodeNumber) * 86_400_000L,
            )
        }
    }

    private companion object {
        private const val SHOW_ID = 1L
        private const val SEASON_ID = 11L
        private const val EPISODE_1_ID = 101L
        private const val EPISODE_2_ID = 102L
        private const val EPISODE_3_ID = 103L
    }
}
