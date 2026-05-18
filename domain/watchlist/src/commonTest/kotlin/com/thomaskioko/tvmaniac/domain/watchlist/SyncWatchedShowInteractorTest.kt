package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SyncWatchedShowInteractorTest {

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

    private val interactor = SyncWatchedShowInteractor(
        showDetailsRepository = showDetailsRepository,
        seasonDetailsRepository = seasonDetailsRepository,
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        dispatchers = dispatchers,
    )

    @Test
    fun `should invoke show then season then episode watches in order`() = runTest(testDispatcher) {
        interactor.executeSync(SyncWatchedShowInteractor.Param(traktId = 1388L))

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(1388L)
        seasonDetailsRepository.getSyncedShowIds() shouldBe listOf(1388L)
        watchedEpisodeSyncRepository.getSyncedShowIds() shouldBe listOf(1388L)
    }

    @Test
    fun `should propagate force refresh to all three repositories`() = runTest(testDispatcher) {
        interactor.executeSync(
            SyncWatchedShowInteractor.Param(traktId = 1388L, forceRefresh = true),
        )

        showDetailsRepository.fetchInvocations().single().forceRefresh shouldBe true
        watchedEpisodeSyncRepository.wasForceRefreshUsed() shouldBe true
    }
}
