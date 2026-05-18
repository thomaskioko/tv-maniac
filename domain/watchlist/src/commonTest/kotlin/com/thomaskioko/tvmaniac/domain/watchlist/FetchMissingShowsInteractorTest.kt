package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.domain.watchlist.model.WatchlistSections
import com.thomaskioko.tvmaniac.domain.watchlist.model.WatchlistShowInfo
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FetchMissingShowsInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()

    private val syncWatchedShowInteractor = SyncWatchedShowInteractor(
        showDetailsRepository = showDetailsRepository,
        seasonDetailsRepository = seasonDetailsRepository,
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        dispatchers = dispatchers,
    )

    private val interactor = FetchMissingShowsInteractor(
        syncWatchedShowInteractor = syncWatchedShowInteractor,
        dispatchers = dispatchers,
    )

    @Test
    fun `should fetch each show with null title once`() = runTest(testDispatcher) {
        val sections = WatchlistSections(
            watchNext = listOf(showInfo(42L, title = null), showInfo(100L, title = "Loaded")),
            stale = listOf(showInfo(99L, title = null)),
        )

        interactor.executeSync(sections)

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(42L, 99L)
    }

    @Test
    fun `should skip ids already fetched on subsequent invocations`() = runTest(testDispatcher) {
        val first = WatchlistSections(
            watchNext = listOf(showInfo(42L, title = null)),
            stale = emptyList(),
        )
        val second = WatchlistSections(
            watchNext = listOf(showInfo(42L, title = null), showInfo(99L, title = null)),
            stale = emptyList(),
        )

        interactor.executeSync(first)
        interactor.executeSync(second)

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(42L, 99L)
    }

    @Test
    fun `should propagate error from downstream sync`() = runTest(testDispatcher) {
        seasonDetailsRepository.setFetchError(IllegalStateException("boom"))

        val sections = WatchlistSections(
            watchNext = listOf(showInfo(42L, title = null)),
            stale = emptyList(),
        )

        interactor.executeSync(sections)

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(42L)
    }
}

private fun showInfo(traktId: Long, title: String?): WatchlistShowInfo = WatchlistShowInfo(
    traktId = traktId,
    tmdbId = null,
    title = title,
    posterImageUrl = null,
    status = null,
    year = null,
    seasonCount = 0,
    episodeCount = 0,
    episodesWatched = 0,
    totalEpisodesTracked = 0,
    watchProgress = 0f,
    lastWatchedAt = null,
    followedAt = null,
)
