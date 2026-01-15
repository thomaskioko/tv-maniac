package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.episodes.api.NextEpisodeDao
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

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultNextEpisodeDaoTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private var watchDate = LocalDate(2024, 1, 1)
        .atStartOfDayIn(TimeZone.UTC).epochSeconds

    private lateinit var nextEpisodeDao: NextEpisodeDao

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        nextEpisodeDao = DefaultNextEpisodeDao(database, coroutineDispatcher)
        insertTestData()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should observe next episodes for watchlist`() = runTest {
        followShow(showId = 1L, followedAt = watchDate)
        followShow(showId = 2L, followedAt = watchDate + 1000)
        markEpisodeWatched(showId = 1L, episodeId = 101L, seasonNumber = 1L, episodeNumber = 1L)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val watchlistEpisodes = awaitItem()
            watchlistEpisodes.size shouldBe 2

            val show2Episode = watchlistEpisodes[0]
            show2Episode.showTraktId shouldBe 2L
            show2Episode.showName shouldBe "Test Show 2"
            show2Episode.episodeName shouldBe "Show 2 Episode 1"
            show2Episode.seasonNumber shouldBe 1
            show2Episode.episodeNumber shouldBe 1
            show2Episode.followedAt.shouldNotBeNull()

            val show1Episode = watchlistEpisodes[1]
            show1Episode.showTraktId shouldBe 1L
            show1Episode.showName shouldBe "Test Show 1"
            show1Episode.episodeName shouldBe "Episode 2"
            show1Episode.seasonNumber shouldBe 1
            show1Episode.episodeNumber shouldBe 2
            show1Episode.followedAt.shouldNotBeNull()
        }
    }

    @Test
    fun `should return first episode given show with no watched episodes`() = runTest {
        followShow(showId = 1L, followedAt = watchDate)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 1

            val nextEpisode = episodes[0]
            nextEpisode.showTraktId shouldBe 1L
            nextEpisode.showName shouldBe "Test Show 1"
            nextEpisode.episodeName shouldBe "Episode 1"
            nextEpisode.seasonNumber shouldBe 1
            nextEpisode.episodeNumber shouldBe 1
        }
    }

    @Test
    fun `should return next unwatched episode given some episodes watched`() = runTest {
        followShow(showId = 1L, followedAt = watchDate)
        markEpisodeWatched(showId = 1L, episodeId = 101L, seasonNumber = 1L, episodeNumber = 1L)
        markEpisodeWatched(showId = 1L, episodeId = 102L, seasonNumber = 1L, episodeNumber = 2L)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 1

            val nextEpisode = episodes[0]
            nextEpisode.episodeName shouldBe "Episode 3"
            nextEpisode.seasonNumber shouldBe 1
            nextEpisode.episodeNumber shouldBe 3
        }
    }

    @Test
    fun `should exclude show given all episodes watched`() = runTest {
        followShow(showId = 1L, followedAt = watchDate)
        followShow(showId = 2L, followedAt = watchDate + 1000)
        markEpisodeWatched(showId = 1L, episodeId = 101L, seasonNumber = 1L, episodeNumber = 1L)
        markEpisodeWatched(showId = 1L, episodeId = 102L, seasonNumber = 1L, episodeNumber = 2L)
        markEpisodeWatched(showId = 1L, episodeId = 103L, seasonNumber = 1L, episodeNumber = 3L)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 1
            episodes[0].showTraktId shouldBe 2L
            episodes[0].showName shouldBe "Test Show 2"
        }
    }

    @Test
    fun `should return first episode of next season given all season episodes watched`() = runTest {
        insertShow3WithMultipleSeasons()
        followShow(showId = 3L, followedAt = watchDate)
        markEpisodeWatched(showId = 3L, episodeId = 301L, seasonNumber = 1L, episodeNumber = 1L)
        markEpisodeWatched(showId = 3L, episodeId = 302L, seasonNumber = 1L, episodeNumber = 2L)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 1

            val nextEpisode = episodes[0]
            nextEpisode.episodeName shouldBe "S2E1"
            nextEpisode.seasonNumber shouldBe 2
            nextEpisode.episodeNumber shouldBe 1
        }
    }

    @Test
    fun `should skip season 0 episodes given includeSpecials is false`() = runTest {
        insertShow4WithSpecials()
        followShow(showId = 4L, followedAt = watchDate)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 1

            val nextEpisode = episodes[0]
            nextEpisode.episodeName shouldBe "Regular Episode 1"
            nextEpisode.seasonNumber shouldBe 1
            nextEpisode.episodeNumber shouldBe 1
        }
    }

    @Test
    fun `should maintain shows in watchlist when tracking sequentially`() = runTest {
        followShow(showId = 1L, followedAt = watchDate)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes1 = awaitItem()
            episodes1.size shouldBe 1
            episodes1[0].showTraktId shouldBe 1L
            episodes1[0].showName shouldBe "Test Show 1"

            followShow(showId = 2L, followedAt = watchDate + 1000)

            val episodes2 = awaitItem()
            episodes2.size shouldBe 2

            episodes2[0].showTraktId shouldBe 2L
            episodes2[0].showName shouldBe "Test Show 2"

            episodes2[1].showTraktId shouldBe 1L
            episodes2[1].showName shouldBe "Test Show 1"
        }
    }

    @Test
    fun `should handle shows with Specials seasons correctly`() = runTest {
        insertShowWithSpecials(
            showId = 5L,
            showName = "Breaking Bad",
            specialsSeasonId = 50L,
            regularSeasonId = 51L,
            specialEpisodeId = 500L,
            regularEpisodeIds = listOf(501L to "Pilot", 502L to "Cat's in the Bag..."),
        )
        insertShowWithSpecials(
            showId = 6L,
            showName = "Game of Thrones",
            specialsSeasonId = 60L,
            regularSeasonId = 61L,
            specialEpisodeId = 600L,
            regularEpisodeIds = listOf(610L to "Winter Is Coming", 611L to "The Kingsroad"),
        )

        followShow(showId = 5L, followedAt = watchDate)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes1 = awaitItem()
            episodes1.size shouldBe 1
            episodes1[0].showTraktId shouldBe 5L
            episodes1[0].showName shouldBe "Breaking Bad"
            episodes1[0].episodeName shouldBe "Pilot"
            episodes1[0].seasonNumber shouldBe 1
            episodes1[0].episodeNumber shouldBe 1

            followShow(showId = 6L, followedAt = watchDate + 1000)

            val episodes2 = awaitItem()
            episodes2.size shouldBe 2

            episodes2[0].showTraktId shouldBe 6L
            episodes2[0].showName shouldBe "Game of Thrones"
            episodes2[0].episodeName shouldBe "Winter Is Coming"
            episodes2[0].seasonNumber shouldBe 1
            episodes2[0].episodeNumber shouldBe 1

            episodes2[1].showTraktId shouldBe 5L
            episodes2[1].showName shouldBe "Breaking Bad"
            episodes2[1].episodeName shouldBe "Pilot"
            episodes2[1].seasonNumber shouldBe 1
            episodes2[1].episodeNumber shouldBe 1
        }
    }

    @Test
    fun `should return empty list given no shows in watchlist`() = runTest {
        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 0
        }
    }

    @Test
    fun `should order by followed_at descending`() = runTest {
        followShow(showId = 1L, followedAt = watchDate)
        followShow(showId = 2L, followedAt = watchDate + 2000)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 2
            episodes[0].showTraktId shouldBe 2L
            episodes[1].showTraktId shouldBe 1L
        }
    }

    private fun followShow(showId: Long, followedAt: Long) {
        val _ = database.followedShowsQueries.upsert(
            id = null,
            traktId = Id<TraktId>(showId),
            tmdbId = Id<TmdbId>(showId),
            followedAt = followedAt,
            pendingAction = "NOTHING",
        )
    }

    private fun markEpisodeWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long = watchDate,
    ) {
        val _ = database.watchedEpisodesQueries.upsert(
            show_trakt_id = Id<TraktId>(showId),
            episode_id = Id(episodeId),
            season_number = seasonNumber,
            episode_number = episodeNumber,
            watched_at = watchedAt,
            pending_action = "NOTHING",
        )
    }

    private fun insertShow(
        id: Long,
        name: String,
        overview: String = "Overview for $name",
        status: String = "Returning Series",
    ) {
        val _ = database.tvShowQueries.upsert(
            trakt_id = Id<TraktId>(id),
            tmdb_id = Id<TmdbId>(id),
            name = name,
            overview = overview,
            language = "en",
            year = "2023-01-01",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama", "Action"),
            status = status,
            episode_numbers = null,
            season_numbers = null,
            poster_path = "/$id.jpg",
            backdrop_path = "/$id-back.jpg",
        )
    }

    private fun insertSeason(
        seasonId: Long,
        showId: Long,
        seasonNumber: Long,
        title: String = "Season $seasonNumber",
        episodeCount: Long = 2L,
    ) {
        val _ = database.seasonsQueries.upsert(
            id = Id(seasonId),
            show_trakt_id = Id<TraktId>(showId),
            season_number = seasonNumber,
            title = title,
            overview = "Overview for $title",
            episode_count = episodeCount,
            image_url = "/s$seasonNumber.jpg",
        )
    }

    private fun insertEpisode(
        episodeId: Long,
        seasonId: Long,
        showId: Long,
        episodeNumber: Long,
        title: String,
        airDate: String = "2023-01-01",
    ) {
        val _ = database.episodesQueries.upsert(
            id = Id(episodeId),
            season_id = Id(seasonId),
            show_trakt_id = Id<TraktId>(showId),
            title = title,
            overview = "Overview for $title",
            episode_number = episodeNumber,
            runtime = 45L,
            image_url = "/ep$episodeId.jpg",
            ratings = 8.0,
            vote_count = 100L,
            air_date = airDate,
            trakt_id = null,
        )
    }

    private fun insertShowWithSpecials(
        showId: Long,
        showName: String,
        specialsSeasonId: Long,
        regularSeasonId: Long,
        specialEpisodeId: Long,
        regularEpisodeIds: List<Pair<Long, String>>,
    ) {
        insertShow(id = showId, name = showName, status = "Ended")
        insertSeason(
            seasonId = specialsSeasonId,
            showId = showId,
            seasonNumber = 0L,
            title = "Specials",
            episodeCount = 1L,
        )
        insertEpisode(
            episodeId = specialEpisodeId,
            seasonId = specialsSeasonId,
            showId = showId,
            episodeNumber = 1L,
            title = "Special Episode",
        )
        insertSeason(
            seasonId = regularSeasonId,
            showId = showId,
            seasonNumber = 1L,
            episodeCount = regularEpisodeIds.size.toLong(),
        )
        regularEpisodeIds.forEachIndexed { index, (episodeId, title) ->
            insertEpisode(
                episodeId = episodeId,
                seasonId = regularSeasonId,
                showId = showId,
                episodeNumber = (index + 1).toLong(),
                title = title,
            )
        }
    }

    private fun insertTestData() {
        insertShow(id = 1, name = "Test Show 1")
        insertShow(id = 2, name = "Test Show 2", status = "Ended")

        val _ = database.showMetadataQueries.upsert(
            show_trakt_id = Id<TraktId>(1),
            season_count = 1,
            episode_count = 3,
            status = "Returning Series",
        )
        val _ = database.showMetadataQueries.upsert(
            show_trakt_id = Id<TraktId>(2),
            season_count = 1,
            episode_count = 2,
            status = "Ended",
        )

        insertSeason(seasonId = 11L, showId = 1L, seasonNumber = 1L, episodeCount = 3L)
        insertEpisode(episodeId = 101L, seasonId = 11L, showId = 1L, episodeNumber = 1L, title = "Episode 1")
        insertEpisode(episodeId = 102L, seasonId = 11L, showId = 1L, episodeNumber = 2L, title = "Episode 2")
        insertEpisode(episodeId = 103L, seasonId = 11L, showId = 1L, episodeNumber = 3L, title = "Episode 3")

        insertSeason(seasonId = 21L, showId = 2L, seasonNumber = 1L, episodeCount = 2L)
        insertEpisode(episodeId = 201L, seasonId = 21L, showId = 2L, episodeNumber = 1L, title = "Show 2 Episode 1")
        insertEpisode(episodeId = 202L, seasonId = 21L, showId = 2L, episodeNumber = 2L, title = "Show 2 Episode 2")
    }

    private fun insertShow3WithMultipleSeasons() {
        insertShow(id = 3, name = "Multi-Season Show")

        insertSeason(seasonId = 31L, showId = 3L, seasonNumber = 1L)
        insertEpisode(episodeId = 301L, seasonId = 31L, showId = 3L, episodeNumber = 1L, title = "S1E1")
        insertEpisode(episodeId = 302L, seasonId = 31L, showId = 3L, episodeNumber = 2L, title = "S1E2")

        insertSeason(seasonId = 32L, showId = 3L, seasonNumber = 2L)
        insertEpisode(episodeId = 303L, seasonId = 32L, showId = 3L, episodeNumber = 1L, title = "S2E1")
    }

    private fun insertShow4WithSpecials() {
        insertShow(id = 4, name = "Show with Specials")

        insertSeason(seasonId = 40L, showId = 4L, seasonNumber = 0L, title = "Specials", episodeCount = 1L)
        insertEpisode(episodeId = 400L, seasonId = 40L, showId = 4L, episodeNumber = 1L, title = "Christmas Special")

        insertSeason(seasonId = 41L, showId = 4L, seasonNumber = 1L, episodeCount = 1L)
        insertEpisode(episodeId = 401L, seasonId = 41L, showId = 4L, episodeNumber = 1L, title = "Regular Episode 1")
    }
}
