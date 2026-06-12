package com.thomaskioko.tvmaniac.domain.continuewatching

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.domain.continuewatching.model.UpNextSortOption
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
    fun `should sort shows by last watched descending`() = runTest {
        val watchedNow = createEpisode(showId = 1, lastWatchedAt = 5_000L)
        val watchedEarlier = createEpisode(showId = 2, lastWatchedAt = 2_000L)
        val watchedLongAgo = createEpisode(showId = 3, lastWatchedAt = 1_000L)
        repository.setNextEpisodesForWatchlist(listOf(watchedEarlier, watchedLongAgo, watchedNow))
        repository.setUpNextSortOption(UpNextSortOption.LAST_WATCHED.name)

        interactor.flow.test {
            awaitItem().episodes.map { it.showId } shouldBe listOf(1L, 2L, 3L)
        }
    }

    @Test
    fun `should sort by air date descending when sort option is air date`() = runTest {
        val airedNow = createEpisode(showId = 1, firstAired = 5_000L)
        val airedEarlier = createEpisode(showId = 2, firstAired = 2_000L)
        repository.setNextEpisodesForWatchlist(listOf(airedEarlier, airedNow))
        repository.setUpNextSortOption(UpNextSortOption.AIR_DATE.name)

        interactor.flow.test {
            awaitItem().episodes.map { it.showId } shouldBe listOf(1L, 2L)
        }
    }

    private fun createEpisode(
        showId: Long,
        lastWatchedAt: Long? = null,
        firstAired: Long? = null,
    ) = NextEpisodeWithShow(
        showId = showId,
        showName = "Show $showId",
        showPoster = null,
        showStatus = null,
        showYear = null,
        episodeId = showId * 100,
        episodeName = "Episode",
        seasonId = 1L,
        seasonNumber = 1L,
        episodeNumber = 1L,
        runtime = null,
        stillPath = null,
        overview = null,
        firstAired = firstAired,
        lastWatchedAt = lastWatchedAt,
        seasonCount = 1,
        episodeCount = 1,
        watchedCount = 0,
        totalCount = 1,
    )
}
