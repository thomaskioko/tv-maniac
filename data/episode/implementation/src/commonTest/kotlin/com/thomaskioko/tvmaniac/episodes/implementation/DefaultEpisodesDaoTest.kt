package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultEpisodesDao
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
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
import kotlin.time.Duration.Companion.hours

private fun LocalDate.toEpochMillis(): Long =
    atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultEpisodesDaoTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val dateTimeProvider = FakeDateTimeProvider()
    private lateinit var episodesDao: EpisodesDao

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        episodesDao = DefaultEpisodesDao(database, dispatchers, dateTimeProvider)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should return upcoming episodes given they air within the time window`() = runTest {
        val now = LocalDate(2025, 4, 1).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(now)

        val todayAirTime = now + 2.hours.inWholeMilliseconds
        val tomorrowAirTime = now + 25.hours.inWholeMilliseconds

        insertShow(id = 1L, name = "Show A")
        insertSeason(seasonId = 10L, showId = 1L, seasonNumber = 1L)
        insertEpisode(episodeId = 100L, seasonId = 10L, showId = 1L, episodeNumber = 1L, title = "Today Ep", firstAired = todayAirTime)
        insertEpisode(episodeId = 101L, seasonId = 10L, showId = 1L, episodeNumber = 2L, title = "Tomorrow Ep", firstAired = tomorrowAirTime)
        followShow(showId = 1L)

        val result = episodesDao.getUpcomingEpisodesFromFollowedShows(limit = 24.hours)

        result shouldHaveSize 1
        result[0].episode_id.id shouldBe 100L
        result[0].title shouldBe "Today Ep"
    }

    @Test
    fun `should return multiple episodes given they air on the same day`() = runTest {
        val now = LocalDate(2025, 4, 1).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(now)

        val ep1AirTime = now + 2.hours.inWholeMilliseconds
        val ep2AirTime = now + 3.hours.inWholeMilliseconds

        insertShow(id = 1L, name = "Binge Show")
        insertSeason(seasonId = 10L, showId = 1L, seasonNumber = 1L)
        insertEpisode(episodeId = 100L, seasonId = 10L, showId = 1L, episodeNumber = 1L, title = "Ep 1", firstAired = ep1AirTime)
        insertEpisode(episodeId = 101L, seasonId = 10L, showId = 1L, episodeNumber = 2L, title = "Ep 2", firstAired = ep2AirTime)
        followShow(showId = 1L)

        val result = episodesDao.getUpcomingEpisodesFromFollowedShows(limit = 24.hours)

        result shouldHaveSize 2
        result[0].episode_id.id shouldBe 100L
        result[1].episode_id.id shouldBe 101L
    }

    @Test
    fun `should exclude episodes given first_aired is null`() = runTest {
        val now = LocalDate(2025, 4, 1).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(now)

        val todayAirTime = now + 2.hours.inWholeMilliseconds

        insertShow(id = 1L, name = "Show A")
        insertSeason(seasonId = 10L, showId = 1L, seasonNumber = 1L)
        insertEpisode(episodeId = 100L, seasonId = 10L, showId = 1L, episodeNumber = 1L, title = "Aired Ep", firstAired = todayAirTime)
        insertEpisode(episodeId = 101L, seasonId = 10L, showId = 1L, episodeNumber = 2L, title = "TBA Ep", firstAired = null)
        followShow(showId = 1L)

        val result = episodesDao.getUpcomingEpisodesFromFollowedShows(limit = 24.hours)

        result shouldHaveSize 1
        result[0].episode_id.id shouldBe 100L
    }

    @Test
    fun `should exclude watched episodes from upcoming results`() = runTest {
        val now = LocalDate(2025, 4, 1).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(now)

        val ep1AirTime = now + 2.hours.inWholeMilliseconds
        val ep2AirTime = now + 3.hours.inWholeMilliseconds

        insertShow(id = 1L, name = "Show A")
        insertSeason(seasonId = 10L, showId = 1L, seasonNumber = 1L)
        insertEpisode(episodeId = 100L, seasonId = 10L, showId = 1L, episodeNumber = 1L, title = "Watched Ep", firstAired = ep1AirTime)
        insertEpisode(episodeId = 101L, seasonId = 10L, showId = 1L, episodeNumber = 2L, title = "Unwatched Ep", firstAired = ep2AirTime)
        followShow(showId = 1L)
        markEpisodeWatched(showId = 1L, episodeId = 100L, seasonNumber = 1L, episodeNumber = 1L)

        val result = episodesDao.getUpcomingEpisodesFromFollowedShows(limit = 24.hours)

        result shouldHaveSize 1
        result[0].episode_id.id shouldBe 101L
    }

    @Test
    fun `should only return episodes from followed shows`() = runTest {
        val now = LocalDate(2025, 4, 1).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(now)

        val airTime = now + 2.hours.inWholeMilliseconds

        insertShow(id = 1L, name = "Followed Show")
        insertSeason(seasonId = 10L, showId = 1L, seasonNumber = 1L)
        insertEpisode(episodeId = 100L, seasonId = 10L, showId = 1L, episodeNumber = 1L, title = "Followed Ep", firstAired = airTime)

        insertShow(id = 2L, name = "Unfollowed Show")
        insertSeason(seasonId = 20L, showId = 2L, seasonNumber = 1L)
        insertEpisode(episodeId = 200L, seasonId = 20L, showId = 2L, episodeNumber = 1L, title = "Unfollowed Ep", firstAired = airTime)

        followShow(showId = 1L)

        val result = episodesDao.getUpcomingEpisodesFromFollowedShows(limit = 24.hours)

        result shouldHaveSize 1
        result[0].show_name shouldBe "Followed Show"
    }

    @Test
    fun `should exclude specials from upcoming episodes`() = runTest {
        val now = LocalDate(2025, 4, 1).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(now)

        val airTime = now + 2.hours.inWholeMilliseconds

        insertShow(id = 1L, name = "Show A")
        insertSeason(seasonId = 10L, showId = 1L, seasonNumber = 0L, title = "Specials")
        insertEpisode(episodeId = 100L, seasonId = 10L, showId = 1L, episodeNumber = 1L, title = "Special", firstAired = airTime)
        insertSeason(seasonId = 11L, showId = 1L, seasonNumber = 1L)
        insertEpisode(episodeId = 101L, seasonId = 11L, showId = 1L, episodeNumber = 1L, title = "Regular Ep", firstAired = airTime)
        followShow(showId = 1L)

        val result = episodesDao.getUpcomingEpisodesFromFollowedShows(limit = 24.hours)

        result shouldHaveSize 1
        result[0].title shouldBe "Regular Ep"
    }

    @Test
    fun `should return empty list given no upcoming episodes in window`() = runTest {
        val now = LocalDate(2025, 4, 1).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(now)

        val pastAirTime = now - 2.hours.inWholeMilliseconds

        insertShow(id = 1L, name = "Show A")
        insertSeason(seasonId = 10L, showId = 1L, seasonNumber = 1L)
        insertEpisode(episodeId = 100L, seasonId = 10L, showId = 1L, episodeNumber = 1L, title = "Past Ep", firstAired = pastAirTime)
        followShow(showId = 1L)

        val result = episodesDao.getUpcomingEpisodesFromFollowedShows(limit = 24.hours)

        result.shouldBeEmpty()
    }

    @Test
    fun `should update first_aired given updateFirstAired is called`() = runTest {
        val now = LocalDate(2025, 4, 1).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(now)

        insertShow(id = 1L, name = "Show A")
        insertSeason(seasonId = 10L, showId = 1L, seasonNumber = 1L)
        insertEpisode(episodeId = 100L, seasonId = 10L, showId = 1L, episodeNumber = 1L, title = "Ep 1", firstAired = null)
        followShow(showId = 1L)

        val beforeUpdate = episodesDao.getUpcomingEpisodesFromFollowedShows(limit = 24.hours)
        beforeUpdate.shouldBeEmpty()

        val newAirTime = now + 2.hours.inWholeMilliseconds
        episodesDao.updateFirstAired(showId = 1L, seasonNumber = 1L, episodeNumber = 1L, firstAired = newAirTime)

        val afterUpdate = episodesDao.getUpcomingEpisodesFromFollowedShows(limit = 24.hours)
        afterUpdate shouldHaveSize 1
        afterUpdate[0].first_aired shouldBe newAirTime
    }

    @Test
    fun `should observe upcoming episodes reactively`() = runTest {
        val now = LocalDate(2025, 4, 1).toEpochMillis()
        dateTimeProvider.setCurrentTimeMillis(now)

        insertShow(id = 1L, name = "Show A")
        insertSeason(seasonId = 10L, showId = 1L, seasonNumber = 1L)
        followShow(showId = 1L)

        episodesDao.observeUpcomingEpisodesFromFollowedShows(limit = 24.hours).test {
            awaitItem().shouldBeEmpty()

            val airTime = now + 2.hours.inWholeMilliseconds
            insertEpisode(episodeId = 100L, seasonId = 10L, showId = 1L, episodeNumber = 1L, title = "New Ep", firstAired = airTime)

            val updated = awaitItem()
            updated shouldHaveSize 1
            updated[0].title shouldBe "New Ep"

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return episode given getEpisodeByShowSeasonEpisodeNumber`() = runTest {
        insertShow(id = 1L, name = "Show A")
        insertSeason(seasonId = 10L, showId = 1L, seasonNumber = 1L)
        insertEpisode(episodeId = 100L, seasonId = 10L, showId = 1L, episodeNumber = 3L, title = "Target Ep")

        val result = episodesDao.getEpisodeByShowSeasonEpisodeNumber(
            showTraktId = 1L,
            seasonNumber = 1L,
            episodeNumber = 3L,
        )

        result.shouldNotBeNull()
        result.title shouldBe "Target Ep"
    }

    @Test
    fun `should return null given episode does not exist`() = runTest {
        insertShow(id = 1L, name = "Show A")
        insertSeason(seasonId = 10L, showId = 1L, seasonNumber = 1L)

        val result = episodesDao.getEpisodeByShowSeasonEpisodeNumber(
            showTraktId = 1L,
            seasonNumber = 1L,
            episodeNumber = 99L,
        )

        result.shouldBeNull()
    }

    private fun insertShow(id: Long, name: String) {
        val _ = database.tvShowQueries.upsert(
            trakt_id = Id<TraktId>(id),
            tmdb_id = Id<TmdbId>(id),
            name = name,
            overview = "Overview for $name",
            language = "en",
            year = "2025-01-01",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = "/$id.jpg",
            backdrop_path = null,
        )
    }

    private fun insertSeason(
        seasonId: Long,
        showId: Long,
        seasonNumber: Long,
        title: String = "Season $seasonNumber",
    ) {
        val _ = database.seasonsQueries.upsert(
            id = Id(seasonId),
            show_trakt_id = Id<TraktId>(showId),
            season_number = seasonNumber,
            title = title,
            overview = "Overview",
            episode_count = 10L,
            image_url = null,
        )
    }

    private fun insertEpisode(
        episodeId: Long,
        seasonId: Long,
        showId: Long,
        episodeNumber: Long,
        title: String,
        firstAired: Long? = null,
    ) {
        val _ = database.episodesQueries.upsert(
            id = Id(episodeId),
            season_id = Id(seasonId),
            show_trakt_id = Id<TraktId>(showId),
            title = title,
            overview = "Overview for $title",
            episode_number = episodeNumber,
            runtime = 45L,
            image_url = null,
            ratings = 8.0,
            vote_count = 100L,
            trakt_id = null,
            first_aired = firstAired,
        )
    }

    private fun followShow(showId: Long) {
        val _ = database.followedShowsQueries.upsert(
            id = null,
            traktId = Id(showId),
            tmdbId = Id(showId),
            followedAt = 1000L,
            pendingAction = "NOTHING",
        )
    }

    private fun markEpisodeWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        val _ = database.watchedEpisodesQueries.upsert(
            show_trakt_id = Id(showId),
            episode_id = Id(episodeId),
            season_number = seasonNumber,
            episode_number = episodeNumber,
            watched_at = 1000L,
            pending_action = "NOTHING",
        )
    }
}
