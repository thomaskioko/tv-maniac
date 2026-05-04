package com.thomaskioko.tvmaniac.upnext.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultEpisodesDao
import com.thomaskioko.tvmaniac.followedshows.implementation.DefaultFollowedShowsDao
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.shows.implementation.DefaultTvShowsDao
import com.thomaskioko.tvmaniac.trakt.api.TimePeriod
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
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
internal class DefaultUpNextRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val dateTimeProvider = FakeDateTimeProvider()
    private val requestManagerRepository = FakeRequestManagerRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val seasonsRepository = FakeSeasonsRepository()
    private val datastoreRepository = FakeDatastoreRepository()

    private lateinit var upNextDao: DefaultUpNextDao
    private lateinit var episodesDao: DefaultEpisodesDao
    private lateinit var followedShowsDao: DefaultFollowedShowsDao
    private lateinit var tvShowsDao: DefaultTvShowsDao
    private lateinit var store: ShowUpNextStore
    private lateinit var repository: DefaultUpNextRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dateTimeProvider.setCurrentTimeMillis(NOW)

        upNextDao = DefaultUpNextDao(database, dispatchers, dateTimeProvider)
        episodesDao = DefaultEpisodesDao(database, dispatchers, dateTimeProvider)
        followedShowsDao = DefaultFollowedShowsDao(database, dispatchers)
        tvShowsDao = DefaultTvShowsDao(database, dispatchers)

        store = ShowUpNextStore(
            traktRemoteDataSource = FakeRemoteDataSource(),
            upNextDao = upNextDao,
            requestManagerRepository = requestManagerRepository,
            dateTimeProvider = dateTimeProvider,
            dispatchers = dispatchers,
        )

        repository = DefaultUpNextRepository(
            upNextDao = upNextDao,
            showUpNextStore = store,
            datastoreRepository = datastoreRepository,
            episodesDao = episodesDao,
            followedShowsDao = followedShowsDao,
            tvShowsDao = tvShowsDao,
            showDetailsRepository = showDetailsRepository,
            seasonDetailsRepository = seasonDetailsRepository,
            seasonsRepository = seasonsRepository,
            requestManagerRepository = requestManagerRepository,
            dateTimeProvider = dateTimeProvider,
            logger = FakeLogger(),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should populate next episode from local data given user is unauthenticated`() = runTest {
        insertShow(id = 1L, name = "Severance")
        insertFollowedShow(showId = 1L, pendingAction = "UPLOAD")
        insertSeason(showId = 1L, seasonNumber = 1)
        insertEpisode(showId = 1L, seasonId = 101L, episodeNumber = 1, title = "Good News About Hell")
        insertEpisode(showId = 1L, seasonId = 101L, episodeNumber = 2, title = "Half Loop")

        repository.updateUpNextForShow(showTraktId = 1L)

        upNextDao.observeNextEpisodesFromCache().test {
            val items = awaitItem()
            items.size shouldBe 1
            items[0].showName shouldBe "Severance"
            items[0].episodeNumber shouldBe 1L
            items[0].seasonNumber shouldBe 1L
        }
    }

    @Test
    fun `should populate next episodes for all followed shows given batch refresh and user is unauthenticated`() = runTest {
        insertShow(id = 1L, name = "Severance")
        insertShow(id = 2L, name = "The Bear")
        insertFollowedShow(showId = 1L, pendingAction = "UPLOAD")
        insertFollowedShow(showId = 2L, pendingAction = "UPLOAD")
        insertSeason(showId = 1L, seasonNumber = 1)
        insertSeason(showId = 2L, seasonNumber = 1)
        insertEpisode(showId = 1L, seasonId = 101L, episodeNumber = 1, title = "Good News About Hell")
        insertEpisode(showId = 2L, seasonId = 201L, episodeNumber = 1, title = "System")

        requestManagerRepository.requestValid = false
        repository.fetchUpNextEpisodes(forceRefresh = false)

        upNextDao.observeNextEpisodesFromCache().test {
            val items = awaitItem()
            items.size shouldBe 2
        }
    }

    @Test
    fun `should not include deleted shows given batch refresh and user is unauthenticated`() = runTest {
        insertShow(id = 1L, name = "Severance")
        insertShow(id = 2L, name = "Deleted Show")
        insertFollowedShow(showId = 1L, pendingAction = "UPLOAD")
        insertFollowedShow(showId = 2L, pendingAction = "DELETE")
        insertSeason(showId = 1L, seasonNumber = 1)
        insertSeason(showId = 2L, seasonNumber = 1)
        insertEpisode(showId = 1L, seasonId = 101L, episodeNumber = 1, title = "Good News About Hell")
        insertEpisode(showId = 2L, seasonId = 201L, episodeNumber = 1, title = "Pilot")

        requestManagerRepository.requestValid = false
        repository.fetchUpNextEpisodes(forceRefresh = false)

        upNextDao.observeNextEpisodesFromCache().test {
            val items = awaitItem()
            items.size shouldBe 1
            items[0].showName shouldBe "Severance"
        }
    }

    @Test
    fun `should set lastWatchedAt given episode is marked as watched and user is unauthenticated`() = runTest {
        insertShow(id = 1L, name = "Severance")
        insertFollowedShow(showId = 1L, pendingAction = "UPLOAD")
        insertSeason(showId = 1L, seasonNumber = 1)
        insertEpisode(showId = 1L, seasonId = 101L, episodeNumber = 1, title = "Good News About Hell")
        insertEpisode(showId = 1L, seasonId = 101L, episodeNumber = 2, title = "Half Loop")

        repository.updateUpNextForShow(showTraktId = 1L)

        insertWatchedEpisode(showId = 1L, episodeId = 1001L, seasonNumber = 1, episodeNumber = 1)

        repository.fetchUpNext(showTraktId = 1L, seasonNumber = 1, episodeNumber = 1)

        upNextDao.observeNextEpisodesFromCache().test {
            val items = awaitItem()
            items.size shouldBe 1
            items[0].episodeNumber shouldBe 2L
            items[0].lastWatchedAt shouldBe NOW
        }
    }

    @Test
    fun `should count followed shows excluding deleted entries`() = runTest {
        insertShow(id = 1L, name = "Show A")
        insertShow(id = 2L, name = "Show B")
        insertShow(id = 3L, name = "Show C")
        insertFollowedShow(showId = 1L, pendingAction = "UPLOAD")
        insertFollowedShow(showId = 2L, pendingAction = "NOTHING")
        insertFollowedShow(showId = 3L, pendingAction = "DELETE")

        repository.observeFollowedShowsCount().test {
            awaitItem() shouldBe 2
        }
    }

    private fun insertShow(id: Long, name: String) {
        database.tvShowQueries.upsert(
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

    private fun insertFollowedShow(
        showId: Long,
        followedAt: Long = NOW - 10_000,
        pendingAction: String = "NOTHING",
    ) {
        database.followedShowsQueries.upsert(
            id = null,
            traktId = Id(showId),
            tmdbId = Id(showId),
            followedAt = followedAt,
            pendingAction = pendingAction,
        )
    }

    private fun insertSeason(showId: Long, seasonNumber: Long, episodeCount: Long = 10) {
        database.seasonsQueries.upsert(
            id = Id(showId * 100 + seasonNumber),
            show_trakt_id = Id(showId),
            season_number = seasonNumber,
            episode_count = episodeCount,
            title = "Season $seasonNumber",
            overview = null,
            image_url = null,
        )
    }

    private fun insertEpisode(
        showId: Long,
        seasonId: Long,
        episodeNumber: Long,
        title: String,
    ) {
        database.episodesQueries.upsert(
            id = Id(showId * 1000 + episodeNumber),
            season_id = Id(seasonId),
            show_trakt_id = Id(showId),
            title = title,
            overview = "Overview for $title",
            ratings = 8.0,
            vote_count = 100,
            runtime = 45,
            episode_number = episodeNumber,
            image_url = null,
            trakt_id = showId * 1000 + episodeNumber,
            first_aired = NOW - 86_400_000L,
        )
    }

    private fun insertWatchedEpisode(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        database.watchedEpisodesQueries.upsert(
            show_trakt_id = Id(showId),
            episode_id = Id(episodeId),
            season_number = seasonNumber,
            episode_number = episodeNumber,
            watched_at = NOW,
            pending_action = "UPLOAD",
        )
    }

    private companion object {
        private val NOW = LocalDate(2025, 6, 15).toEpochMillis()
    }
}

private class FakeRemoteDataSource : TraktShowsRemoteDataSource {
    var watchedProgressResponse: ApiResponse<TraktWatchedProgressResponse> = ApiResponse.Unauthenticated

    override suspend fun getWatchedProgress(traktId: Long) = watchedProgressResponse

    override suspend fun getTrendingShows(page: Int, limit: Int, genres: String?) = notImplemented()
    override suspend fun getGenres() = notImplemented()
    override suspend fun getPopularShows(page: Int, limit: Int, genres: String?) = notImplemented()
    override suspend fun getFavoritedShows(page: Int, limit: Int, period: TimePeriod, genres: String?) = notImplemented()
    override suspend fun getMostWatchedShows(page: Int, limit: Int, period: TimePeriod, genres: String?) = notImplemented()
    override suspend fun getRelatedShows(traktId: Long, page: Int, limit: Int) = notImplemented()
    override suspend fun getShowDetails(traktId: Long) = notImplemented()
    override suspend fun getShowSeasons(traktId: Long) = notImplemented()
    override suspend fun getShowSeasonEpisodes(traktId: Long, seasonNumber: Int) = notImplemented()
    override suspend fun getSeasonsWithEpisodes(traktId: Long) = notImplemented()
    override suspend fun getShowByTmdbId(tmdbId: Long) = notImplemented()
    override suspend fun searchShows(query: String, page: Int, limit: Int) = notImplemented()
    override suspend fun getShowPeople(traktId: Long) = notImplemented()
    override suspend fun getShowVideos(traktId: Long) = notImplemented()

    private fun notImplemented(): Nothing = throw NotImplementedError("Not used in test")
}
