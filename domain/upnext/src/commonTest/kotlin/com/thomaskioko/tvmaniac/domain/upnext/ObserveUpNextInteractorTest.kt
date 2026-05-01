package com.thomaskioko.tvmaniac.domain.upnext

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.domain.upnext.model.UpNextSortOption
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ObserveUpNextInteractorTest {
    private val testDispatcher = StandardTestDispatcher()
    private val repository = FakeUpNextRepository()

    private lateinit var interactor: ObserveUpNextInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        interactor = ObserveUpNextInteractor(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should rank watched shows above unwatched shows when sorted by last watched`() = runTest {
        val watchedRecently = createEpisode(showTraktId = 1, lastWatchedAt = 1_000L, followedAt = 500L)
        val followedRecentlyButUnwatched = createEpisode(showTraktId = 2, lastWatchedAt = null, followedAt = 9_000L)
        val followedLongAgoUnwatched = createEpisode(showTraktId = 3, lastWatchedAt = null, followedAt = 100L)
        repository.setNextEpisodesForWatchlist(
            listOf(followedRecentlyButUnwatched, followedLongAgoUnwatched, watchedRecently),
        )
        repository.setUpNextSortOption(UpNextSortOption.LAST_WATCHED.name)

        interactor.flow.test {
            awaitItem().episodes.map { it.showTraktId } shouldBe listOf(1L, 2L, 3L)
        }
    }

    @Test
    fun `should sort watched shows by last watched descending`() = runTest {
        val watchedNow = createEpisode(showTraktId = 1, lastWatchedAt = 5_000L)
        val watchedEarlier = createEpisode(showTraktId = 2, lastWatchedAt = 2_000L)
        val watchedLongAgo = createEpisode(showTraktId = 3, lastWatchedAt = 1_000L)
        repository.setNextEpisodesForWatchlist(listOf(watchedEarlier, watchedLongAgo, watchedNow))
        repository.setUpNextSortOption(UpNextSortOption.LAST_WATCHED.name)

        interactor.flow.test {
            awaitItem().episodes.map { it.showTraktId } shouldBe listOf(1L, 2L, 3L)
        }
    }

    @Test
    fun `should sort unwatched shows by followed at descending`() = runTest {
        val followedRecently = createEpisode(showTraktId = 1, lastWatchedAt = null, followedAt = 5_000L)
        val followedEarlier = createEpisode(showTraktId = 2, lastWatchedAt = null, followedAt = 2_000L)
        repository.setNextEpisodesForWatchlist(listOf(followedEarlier, followedRecently))
        repository.setUpNextSortOption(UpNextSortOption.LAST_WATCHED.name)

        interactor.flow.test {
            awaitItem().episodes.map { it.showTraktId } shouldBe listOf(1L, 2L)
        }
    }

    @Test
    fun `should sort by air date descending when sort option is air date`() = runTest {
        val airedNow = createEpisode(showTraktId = 1, firstAired = 5_000L)
        val airedEarlier = createEpisode(showTraktId = 2, firstAired = 2_000L)
        repository.setNextEpisodesForWatchlist(listOf(airedEarlier, airedNow))
        repository.setUpNextSortOption(UpNextSortOption.AIR_DATE.name)

        interactor.flow.test {
            awaitItem().episodes.map { it.showTraktId } shouldBe listOf(1L, 2L)
        }
    }

    private fun createEpisode(
        showTraktId: Long,
        lastWatchedAt: Long? = null,
        followedAt: Long? = 0L,
        firstAired: Long? = null,
    ) = NextEpisodeWithShow(
        showTraktId = showTraktId,
        showTmdbId = showTraktId,
        showName = "Show $showTraktId",
        showPoster = null,
        showStatus = null,
        showYear = null,
        episodeId = showTraktId * 100,
        episodeName = "Episode",
        seasonId = 1L,
        seasonNumber = 1L,
        episodeNumber = 1L,
        runtime = null,
        stillPath = null,
        overview = null,
        followedAt = followedAt,
        firstAired = firstAired,
        lastWatchedAt = lastWatchedAt,
        seasonCount = 1,
        episodeCount = 1,
        watchedCount = 0,
        totalCount = 1,
    )
}
