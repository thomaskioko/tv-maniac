package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.watchedshows.testing.FakeWatchedShowsDao
import com.thomaskioko.tvmaniac.watchedshows.testing.FakeWatchedShowsRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WatchlistSyncInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val activityRepository = FakeTraktActivityRepository()
    private val watchedShowsRepository = FakeWatchedShowsRepository()
    private val watchedShowsDao = FakeWatchedShowsDao()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()

    private val syncWatchedShowInteractor = SyncWatchedShowInteractor(
        showDetailsRepository = showDetailsRepository,
        seasonDetailsRepository = seasonDetailsRepository,
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        dispatchers = dispatchers,
    )

    private val fetchMissingShowsInteractor = FetchMissingShowsInteractor(
        watchedShowsDao = watchedShowsDao,
        syncWatchedShowInteractor = syncWatchedShowInteractor,
        dispatchers = dispatchers,
    )

    private val interactor = WatchlistSyncInteractor(
        traktActivityRepository = activityRepository,
        watchedShowsRepository = watchedShowsRepository,
        fetchMissingShowsInteractor = fetchMissingShowsInteractor,
        dispatchers = dispatchers,
    )

    @Test
    fun `should fetch activities then sync watched shows`() = runTest(testDispatcher) {
        interactor.executeSync(WatchlistSyncInteractor.Param(forceRefresh = false))

        watchedShowsRepository.syncInvocations() shouldBe listOf(false)
    }

    @Test
    fun `should propagate force refresh to watched shows sync`() = runTest(testDispatcher) {
        interactor.executeSync(WatchlistSyncInteractor.Param(forceRefresh = true))

        watchedShowsRepository.syncInvocations() shouldBe listOf(true)
    }

    @Test
    fun `should fetch missing show details after sync`() = runTest(testDispatcher) {
        watchedShowsDao.setTraktIdsMissingShowDetails(listOf(42L, 99L))

        interactor.executeSync(WatchlistSyncInteractor.Param(forceRefresh = false))

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(42L, 99L)
    }
}
