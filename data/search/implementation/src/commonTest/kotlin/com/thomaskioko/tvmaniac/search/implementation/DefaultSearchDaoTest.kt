package com.thomaskioko.tvmaniac.search.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
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
internal class DefaultSearchDaoTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var dao: DefaultSearchDao

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dao = DefaultSearchDao(database = database, showIdResolver = showIdResolver, dispatchers = dispatchers)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should mark result in library given the show is followed`() = runTest {
        insertShow(tmdbId = TMDB_ID, name = "Breaking Bad")
        dao.upsertResult(query = QUERY, tmdbId = TMDB_ID, position = 0, score = 9.0)
        val internalShowId = database.tvShowQueries.getShowIdByTmdbId(Id(TMDB_ID)).executeAsOne()
        database.followedShowsQueries.upsert(
            showId = internalShowId,
            tmdbId = Id(TMDB_ID),
            followedAt = 0L,
            pendingAction = "NOTHING",
        )

        dao.observeResults(QUERY).test {
            val results = awaitItem()
            results.size shouldBe 1
            results.first().inLibrary shouldBe true
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should remove result given the underlying show is deleted`() = runTest {
        insertShow(tmdbId = TMDB_ID, name = "Breaking Bad")
        showIdForTraktId(traktId = TMDB_ID, tmdbId = TMDB_ID)
        dao.upsertResult(query = QUERY, tmdbId = TMDB_ID, position = 0, score = null)

        database.tvShowQueries.delete(TMDB_ID)

        dao.observeResults(QUERY).test {
            awaitItem() shouldBe emptyList()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should map episode numbers to episode count given results are read`() = runTest {
        insertShow(tmdbId = TMDB_ID, name = "Breaking Bad", episodeNumbers = "62")
        dao.upsertResult(query = QUERY, tmdbId = TMDB_ID, position = 0, score = null)

        dao.observeResults(QUERY).test {
            val results = awaitItem()
            results.first().episodeCount shouldBe 62
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun insertShow(tmdbId: Long, name: String, episodeNumbers: String? = null) {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(tmdbId),
            name = name,
            overview = "Overview",
            language = "en",
            year = "2008",
            ratings = 9.5,
            vote_count = 1000,
            genres = listOf("Drama"),
            status = "Ended",
            episode_numbers = episodeNumbers,
            season_numbers = null,
            poster_path = "/poster.jpg",
            backdrop_path = null,
        )
    }

    private companion object {
        private const val QUERY = "trakt:breaking bad"
        private const val TMDB_ID = 1396L
    }
}
