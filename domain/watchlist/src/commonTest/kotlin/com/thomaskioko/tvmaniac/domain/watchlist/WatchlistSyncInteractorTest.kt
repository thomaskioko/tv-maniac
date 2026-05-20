package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingDao
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingRepository
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
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
    private val continueWatchingRepository = FakeContinueWatchingRepository()
    private val continueWatchingDao = FakeContinueWatchingDao()
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
        continueWatchingDao = continueWatchingDao,
        syncWatchedShowInteractor = syncWatchedShowInteractor,
        dispatchers = dispatchers,
    )

    private val syncObserver = FakeSyncObserver()

    private val interactor = WatchlistSyncInteractor(
        traktActivityRepository = activityRepository,
        continueWatchingRepository = continueWatchingRepository,
        fetchMissingShowsInteractor = fetchMissingShowsInteractor,
        syncObserver = syncObserver,
        dispatchers = dispatchers,
    )

    @Test
    fun `should fetch activities then sync watched shows`() = runTest(testDispatcher) {
        interactor.executeSync(WatchlistSyncInteractor.Param(forceRefresh = false))

        continueWatchingRepository.syncInvocations() shouldBe listOf(false)
    }

    @Test
    fun `should propagate force refresh to watched shows sync`() = runTest(testDispatcher) {
        interactor.executeSync(WatchlistSyncInteractor.Param(forceRefresh = true))

        continueWatchingRepository.syncInvocations() shouldBe listOf(true)
    }

    @Test
    fun `should fetch missing show details after sync`() = runTest(testDispatcher) {
        continueWatchingDao.setTraktIdsMissingShowDetails(listOf(42L, 99L))

        interactor.executeSync(WatchlistSyncInteractor.Param(forceRefresh = false))

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(42L, 99L)
    }
}
