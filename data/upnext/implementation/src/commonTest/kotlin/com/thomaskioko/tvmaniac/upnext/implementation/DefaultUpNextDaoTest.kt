package com.thomaskioko.tvmaniac.upnext.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.upnext.api.UpNextDao
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
internal class DefaultUpNextDaoTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val dateTimeProvider = FakeDateTimeProvider()
    private lateinit var dao: UpNextDao

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dateTimeProvider.setCurrentTimeMillis(NOW)
        dao = DefaultUpNextDao(database, coroutineDispatcher, dateTimeProvider)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should exclude caught-up show given next episode has no air date`() = runTest {
        insertShow(id = 1L, name = "Outer Banks")
        insertFollowedShow(showId = 1L)
        insertShowMetadata(showId = 1L, watchedCount = 40, totalCount = 40)
        dao.upsert(
            showTraktId = 1L,
            episodeTraktId = 5001L,
            seasonNumber = 5L,
            episodeNumber = 1L,
            title = "Episode 1",
            overview = null,
            runtime = null,
            firstAired = null,
            imageUrl = null,
            isShowComplete = false,
            lastEpisodeSeason = 4L,
            lastEpisodeNumber = 10L,
            traktLastWatchedAt = NOW - 1000,
            updatedAt = NOW,
        )

        dao.observeNextEpisodesFromCache().test {
            awaitItem().size shouldBe 0
        }
    }

    @Test
    fun `should exclude caught-up show given next episode airs in the future`() = runTest {
        val futureAirDate = NOW + 86_400_000L
        insertShow(id = 1L, name = "Outer Banks")
        insertFollowedShow(showId = 1L)
        insertShowMetadata(showId = 1L, watchedCount = 40, totalCount = 40)
        dao.upsert(
            showTraktId = 1L,
            episodeTraktId = 5001L,
            seasonNumber = 5L,
            episodeNumber = 1L,
            title = "Episode 1",
            overview = null,
            runtime = null,
            firstAired = futureAirDate,
            imageUrl = null,
            isShowComplete = false,
            lastEpisodeSeason = 4L,
            lastEpisodeNumber = 10L,
            traktLastWatchedAt = NOW - 1000,
            updatedAt = NOW,
        )

        dao.observeNextEpisodesFromCache().test {
            awaitItem().size shouldBe 0
        }
    }

    @Test
    fun `should include caught-up show given next episode has already aired`() = runTest {
        val pastAirDate = NOW - 86_400_000L
        insertShow(id = 1L, name = "Severance")
        insertFollowedShow(showId = 1L)
        insertShowMetadata(showId = 1L, watchedCount = 10, totalCount = 10)
        dao.upsert(
            showTraktId = 1L,
            episodeTraktId = 2001L,
            seasonNumber = 2L,
            episodeNumber = 1L,
            title = "Hello, Ms. Cobel",
            overview = null,
            runtime = 45L,
            firstAired = pastAirDate,
            imageUrl = null,
            isShowComplete = false,
            lastEpisodeSeason = 1L,
            lastEpisodeNumber = 9L,
            traktLastWatchedAt = NOW - 2000,
            updatedAt = NOW,
        )

        dao.observeNextEpisodesFromCache().test {
            val items = awaitItem()
            items.size shouldBe 1
            items[0].showName shouldBe "Severance"
        }
    }

    @Test
    fun `should include show given user has unwatched aired episodes`() = runTest {
        insertShow(id = 1L, name = "Wonder Man")
        insertFollowedShow(showId = 1L)
        insertShowMetadata(showId = 1L, watchedCount = 0, totalCount = 8)
        dao.upsert(
            showTraktId = 1L,
            episodeTraktId = 1001L,
            seasonNumber = 1L,
            episodeNumber = 1L,
            title = "Matinee",
            overview = null,
            runtime = 35L,
            firstAired = NOW - 86_400_000L,
            imageUrl = null,
            isShowComplete = false,
            lastEpisodeSeason = 1L,
            lastEpisodeNumber = 8L,
            traktLastWatchedAt = null,
            updatedAt = NOW,
        )

        dao.observeNextEpisodesFromCache().test {
            val items = awaitItem()
            items.size shouldBe 1
            items[0].showName shouldBe "Wonder Man"
        }
    }

    @Test
    fun `should include show given no metadata exists`() = runTest {
        insertShow(id = 1L, name = "New Show")
        insertFollowedShow(showId = 1L)
        dao.upsert(
            showTraktId = 1L,
            episodeTraktId = 1001L,
            seasonNumber = 1L,
            episodeNumber = 1L,
            title = "Pilot",
            overview = null,
            runtime = 45L,
            firstAired = null,
            imageUrl = null,
            isShowComplete = false,
            lastEpisodeSeason = 1L,
            lastEpisodeNumber = 10L,
            traktLastWatchedAt = null,
            updatedAt = NOW,
        )

        dao.observeNextEpisodesFromCache().test {
            val items = awaitItem()
            items.size shouldBe 1
            items[0].showName shouldBe "New Show"
        }
    }

    private fun insertShow(id: Long, name: String) {
        val _ = database.tvShowQueries.upsert(
            trakt_id = Id(id),
            tmdb_id = Id(id),
            name = name,
            overview = "Overview for $name",
            language = "en",
            year = "2024-01-01",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = "/$id.jpg",
            backdrop_path = "/$id-back.jpg",
        )
    }

    private fun insertFollowedShow(showId: Long) {
        val _ = database.followedShowsQueries.upsert(
            id = null,
            traktId = Id(showId),
            tmdbId = Id(showId),
            followedAt = NOW - 10_000,
            pendingAction = "NOTHING",
        )
    }

    private fun insertShowMetadata(showId: Long, watchedCount: Long, totalCount: Long) {
        val _ = database.showMetadataQueries.upsertWithProgress(
            show_trakt_id = Id(showId),
            cached_watched_count = watchedCount,
            cached_total_count = totalCount,
        )
    }

    private companion object {
        private val NOW = LocalDate(2025, 6, 15).toEpochMillis()
    }
}
