package com.thomaskioko.tvmaniac.data.rewatch.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchSessionDao
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultRewatchSessionDaoTest : BaseDatabaseTest() {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val coroutineDispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var dao: RewatchSessionDao
    private var showId: Long = 0

    @BeforeTest
    fun setup() {
        dao = DefaultRewatchSessionDao(database, coroutineDispatchers)
        showId = addShow(tmdbId = TMDB_ID)
        addSeason(seasonId = SEASON_ID, showId = showId)
        addEpisode(episodeId = EPISODE_ID, seasonId = SEASON_ID, showId = showId)
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should return a generated id given a session is opened`() = runTest {
        val sessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)

        sessionId shouldBe 1L
    }

    @Test
    fun `should return distinct ids given two sessions are opened`() = runTest {
        val firstSessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)
        val secondSessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)

        firstSessionId shouldBe 1L
        secondSessionId shouldBe 2L
    }

    @Test
    fun `should emit an empty list given no session exists for a show`() = runTest {
        dao.observeSessionsForShow(showId).test {
            awaitItem().shouldBeEmpty()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should emit the open session given sessions for a show are observed`() = runTest {
        val sessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)

        dao.observeSessionsForShow(showId).test {
            val sessions = awaitItem()
            sessions.size shouldBe 1
            sessions.first().id shouldBe sessionId
            sessions.first().showId shouldBe showId
            sessions.first().startedAt shouldBe STARTED_AT
            sessions.first().closedAt shouldBe null
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should set closed at given a session is closed`() = runTest {
        val sessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)

        dao.closeSession(sessionId = sessionId, closedAt = CLOSED_AT)

        dao.observeSessionsForShow(showId).test {
            awaitItem().first().closedAt shouldBe CLOSED_AT
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should return no rewatches given no rewatch session exists for an episode`() = runTest {
        dao.observeEpisodeRewatches(EPISODE_ID).first() shouldBe 0L
    }

    @Test
    fun `should count the session row given an episode was watched again`() = runTest {
        val sessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)

        dao.addEpisodeToSession(sessionId = sessionId, episodeId = EPISODE_ID, watchedAt = REWATCHED_AT)

        dao.observeEpisodeRewatches(EPISODE_ID).first() shouldBe 1L
    }

    @Test
    fun `should count every session row given an episode was watched again more than once`() = runTest {
        val firstSessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)
        dao.addEpisodeToSession(sessionId = firstSessionId, episodeId = EPISODE_ID, watchedAt = REWATCHED_AT)
        val secondSessionId = dao.openSession(showId = showId, startedAt = REWATCHED_AT)
        dao.addEpisodeToSession(sessionId = secondSessionId, episodeId = EPISODE_ID, watchedAt = SECOND_REWATCHED_AT)

        dao.observeEpisodeRewatches(EPISODE_ID).first() shouldBe 2L
    }

    @Test
    fun `should return the row id given an episode is added to a session`() = runTest {
        val sessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)

        val rowId = dao.addEpisodeToSession(sessionId = sessionId, episodeId = EPISODE_ID, watchedAt = REWATCHED_AT)

        rowId shouldBe 1L
    }

    @Test
    fun `should include an episode in unsentEpisodes given it was never synced`() = runTest {
        val sessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)
        dao.addEpisodeToSession(sessionId = sessionId, episodeId = EPISODE_ID, watchedAt = REWATCHED_AT)

        val unsent = dao.unsentEpisodes()

        unsent.size shouldBe 1
        unsent.first().sessionId shouldBe sessionId
        unsent.first().showId shouldBe showId
        unsent.first().seasonNumber shouldBe 1L
        unsent.first().episodeNumber shouldBe 1L
        unsent.first().watchedAt shouldBe REWATCHED_AT
    }

    @Test
    fun `should exclude an episode from unsentEpisodes given it was marked synced`() = runTest {
        val sessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)
        val rowId = dao.addEpisodeToSession(sessionId = sessionId, episodeId = EPISODE_ID, watchedAt = REWATCHED_AT)

        dao.markEpisodeSynced(rowId = rowId, syncedAt = REWATCHED_AT)

        dao.unsentEpisodes().shouldBeEmpty()
    }

    @Test
    fun `should return null given no open session exists for a show`() = runTest {
        dao.openSessionForShow(showId).shouldBeNull()
    }

    @Test
    fun `should return the open session given one exists for a show`() = runTest {
        val sessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)

        val openSession = dao.openSessionForShow(showId)

        openSession.shouldNotBeNull()
        openSession.id shouldBe sessionId
        openSession.closedAt.shouldBeNull()
    }

    @Test
    fun `should return null given the only session for a show is closed`() = runTest {
        val sessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)
        dao.closeSession(sessionId = sessionId, closedAt = CLOSED_AT)

        dao.openSessionForShow(showId).shouldBeNull()
    }

    @Test
    fun `should return the most recently started open session given multiple are open`() = runTest {
        dao.openSession(showId = showId, startedAt = STARTED_AT)
        val secondSessionId = dao.openSession(showId = showId, startedAt = REWATCHED_AT)

        dao.openSessionForShow(showId)?.id shouldBe secondSessionId
    }

    @Test
    fun `should return null given no session exists with the given id`() = runTest {
        dao.sessionById(999L).shouldBeNull()
    }

    @Test
    fun `should return the session given one exists with the given id`() = runTest {
        val sessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)

        dao.sessionById(sessionId)?.id shouldBe sessionId
    }

    @Test
    fun `should return a null provider session id given none was set`() = runTest {
        val sessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)

        dao.sessionById(sessionId)?.providerSessionId.shouldBeNull()
    }

    @Test
    fun `should persist the provider session id given it is set`() = runTest {
        val sessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)

        dao.setProviderSessionId(sessionId = sessionId, providerSessionId = PROVIDER_SESSION_ID)

        dao.sessionById(sessionId)?.providerSessionId shouldBe PROVIDER_SESSION_ID
    }

    @Test
    fun `should carry the provider session id given the open session for a show is fetched`() = runTest {
        val sessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)
        dao.setProviderSessionId(sessionId = sessionId, providerSessionId = PROVIDER_SESSION_ID)

        dao.openSessionForShow(showId)?.providerSessionId shouldBe PROVIDER_SESSION_ID
    }

    @Test
    fun `should return no sessions and a play count of one given all data is cleared`() = runTest {
        val sessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)
        dao.addEpisodeToSession(sessionId = sessionId, episodeId = EPISODE_ID, watchedAt = REWATCHED_AT)

        dao.clearAll()

        dao.observeSessionsForShow(showId).test {
            awaitItem().shouldBeEmpty()
            cancelAndConsumeRemainingEvents()
        }
        dao.observeEpisodeRewatches(EPISODE_ID).first() shouldBe 0L
    }

    private fun addShow(tmdbId: Long): Long {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(tmdbId),
            name = "Test Show $tmdbId",
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
            episode_count = 10L,
            title = "Season $seasonId",
            overview = "Overview",
            image_url = null,
        )
    }

    private fun addEpisode(episodeId: Long, seasonId: Long, showId: Long) {
        database.episodesQueries.upsert(
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

    @Test
    fun `should emit an empty status given the show has no sessions`() = runTest {
        dao.observeRewatchStatus(showId).test {
            val status = awaitItem()

            status.finishedCount shouldBe 0
            status.openSession.shouldBeNull()
        }
    }

    @Test
    fun `should emit the open session given one is under way`() = runTest {
        val sessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)
        dao.addEpisodeToSession(sessionId = sessionId, episodeId = EPISODE_ID, watchedAt = REWATCHED_AT)

        dao.observeRewatchStatus(showId).test {
            val status = awaitItem()

            status.openSession.shouldNotBeNull().id shouldBe sessionId
            status.openSession.shouldNotBeNull().watchedEpisodes shouldBe 1
            status.finishedCount shouldBe 0
        }
    }

    @Test
    fun `should emit the finished count given a session was closed`() = runTest {
        val sessionId = dao.openSession(showId = showId, startedAt = STARTED_AT)
        dao.closeSession(sessionId = sessionId, closedAt = CLOSED_AT)

        dao.observeRewatchStatus(showId).test {
            val status = awaitItem()

            status.finishedCount shouldBe 1
            status.openSession.shouldBeNull()
        }
    }

    private companion object {
        private const val TMDB_ID = 555L
        private const val SEASON_ID = 100L
        private const val EPISODE_ID = 200L
        private const val STARTED_AT = 1_700_000_000_000L
        private const val REWATCHED_AT = 1_750_000_000_000L
        private const val SECOND_REWATCHED_AT = 1_800_000_000_000L
        private const val CLOSED_AT = 1_760_000_000_000L
        private const val PROVIDER_SESSION_ID = 7482L
    }
}
