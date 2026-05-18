package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
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

    private val interactor = WatchlistSyncInteractor(
        traktActivityRepository = activityRepository,
        watchedShowsRepository = watchedShowsRepository,
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
}
