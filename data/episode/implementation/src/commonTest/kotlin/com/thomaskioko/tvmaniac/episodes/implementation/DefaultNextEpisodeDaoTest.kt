package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.episodes.api.NextEpisodeDao
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultNextEpisodeDao
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
    private val now = LocalDate(2024, 6, 1).toEpochMillis()
    private val fakeDateTimeProvider = FakeDateTimeProvider()

    private lateinit var nextEpisodeDao: NextEpisodeDao
    private val showIdByTraktId = mutableMapOf<Long, Id<ShowId>>()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeDateTimeProvider.setCurrentTimeMillis(now)
        nextEpisodeDao = DefaultNextEpisodeDao(database, coroutineDispatcher, fakeDateTimeProvider)
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

            val show1Episode = watchlistEpisodes[1]
            show1Episode.showTraktId shouldBe 1L
            show1Episode.showName shouldBe "Test Show 1"
            show1Episode.episodeName shouldBe "Episode 2"
            show1Episode.seasonNumber shouldBe 1
            show1Episode.episodeNumber shouldBe 2
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
    fun `should exclude followed-only show given no continue-watching row`() = runTest {
        followShowOnly(showId = 1L, followedAt = watchDate)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            awaitItem().size shouldBe 0
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
    fun `should return correct watch progress given some episodes watched`() = runTest {
        followShow(showId = 1L, followedAt = watchDate)
        markEpisodeWatched(showId = 1L, episodeId = 101L, seasonNumber = 1L, episodeNumber = 1L)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 1

            val episode = episodes[0]
            episode.watchedCount shouldBe 1
            episode.totalCount shouldBe 3
        }
    }

    @Test
    fun `should return zero watched count given no episodes watched`() = runTest {
        followShow(showId = 1L, followedAt = watchDate)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 1

            val episode = episodes[0]
            episode.watchedCount shouldBe 0
            episode.totalCount shouldBe 3
        }
    }

    @Test
    fun `should update watch progress given episode marked as watched`() = runTest {
        followShow(showId = 1L, followedAt = watchDate)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val initialEpisodes = awaitItem()
            initialEpisodes[0].watchedCount shouldBe 0
            initialEpisodes[0].totalCount shouldBe 3

            markEpisodeWatched(showId = 1L, episodeId = 101L, seasonNumber = 1L, episodeNumber = 1L)

            val updatedEpisodes = awaitItem()
            updatedEpisodes[0].watchedCount shouldBe 1
            updatedEpisodes[0].totalCount shouldBe 3

            markEpisodeWatched(showId = 1L, episodeId = 102L, seasonNumber = 1L, episodeNumber = 2L)

            val finalEpisodes = awaitItem()
            finalEpisodes[0].watchedCount shouldBe 2
            finalEpisodes[0].totalCount shouldBe 3
        }
    }

    @Test
    fun `should exclude deleted watched episodes from watch count`() = runTest {
        followShow(showId = 1L, followedAt = watchDate)
        markEpisodeWatched(showId = 1L, episodeId = 101L, seasonNumber = 1L, episodeNumber = 1L)
        markEpisodeWatchedWithPendingAction(
            showId = 1L,
            episodeId = 102L,
            seasonNumber = 1L,
            episodeNumber = 2L,
            pendingAction = "DELETE",
        )

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 1

            val episode = episodes[0]
            episode.watchedCount shouldBe 1
            episode.totalCount shouldBe 3
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

    @Test
    fun `should hide caught-up show whose next episode has not aired`() = runTest {
        val realNow = kotlin.time.Clock.System.now().toEpochMilliseconds()
        fakeDateTimeProvider.setCurrentTimeMillis(realNow)
        insertShow(id = 7L, name = "Future Show", status = "Returning Series")
        insertSeason(seasonId = 71L, showId = 7L, seasonNumber = 1L, episodeCount = 2L)
        insertEpisode(
            episodeId = 701L,
            seasonId = 71L,
            showId = 7L,
            episodeNumber = 1L,
            title = "Aired",
            firstAired = realNow - 86_400_000L,
        )
        insertEpisode(
            episodeId = 702L,
            seasonId = 71L,
            showId = 7L,
            episodeNumber = 2L,
            title = "Future",
            firstAired = realNow + 7L * 86_400_000L,
        )
        followShow(showId = 7L, followedAt = watchDate)
        markEpisodeWatched(showId = 7L, episodeId = 701L, seasonNumber = 1L, episodeNumber = 1L)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            awaitItem().size shouldBe 0
        }
    }

    @Test
    fun `should show row when next episode airs at exactly current time`() = runTest {
        val realNow = (kotlin.time.Clock.System.now().toEpochMilliseconds() / 1000) * 1000
        fakeDateTimeProvider.setCurrentTimeMillis(realNow)
        insertShow(id = 8L, name = "Boundary Show", status = "Returning Series")
        insertSeason(seasonId = 81L, showId = 8L, seasonNumber = 1L, episodeCount = 2L)
        insertEpisode(
            episodeId = 801L,
            seasonId = 81L,
            showId = 8L,
            episodeNumber = 1L,
            title = "Aired",
            firstAired = realNow - 86_400_000L,
        )
        insertEpisode(
            episodeId = 802L,
            seasonId = 81L,
            showId = 8L,
            episodeNumber = 2L,
            title = "Just Aired",
            firstAired = realNow,
        )
        followShow(showId = 8L, followedAt = watchDate)
        markEpisodeWatched(showId = 8L, episodeId = 801L, seasonNumber = 1L, episodeNumber = 1L)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 1
            episodes[0].episodeNumber shouldBe 2L
        }
    }

    @Test
    fun `should show row when not caught up regardless of next episode air state`() = runTest {
        val realNow = kotlin.time.Clock.System.now().toEpochMilliseconds()
        fakeDateTimeProvider.setCurrentTimeMillis(realNow)
        insertShow(id = 9L, name = "Behind Show", status = "Returning Series")
        insertSeason(seasonId = 91L, showId = 9L, seasonNumber = 1L, episodeCount = 2L)
        insertEpisode(
            episodeId = 901L,
            seasonId = 91L,
            showId = 9L,
            episodeNumber = 1L,
            title = "Aired Unwatched",
            firstAired = realNow - 86_400_000L,
        )
        followShow(showId = 9L, followedAt = watchDate)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 1
            episodes[0].episodeNumber shouldBe 1L
            episodes[0].watchedCount shouldBe 0
            episodes[0].totalCount shouldBe 1
        }
    }

    @Test
    fun `should hide row when lowest unwatched episode has null first_aired`() = runTest {
        val realNow = kotlin.time.Clock.System.now().toEpochMilliseconds()
        fakeDateTimeProvider.setCurrentTimeMillis(realNow)
        insertShow(id = 10L, name = "TBD Show", status = "Returning Series")
        insertSeason(seasonId = 1010L, showId = 10L, seasonNumber = 1L, episodeCount = 2L)
        insertEpisode(
            episodeId = 1001L,
            seasonId = 1010L,
            showId = 10L,
            episodeNumber = 1L,
            title = "Aired",
            firstAired = realNow - 86_400_000L,
        )
        insertEpisode(
            episodeId = 1002L,
            seasonId = 1010L,
            showId = 10L,
            episodeNumber = 2L,
            title = "TBD",
            firstAired = null,
        )
        followShow(showId = 10L, followedAt = watchDate)
        markEpisodeWatched(showId = 10L, episodeId = 1001L, seasonNumber = 1L, episodeNumber = 1L)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            awaitItem().size shouldBe 0
        }
    }

    @Test
    fun `should exclude show given followed with watched episodes and not in continue watching`() = runTest {
        // The Trakt fetcher drops a show from `trakt_continue_watching` once the user catches
        // up. If the followed_shows row stays (typical, the user did not unfollow), the
        // watchlist must still hide it. The EXISTS clause uses any watched_episodes row as the
        // "user has started this show" signal so the rule does not depend on stale local counts.
        followShowOnly(showId = 1L, followedAt = watchDate)
        markEpisodeWatched(showId = 1L, episodeId = 101L, seasonNumber = 1L, episodeNumber = 1L)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            awaitItem().size shouldBe 0
        }
    }

    @Test
    fun `should include show given followed with watched episodes and still in continue watching`() = runTest {
        // First branch of the WHERE clause: a row in trakt_continue_watching always keeps the
        // show on screen. The user is mid-progress according to the server.
        followShow(showId = 1L, followedAt = watchDate)
        markEpisodeWatched(showId = 1L, episodeId = 101L, seasonNumber = 1L, episodeNumber = 1L)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 1
            episodes[0].showTraktId shouldBe 1L
            episodes[0].episodeName shouldBe "Episode 2"
        }
    }

    @Test
    fun `should report 8 of 10 watched given user marked first 8 of 10 aired episodes`() = runTest {
        insertShowWithTenAiredEpisodes()
        followShow(showId = 12L, followedAt = watchDate)
        (1L..8L).forEach { episodeNumber ->
            markEpisodeWatched(
                showId = 12L,
                episodeId = 1200L + episodeNumber,
                seasonNumber = 1L,
                episodeNumber = episodeNumber,
            )
        }

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 1
            episodes[0].showTraktId shouldBe 12L
            episodes[0].episodeNumber shouldBe 9L
            episodes[0].watchedCount shouldBe 8L
            episodes[0].totalCount shouldBe 10L
        }
    }

    @Test
    fun `should report 8 of 10 watched given Trakt-synced rows with null episode_id`() = runTest {
        insertShowWithTenAiredEpisodes()
        followShow(showId = 12L, followedAt = watchDate)
        (1L..8L).forEach { episodeNumber ->
            seedTraktSyncedWatch(
                showId = 12L,
                seasonNumber = 1L,
                episodeNumber = episodeNumber,
                traktId = 9000L + episodeNumber,
            )
        }

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 1
            episodes[0].showTraktId shouldBe 12L
            episodes[0].episodeNumber shouldBe 9L
            episodes[0].watchedCount shouldBe 8L
            episodes[0].totalCount shouldBe 10L
        }
    }

    @Test
    fun `should reproduce 6 of 10 with next as 9 given SYNCED_DELETE rows linger from prior unwatch`() = runTest {
        insertShowWithTenAiredEpisodes()
        followShow(showId = 12L, followedAt = watchDate)
        (1L..8L).forEach { episodeNumber ->
            markEpisodeWatched(
                showId = 12L,
                episodeId = 1200L + episodeNumber,
                seasonNumber = 1L,
                episodeNumber = episodeNumber,
            )
        }
        markEpisodeWatchedWithPendingAction(
            showId = 12L,
            episodeId = 1205L,
            seasonNumber = 1L,
            episodeNumber = 5L,
            pendingAction = "SYNCED_DELETE",
        )
        markEpisodeWatchedWithPendingAction(
            showId = 12L,
            episodeId = 1206L,
            seasonNumber = 1L,
            episodeNumber = 6L,
            pendingAction = "SYNCED_DELETE",
        )

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 1
            episodes[0].showTraktId shouldBe 12L
            episodes[0].episodeNumber shouldBe 9L
            episodes[0].watchedCount shouldBe 6L
            episodes[0].totalCount shouldBe 10L
        }
    }

    @Test
    fun `should exclude null-aired and future-aired episodes from total count`() = runTest {
        val realNow = kotlin.time.Clock.System.now().toEpochMilliseconds()
        fakeDateTimeProvider.setCurrentTimeMillis(realNow)
        insertShow(id = 11L, name = "Mixed Show", status = "Returning Series")
        insertSeason(seasonId = 1110L, showId = 11L, seasonNumber = 1L, episodeCount = 3L)
        insertEpisode(
            episodeId = 1101L,
            seasonId = 1110L,
            showId = 11L,
            episodeNumber = 1L,
            title = "Aired",
            firstAired = realNow - 86_400_000L,
        )
        insertEpisode(
            episodeId = 1102L,
            seasonId = 1110L,
            showId = 11L,
            episodeNumber = 2L,
            title = "TBD",
            firstAired = null,
        )
        insertEpisode(
            episodeId = 1103L,
            seasonId = 1110L,
            showId = 11L,
            episodeNumber = 3L,
            title = "Future",
            firstAired = realNow + 7L * 86_400_000L,
        )
        followShow(showId = 11L, followedAt = watchDate)

        nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials = false).test {
            val episodes = awaitItem()
            episodes.size shouldBe 1
            episodes[0].episodeNumber shouldBe 1L
            episodes[0].watchedCount shouldBe 0
            episodes[0].totalCount shouldBe 1
        }
    }

    private fun followShow(showId: Long, followedAt: Long) {
        val resolvedShowId = showIdByTraktId.getValue(showId)
        database.transaction {
            database.followedShowsQueries.upsert(
                id = null,
                showId = resolvedShowId,
                tmdbId = Id(showId),
                followedAt = followedAt,
                pendingAction = "NOTHING",
            )
            database.continueWatchingQueries.upsert(
                showId = resolvedShowId,
                tmdbId = Id(showId),
                airedEpisodes = 0L,
                completedCount = 1L,
                lastWatchedAt = followedAt,
                lastUpdatedAt = followedAt,
                title = null,
                year = null,
            )
        }
    }

    private fun followShowOnly(showId: Long, followedAt: Long) {
        val resolvedShowId = showIdByTraktId.getValue(showId)
        val _ = database.followedShowsQueries.upsert(
            id = null,
            showId = resolvedShowId,
            tmdbId = Id(showId),
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
            show_id = showIdByTraktId.getValue(showId),
            episode_id = Id(episodeId),
            season_number = seasonNumber,
            episode_number = episodeNumber,
            watched_at = watchedAt,
            pending_action = "NOTHING",
        )
    }

    private fun markEpisodeWatchedWithPendingAction(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        pendingAction: String,
        watchedAt: Long = watchDate,
    ) {
        val _ = database.watchedEpisodesQueries.upsert(
            show_id = showIdByTraktId.getValue(showId),
            episode_id = Id(episodeId),
            season_number = seasonNumber,
            episode_number = episodeNumber,
            watched_at = watchedAt,
            pending_action = pendingAction,
        )
    }

    private fun insertShow(
        id: Long,
        name: String,
        overview: String = "Overview for $name",
        status: String = "Returning Series",
    ) {
        val _ = database.tvShowQueries.upsert(
            trakt_id = Id(id),
            tmdb_id = Id(id),
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
        showIdByTraktId[id] = showIdForTraktId(id)
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
            show_id = showIdByTraktId.getValue(showId),
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
        firstAired: Long? = LocalDate(2023, 1, 1).toEpochMillis(),
    ) {
        val _ = database.episodesQueries.upsert(
            id = Id(episodeId),
            season_id = Id(seasonId),
            show_id = showIdByTraktId.getValue(showId),
            title = title,
            overview = "Overview for $title",
            episode_number = episodeNumber,
            runtime = 45L,
            image_url = "/ep$episodeId.jpg",
            ratings = 8.0,
            vote_count = 100L,
            trakt_id = null,
            first_aired = firstAired,
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
            show_id = showIdByTraktId.getValue(1L),
            season_count = 1,
            episode_count = 3,
            status = "Returning Series",
        )
        val _ = database.showMetadataQueries.upsert(
            show_id = showIdByTraktId.getValue(2L),
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

    private fun insertShowWithTenAiredEpisodes() {
        insertShow(id = 12L, name = "Severance", status = "Returning Series")
        insertSeason(seasonId = 1210L, showId = 12L, seasonNumber = 1L, episodeCount = 10L)
        (1L..10L).forEach { episodeNumber ->
            insertEpisode(
                episodeId = 1200L + episodeNumber,
                seasonId = 1210L,
                showId = 12L,
                episodeNumber = episodeNumber,
                title = "Episode $episodeNumber",
            )
        }
    }

    private fun seedTraktSyncedWatch(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        traktId: Long,
        watchedAt: Long = watchDate,
    ) {
        database.watchedEpisodesQueries.upsertFromTrakt(
            show_id = showIdByTraktId.getValue(showId),
            episode_id = null,
            season_number = seasonNumber,
            episode_number = episodeNumber,
            watched_at = watchedAt,
            trakt_id = traktId,
            synced_at = watchedAt,
            pending_action = "NOTHING",
        )
    }

    private fun insertShow4WithSpecials() {
        insertShow(id = 4, name = "Show with Specials")

        insertSeason(seasonId = 40L, showId = 4L, seasonNumber = 0L, title = "Specials", episodeCount = 1L)
        insertEpisode(episodeId = 400L, seasonId = 40L, showId = 4L, episodeNumber = 1L, title = "Christmas Special")

        insertSeason(seasonId = 41L, showId = 4L, seasonNumber = 1L, episodeCount = 1L)
        insertEpisode(episodeId = 401L, seasonId = 41L, showId = 4L, episodeNumber = 1L, title = "Regular Episode 1")
    }
}
