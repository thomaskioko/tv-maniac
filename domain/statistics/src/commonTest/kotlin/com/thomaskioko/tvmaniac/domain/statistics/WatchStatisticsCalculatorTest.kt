package com.thomaskioko.tvmaniac.domain.statistics

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.ratings.implementation.DefaultRatingsDao
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.EpisodeId
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SeasonId
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.WatchStatus
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultWatchedEpisodeDao
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.watchstatus.implementation.DefaultShowWatchStatusDao
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
internal class WatchStatisticsCalculatorTest : BaseDatabaseTest() {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val dateTimeProvider = FakeDateTimeProvider(
        currentTime = Instant.fromEpochMilliseconds(epochMillis(2026, 8, 3, hour = 12)),
    )

    private val watchedEpisodeDao = DefaultWatchedEpisodeDao(database, showIdResolver, dispatchers, dateTimeProvider)
    private val showWatchStatusDao = DefaultShowWatchStatusDao(database, dispatchers)
    private val ratingsDao = DefaultRatingsDao(database, dispatchers)
    private val calculator = WatchStatisticsCalculator(dateTimeProvider)

    private suspend fun calculate(): WatchStatistics = calculator.calculate(
        watches = watchedEpisodeDao.observeWatchedAtWithRuntime().first(),
        mostWatchedShows = watchedEpisodeDao.observeMostWatchedShows(10).first(),
        showCountsByStatus = showWatchStatusDao.observeStatusCounts().first(),
        ratingCounts = ratingsDao.observeUserRatingDistribution().first(),
    )

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should return a zero count for every day and rating given no watch history`() = runTest(testDispatcher) {
        val statistics = calculate()

        statistics.hasWatchHistory shouldBe false
        statistics.hasRuntimeData shouldBe false
        statistics.dailyCounts.size shouldBe 365
        statistics.dailyCounts.all { it.episodeCount == 0 } shouldBe true
        statistics.ratingDistribution.map { it.rating } shouldBe (1..10).toList()
        statistics.ratingDistribution.all { it.count == 0L } shouldBe true
        statistics.streak shouldBe WatchStreak.NONE
        statistics.averageRating shouldBe null
        statistics.topWeekday shouldBe null
        statistics.peakYear shouldBe null
        statistics.mostWatchedShows.shouldBeEmptyList()
    }

    @Test
    fun `should count a run ending today as the current streak`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        markWatched(BREAKING_BAD, episodeNumber = 1, watchedAt = epochMillis(2026, 8, 1))
        markWatched(BREAKING_BAD, episodeNumber = 2, watchedAt = epochMillis(2026, 8, 2))
        markWatched(BREAKING_BAD, episodeNumber = 3, watchedAt = epochMillis(2026, 8, 3))

        val streak = calculate().streak

        streak.currentDays shouldBe 3
        streak.longestDays shouldBe 3
    }

    @Test
    fun `should keep the longest run and reset the current one given a gap`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        markWatched(BREAKING_BAD, episodeNumber = 1, watchedAt = epochMillis(2026, 7, 1))
        markWatched(BREAKING_BAD, episodeNumber = 2, watchedAt = epochMillis(2026, 7, 2))
        markWatched(BREAKING_BAD, episodeNumber = 3, watchedAt = epochMillis(2026, 7, 3))
        markWatched(BREAKING_BAD, episodeNumber = 4, watchedAt = epochMillis(2026, 8, 3))

        val streak = calculate().streak

        streak.currentDays shouldBe 1
        streak.longestDays shouldBe 3
        streak.longestStart shouldBe LocalDate(2026, 7, 1)
        streak.longestEnd shouldBe LocalDate(2026, 7, 3)
    }

    @Test
    fun `should count a run ending yesterday as still current`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        markWatched(BREAKING_BAD, episodeNumber = 1, watchedAt = epochMillis(2026, 8, 1))
        markWatched(BREAKING_BAD, episodeNumber = 2, watchedAt = epochMillis(2026, 8, 2))

        calculate().streak.currentDays shouldBe 2
    }

    @Test
    fun `should count several episodes watched on one day as a single streak day`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        markWatched(BREAKING_BAD, episodeNumber = 1, watchedAt = epochMillis(2026, 8, 3, hour = 9))
        markWatched(BREAKING_BAD, episodeNumber = 2, watchedAt = epochMillis(2026, 8, 3, hour = 21))

        val statistics = calculate()

        statistics.streak.currentDays shouldBe 1
        statistics.streak.longestDays shouldBe 1
    }

    @Test
    fun `should end the daily series on today and cover a year`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        markWatched(BREAKING_BAD, episodeNumber = 1, watchedAt = epochMillis(2026, 8, 3))

        val days = calculate().dailyCounts

        days.size shouldBe 365
        days.last().date shouldBe LocalDate(2026, 8, 3)
        days.last().episodeCount shouldBe 1
        days.first().date shouldBe LocalDate(2025, 8, 4)
    }

    @Test
    fun `should keep watches older than the daily series in the totals`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        markWatched(BREAKING_BAD, episodeNumber = 1, watchedAt = epochMillis(2024, 1, 3))

        val statistics = calculate()

        statistics.hasWatchHistory shouldBe true
        statistics.episodesWatched shouldBe 1L
        statistics.dailyCounts.all { it.episodeCount == 0 } shouldBe true
        statistics.peakYear?.year shouldBe 2024
    }

    @Test
    fun `should sum runtime from the show when the episode has none`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        setShowRuntime(BREAKING_BAD, runtime = 45)
        markWatched(BREAKING_BAD, episodeNumber = 1, watchedAt = epochMillis(2026, 8, 3))
        markWatched(BREAKING_BAD, episodeNumber = 2, watchedAt = epochMillis(2026, 8, 3))

        val statistics = calculate()

        statistics.totalWatchTime.totalMinutes shouldBe 90L
        statistics.hasRuntimeData shouldBe true
    }

    @Test
    fun `should prefer episode runtime over show runtime`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        setShowRuntime(BREAKING_BAD, runtime = 45)
        insertSeasonAndEpisode(tmdbId = BREAKING_BAD)
        markWatchedEpisode(BREAKING_BAD, episodeNumber = 1, episodeId = EPISODE_ID, watchedAt = epochMillis(2026, 8, 3))

        calculate().totalWatchTime.totalMinutes shouldBe 45L
    }

    @Test
    fun `should report zero runtime when neither episode nor show has one`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        markWatched(BREAKING_BAD, episodeNumber = 1, watchedAt = epochMillis(2026, 8, 3))

        val statistics = calculate()

        statistics.hasWatchHistory shouldBe true
        statistics.hasRuntimeData shouldBe false
    }

    @Test
    fun `should report the weekday with the most watches`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        markWatched(BREAKING_BAD, episodeNumber = 1, watchedAt = epochMillis(2026, 8, 3))
        markWatched(BREAKING_BAD, episodeNumber = 2, watchedAt = epochMillis(2026, 7, 27))
        markWatched(BREAKING_BAD, episodeNumber = 3, watchedAt = epochMillis(2026, 8, 1))

        val top = calculate().topWeekday

        top?.dayOfWeek shouldBe DayOfWeek.MONDAY
        top?.episodeCount shouldBe 2
    }

    @Test
    fun `should count watch days against days elapsed this year`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        markWatched(BREAKING_BAD, episodeNumber = 1, watchedAt = epochMillis(2026, 8, 3))
        markWatched(BREAKING_BAD, episodeNumber = 2, watchedAt = epochMillis(2026, 8, 3))
        markWatched(BREAKING_BAD, episodeNumber = 3, watchedAt = epochMillis(2026, 1, 5))
        markWatched(BREAKING_BAD, episodeNumber = 4, watchedAt = epochMillis(2025, 5, 5))

        val thisYear = calculate().watchDaysThisYear

        thisYear.daysWatched shouldBe 2
        thisYear.daysElapsed shouldBe 215
    }

    @Test
    fun `should summarise only the last thirty days`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        setShowRuntime(BREAKING_BAD, runtime = 30)
        markWatched(BREAKING_BAD, episodeNumber = 1, watchedAt = epochMillis(2026, 8, 1))
        markWatched(BREAKING_BAD, episodeNumber = 2, watchedAt = epochMillis(2026, 6, 1))

        val recent = calculate().lastThirtyDays

        recent.episodeCount shouldBe 1
        recent.activeDays shouldBe 1
        recent.minutes shouldBe 30L
    }

    @Test
    fun `should average every rating across shows seasons and episodes`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        insertSeasonAndEpisode(tmdbId = BREAKING_BAD)
        database.ratingsQueries.upsertShowUserRating(
            showId = internalId(BREAKING_BAD),
            userRating = 10L,
            ratedAt = 0L,
            pendingAction = "NOTHING",
        )
        database.ratingsQueries.upsertEpisodeUserRating(
            episodeId = Id<EpisodeId>(EPISODE_ID),
            userRating = 6L,
            ratedAt = 0L,
            pendingAction = "NOTHING",
        )

        val rating = calculate().averageRating

        rating?.average shouldBe 8.0
        rating?.ratedCount shouldBe 2L
    }

    @Test
    fun `should count tracked and completed shows`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        insertShow(tmdbId = THE_WIRE)
        database.showWatchStatusQueries.upsert(
            showId = internalId(BREAKING_BAD),
            status = WatchStatus.COMPLETED,
            lastWatchedAt = null,
            lastSyncedAt = null,
        )
        database.showWatchStatusQueries.upsert(
            showId = internalId(THE_WIRE),
            status = WatchStatus.WATCHING,
            lastWatchedAt = null,
            lastSyncedAt = null,
        )

        val tracked = calculate().showsTracked

        tracked.total shouldBe 2L
        tracked.completed shouldBe 1L
        tracked.completionRate shouldBe 50
    }

    @Test
    fun `should rank shows by watched episode count`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        insertShow(tmdbId = THE_WIRE)
        markWatched(BREAKING_BAD, episodeNumber = 1, watchedAt = epochMillis(2026, 8, 1))
        markWatched(THE_WIRE, episodeNumber = 1, watchedAt = epochMillis(2026, 8, 1))
        markWatched(THE_WIRE, episodeNumber = 2, watchedAt = epochMillis(2026, 8, 2))

        val shows = calculate().mostWatchedShows

        shows.map { it.showId } shouldBe listOf(THE_WIRE, BREAKING_BAD)
        shows.first().episodeCount shouldBe 2
    }

    @Test
    fun `should count shows for every watch status`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        insertShow(tmdbId = THE_WIRE)
        database.showWatchStatusQueries.upsert(
            showId = internalId(BREAKING_BAD),
            status = WatchStatus.COMPLETED,
            lastWatchedAt = null,
            lastSyncedAt = null,
        )
        database.showWatchStatusQueries.upsert(
            showId = internalId(THE_WIRE),
            status = WatchStatus.COMPLETED,
            lastWatchedAt = null,
            lastSyncedAt = null,
        )

        val counts = calculate().showsByWatchStatus.associate { it.status to it.showCount }

        counts.keys shouldBe WatchStatus.entries.toSet()
        counts[WatchStatus.COMPLETED] shouldBe 2L
        counts[WatchStatus.DROPPED] shouldBe 0L
    }

    @Test
    fun `should combine show season and episode ratings into one distribution`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        insertSeasonAndEpisode(tmdbId = BREAKING_BAD)
        database.ratingsQueries.upsertShowUserRating(
            showId = internalId(BREAKING_BAD),
            userRating = 9L,
            ratedAt = 0L,
            pendingAction = "NOTHING",
        )
        database.ratingsQueries.upsertSeasonUserRating(
            seasonId = Id<SeasonId>(SEASON_ID),
            userRating = 9L,
            ratedAt = 0L,
            pendingAction = "NOTHING",
        )
        database.ratingsQueries.upsertEpisodeUserRating(
            episodeId = Id<EpisodeId>(EPISODE_ID),
            userRating = 4L,
            ratedAt = 0L,
            pendingAction = "NOTHING",
        )

        val distribution = calculate().ratingDistribution.associate { it.rating to it.count }

        distribution[9] shouldBe 2L
        distribution[4] shouldBe 1L
        distribution[1] shouldBe 0L
    }

    @Test
    fun `should exclude ratings pending deletion`() = runTest(testDispatcher) {
        insertShow(tmdbId = BREAKING_BAD)
        database.ratingsQueries.upsertShowUserRating(
            showId = internalId(BREAKING_BAD),
            userRating = 7L,
            ratedAt = 0L,
            pendingAction = "DELETE",
        )

        calculate().ratingDistribution.all { it.count == 0L } shouldBe true
    }

    private fun internalId(tmdbId: Long): Id<ShowId> =
        database.tvShowQueries.getShowIdByTmdbId(Id<TmdbId>(tmdbId)).executeAsOne()

    private fun setShowRuntime(tmdbId: Long, runtime: Long) {
        database.tvShowQueries.updateRuntime(runtime = runtime, tmdbId = Id<TmdbId>(tmdbId))
    }

    private fun insertShow(tmdbId: Long) {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(tmdbId),
            name = "show-$tmdbId",
            overview = "overview",
            language = "en",
            year = "2020",
            ratings = 8.0,
            vote_count = 100,
            genres = emptyList(),
            status = "Ended",
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
        showIdForTraktId(traktId = tmdbId, tmdbId = tmdbId)
    }

    private fun insertSeasonAndEpisode(tmdbId: Long) {
        val showId = internalId(tmdbId)
        database.seasonsQueries.upsert(
            id = Id<SeasonId>(SEASON_ID),
            show_id = showId,
            season_number = 1,
            episode_count = 10,
            title = "Season 1",
            overview = "overview",
            image_url = null,
        )
        database.episodesQueries.upsert(
            id = Id<EpisodeId>(EPISODE_ID),
            season_id = Id<SeasonId>(SEASON_ID),
            show_id = showId,
            title = "Episode 1",
            overview = "overview",
            runtime = 45,
            vote_count = 10,
            ratings = 8.0,
            episode_number = 1,
            image_url = null,
            first_aired = null,
        )
    }

    private fun markWatchedEpisode(tmdbId: Long, episodeNumber: Long, episodeId: Long, watchedAt: Long) {
        database.watchedEpisodesQueries.upsert(
            show_id = internalId(tmdbId),
            episode_id = Id<EpisodeId>(episodeId),
            season_number = 1,
            episode_number = episodeNumber,
            watched_at = watchedAt,
            pending_action = "NOTHING",
        )
    }

    private fun markWatched(tmdbId: Long, episodeNumber: Long, watchedAt: Long) {
        database.watchedEpisodesQueries.upsert(
            show_id = internalId(tmdbId),
            episode_id = null,
            season_number = 1,
            episode_number = episodeNumber,
            watched_at = watchedAt,
            pending_action = "NOTHING",
        )
    }

    private fun List<*>.shouldBeEmptyList() {
        isEmpty() shouldBe true
    }

    private companion object {
        const val BREAKING_BAD = 1396L
        const val THE_WIRE = 1438L
        const val SEASON_ID = 3572L
        const val EPISODE_ID = 62085L

        fun epochMillis(year: Int, month: Int, day: Int, hour: Int = 12): Long =
            LocalDateTime(year, month, day, hour, 0).toInstant(TimeZone.UTC).toEpochMilliseconds()
    }
}
