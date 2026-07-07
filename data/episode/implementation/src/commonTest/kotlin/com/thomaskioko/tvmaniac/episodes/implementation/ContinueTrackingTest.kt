package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultEpisodesDao
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
internal class ContinueTrackingTest : BaseDatabaseTest() {

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
    private lateinit var episodesDao: DefaultEpisodesDao

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeDateTimeProvider.setCurrentTimeMillis(now)
        watchedEpisodeDao = DefaultWatchedEpisodeDao(database, showIdResolver, dispatchers, fakeDateTimeProvider)
        episodesDao = DefaultEpisodesDao(database, showIdResolver, dispatchers, fakeDateTimeProvider)
        seedShow()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should flip is_watched given local mark-as-watched write`() = runTest {
        episodesDao.observeEpisodeById(EPISODE_1_ID).test {
            val initial = awaitItem()
            initial?.is_watched shouldBe 0L

            watchedEpisodeDao.markAsWatched(
                showId = SHOW_ID,
                episodeId = EPISODE_1_ID,
                seasonNumber = 1L,
                episodeNumber = 1L,
                includeSpecials = false,
            )

            val updated = awaitItem()
            updated?.is_watched shouldBe 1L
        }
    }

    @Test
    fun `should advance next episode for show given local mark-as-watched write`() = runTest {
        episodesDao.observeNextEpisodeForShow(
            showId = SHOW_ID,
            includeSpecials = false,
        ).test {
            val initial = awaitItem()
            initial?.episode_number shouldBe 1L

            watchedEpisodeDao.markAsWatched(
                showId = SHOW_ID,
                episodeId = EPISODE_1_ID,
                seasonNumber = 1L,
                episodeNumber = 1L,
                includeSpecials = false,
            )

            val advanced = awaitItem()
            advanced?.episode_number shouldBe 2L
        }
    }

    @Test
    fun `should advance next episode given show is watched but not followed`() = runTest {
        val unfollowedShowId = 4L
        seedShowWithSecondEpisodeAt(
            showId = unfollowedShowId,
            seasonId = 41L,
            airedEpisodeId = 401L,
            secondEpisodeId = 402L,
            secondEpisodeFirstAired = now - 86_400_000L,
            addToFollowedShows = false,
        )

        episodesDao.observeNextEpisodeForShow(
            showId = unfollowedShowId,
            includeSpecials = false,
        ).test {
            val initial = awaitItem()
            initial?.episode_number shouldBe 1L

            watchedEpisodeDao.markAsWatched(
                showId = unfollowedShowId,
                episodeId = 401L,
                seasonNumber = 1L,
                episodeNumber = 1L,
                includeSpecials = false,
            )

            val advanced = awaitItem()
            advanced?.episode_number shouldBe 2L
        }
    }

    @Test
    fun `should return unaired episode given all aired episodes watched`() = runTest {
        val futureShowId = 2L
        seedShowWithSecondEpisodeAt(
            showId = futureShowId,
            seasonId = 21L,
            airedEpisodeId = 201L,
            secondEpisodeId = 202L,
            secondEpisodeFirstAired = now + 7L * 86_400_000L,
        )
        watchedEpisodeDao.markAsWatched(
            showId = futureShowId,
            episodeId = 201L,
            seasonNumber = 1L,
            episodeNumber = 1L,
            includeSpecials = false,
        )

        episodesDao.observeNextEpisodeForShow(
            showId = futureShowId,
            includeSpecials = false,
        ).test {
            awaitItem()?.episode_number shouldBe 2L
        }
    }

    @Test
    fun `should return episode without air date given all aired episodes watched`() = runTest {
        val tbdShowId = 3L
        seedShowWithSecondEpisodeAt(
            showId = tbdShowId,
            seasonId = 31L,
            airedEpisodeId = 301L,
            secondEpisodeId = 302L,
            secondEpisodeFirstAired = null,
        )
        watchedEpisodeDao.markAsWatched(
            showId = tbdShowId,
            episodeId = 301L,
            seasonNumber = 1L,
            episodeNumber = 1L,
            includeSpecials = false,
        )

        episodesDao.observeNextEpisodeForShow(
            showId = tbdShowId,
            includeSpecials = false,
        ).test {
            awaitItem()?.episode_number shouldBe 2L
        }
    }

    @Test
    fun `should return null given all episodes watched`() = runTest {
        val completedShowId = 5L
        seedShowWithSecondEpisodeAt(
            showId = completedShowId,
            seasonId = 51L,
            airedEpisodeId = 501L,
            secondEpisodeId = 502L,
            secondEpisodeFirstAired = now - 86_400_000L,
        )
        watchedEpisodeDao.markAsWatched(
            showId = completedShowId,
            episodeId = 501L,
            seasonNumber = 1L,
            episodeNumber = 1L,
            includeSpecials = false,
        )
        watchedEpisodeDao.markAsWatched(
            showId = completedShowId,
            episodeId = 502L,
            seasonNumber = 1L,
            episodeNumber = 2L,
            includeSpecials = false,
        )

        episodesDao.observeNextEpisodeForShow(
            showId = completedShowId,
            includeSpecials = false,
        ).test {
            awaitItem() shouldBe null
        }
    }

    @Test
    fun `should return latest season with episodes given later season has no episodes`() = runTest {
        database.seasonsQueries.upsert(
            id = Id(12L),
            show_id = showIdForTraktId(SHOW_ID),
            season_number = 2L,
            title = "Season 2",
            overview = null,
            episode_count = 0L,
            image_url = null,
        )

        episodesDao.observeLatestSeasonForShow(
            showId = SHOW_ID,
            includeSpecials = false,
        ).test {
            awaitItem()?.season_number shouldBe 1L
        }
    }

    private fun seedShowWithSecondEpisodeAt(
        showId: Long,
        seasonId: Long,
        airedEpisodeId: Long,
        secondEpisodeId: Long,
        secondEpisodeFirstAired: Long?,
        addToFollowedShows: Boolean = true,
    ) {
        database.tvShowQueries.upsert(
            tmdb_id = Id(showId),
            name = "Show $showId",
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
        val resolvedShowId = showIdForTraktId(showId)
        if (addToFollowedShows) {
            database.followedShowsQueries.upsert(
                showId = resolvedShowId,
                tmdbId = Id(showId),
                followedAt = now,
                pendingAction = "NOTHING",
            )
        }
        database.seasonsQueries.upsert(
            id = Id(seasonId),
            show_id = resolvedShowId,
            season_number = 1L,
            title = "Season 1",
            overview = null,
            episode_count = 2L,
            image_url = null,
        )
        database.episodesQueries.upsert(
            id = Id(airedEpisodeId),
            season_id = Id(seasonId),
            show_id = resolvedShowId,
            title = "Aired",
            overview = "",
            episode_number = 1L,
            runtime = 45L,
            image_url = null,
            ratings = 8.0,
            vote_count = 100L,
            first_aired = now - 86_400_000L,
        )
        database.episodesQueries.upsert(
            id = Id(secondEpisodeId),
            season_id = Id(seasonId),
            show_id = resolvedShowId,
            title = "Second",
            overview = "",
            episode_number = 2L,
            runtime = 45L,
            image_url = null,
            ratings = 8.0,
            vote_count = 100L,
            first_aired = secondEpisodeFirstAired,
        )
    }

    private fun seedShow() {
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
        database.seasonsQueries.upsert(
            id = Id(SEASON_ID),
            show_id = showId,
            season_number = 1L,
            title = "Season 1",
            overview = null,
            episode_count = 2L,
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
            first_aired = now - 43_200_000L,
        )
    }

    private companion object {
        private const val SHOW_ID = 1L
        private const val SEASON_ID = 11L
        private const val EPISODE_1_ID = 101L
        private const val EPISODE_2_ID = 102L
    }
}
