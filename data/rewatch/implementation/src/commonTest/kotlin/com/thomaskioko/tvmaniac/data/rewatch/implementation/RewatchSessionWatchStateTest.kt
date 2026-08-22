package com.thomaskioko.tvmaniac.data.rewatch.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class RewatchSessionWatchStateTest : BaseDatabaseTest() {

    private lateinit var dao: DefaultRewatchSessionDao
    private var showId: Long = 0

    @BeforeTest
    fun setup() {
        dao = DefaultRewatchSessionDao(
            database = database,
            dispatchers = AppCoroutineDispatchers(
                main = Dispatchers.Unconfined,
                io = Dispatchers.Unconfined,
                computation = Dispatchers.Unconfined,
                databaseWrite = Dispatchers.Unconfined,
                databaseRead = Dispatchers.Unconfined,
            ),
        )
        showId = addShow(tmdbId = TMDB_ID)
        addSeason(seasonId = SEASON_ID, showId = showId)
        addEpisode(episodeId = EPISODE_ONE, showId = showId, episodeNumber = 1L)
        addEpisode(episodeId = EPISODE_TWO, showId = showId, episodeNumber = 2L)
        addEpisode(episodeId = EPISODE_THREE, showId = showId, episodeNumber = 3L)
        markWatched(episodeId = EPISODE_ONE, episodeNumber = 1L, watchedAt = FIRST_WATCHED_AT)
        markWatched(episodeId = EPISODE_TWO, episodeNumber = 2L, watchedAt = SECOND_WATCHED_AT)
        addToContinueWatching(lastWatchedAt = SECOND_WATCHED_AT)
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should return the same watch progress given a rewatch session exists for the show`() {
        val before = database.showWatchStatusQueries.watchProgressForShow(Id(showId)).executeAsOne()

        startRewatchSession()

        database.showWatchStatusQueries.watchProgressForShow(Id(showId)).executeAsOne() shouldBe before
    }

    @Test
    fun `should return the same next episode for the show given a rewatch session exists`() {
        val before = database.showsNextToWatchQueries
            .nextEpisodeForShow(showId = Id(showId), includeSpecials = 0)
            .executeAsOneOrNull()

        startRewatchSession()

        database.showsNextToWatchQueries
            .nextEpisodeForShow(showId = Id(showId), includeSpecials = 0)
            .executeAsOneOrNull() shouldBe before
    }

    @Test
    fun `should return the same watchlist next episodes given a rewatch session exists`() {
        val before = database.showsNextToWatchQueries.nextEpisodesForWatchlist(includeSpecials = 0).executeAsList()

        startRewatchSession()

        database.showsNextToWatchQueries.nextEpisodesForWatchlist(includeSpecials = 0).executeAsList() shouldBe before
    }

    @Test
    fun `should return the same completed shows for the watchlist given a rewatch session exists`() {
        markWatched(episodeId = EPISODE_THREE, episodeNumber = 3L, watchedAt = THIRD_WATCHED_AT)
        val before = database.showsNextToWatchQueries.completedShowsForWatchlist().executeAsList()

        startRewatchSession()

        database.showsNextToWatchQueries.completedShowsForWatchlist().executeAsList() shouldBe before
    }

    @Test
    fun `should offer no next episode for a fully watched show given a rewatch session exists`() {
        markWatched(episodeId = EPISODE_THREE, episodeNumber = 3L, watchedAt = THIRD_WATCHED_AT)
        database.showsNextToWatchQueries.nextEpisodesForWatchlist(includeSpecials = 0)
            .executeAsList().single().episode_id.shouldBeNull()

        startRewatchSession()

        database.showsNextToWatchQueries.nextEpisodesForWatchlist(includeSpecials = 0)
            .executeAsList().single().episode_id.shouldBeNull()
    }

    @Test
    fun `should keep a fully watched show complete given a rewatch session exists`() {
        markWatched(episodeId = EPISODE_THREE, episodeNumber = 3L, watchedAt = THIRD_WATCHED_AT)

        startRewatchSession()

        val progress = database.showWatchStatusQueries.watchProgressForShow(Id(showId)).executeAsOne()
        progress.watched_count shouldBe progress.total_count
    }

    @Test
    fun `should return the same library count given a rewatch session exists`() {
        val before = database.libraryQueries.countLibraryShows().executeAsOne()

        startRewatchSession()

        database.libraryQueries.countLibraryShows().executeAsOne() shouldBe before
    }

    private fun startRewatchSession() {
        val sessionId = dao.openSession(showId = showId, startedAt = SESSION_STARTED_AT)
        dao.addEpisodeToSession(sessionId = sessionId, episodeId = EPISODE_ONE, watchedAt = REWATCHED_AT)
    }

    private fun addShow(tmdbId: Long): Long {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(tmdbId),
            name = "Test Show",
            overview = "Overview",
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
        return database.tvShowQueries.getShowIdByTmdbId(Id(tmdbId)).executeAsOne().id
    }

    private fun addSeason(seasonId: Long, showId: Long) {
        database.seasonsQueries.upsert(
            id = Id(seasonId),
            show_id = Id(showId),
            season_number = 1L,
            episode_count = 3L,
            title = "Season $seasonId",
            overview = "Overview",
            image_url = null,
        )
    }

    private fun addEpisode(episodeId: Long, showId: Long, episodeNumber: Long) {
        database.episodesQueries.upsert(
            id = Id(episodeId),
            season_id = Id(SEASON_ID),
            show_id = Id(showId),
            title = "Episode $episodeId",
            overview = "Overview",
            runtime = 40L,
            vote_count = 10L,
            ratings = 8.0,
            episode_number = episodeNumber,
            image_url = null,
            first_aired = PAST_AIR_DATE,
        )
    }

    private fun markWatched(episodeId: Long, episodeNumber: Long, watchedAt: Long) {
        database.watchedEpisodesQueries.upsert(
            show_id = Id(showId),
            episode_id = Id(episodeId),
            season_number = 1L,
            episode_number = episodeNumber,
            watched_at = watchedAt,
            pending_action = "NOTHING",
        )
    }

    private fun addToContinueWatching(lastWatchedAt: Long) {
        database.continueWatchingQueries.upsert(
            showId = Id(showId),
            tmdbId = Id(TMDB_ID),
            airedEpisodes = 3L,
            completedCount = 0L,
            lastWatchedAt = lastWatchedAt,
            lastUpdatedAt = lastWatchedAt,
            title = "Test Show",
            year = 2023L,
        )
    }

    private companion object {
        private const val TMDB_ID = 777L
        private const val SEASON_ID = 100L
        private const val EPISODE_ONE = 200L
        private const val EPISODE_TWO = 201L
        private const val EPISODE_THREE = 202L
        private const val PAST_AIR_DATE = 1_600_000_000_000L
        private const val FIRST_WATCHED_AT = 1_650_000_000_000L
        private const val SECOND_WATCHED_AT = 1_660_000_000_000L
        private const val THIRD_WATCHED_AT = 1_670_000_000_000L
        private const val SESSION_STARTED_AT = 1_700_000_000_000L
        private const val REWATCHED_AT = 1_750_000_000_000L
    }
}
