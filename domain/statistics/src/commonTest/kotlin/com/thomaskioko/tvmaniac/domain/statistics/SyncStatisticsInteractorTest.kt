package com.thomaskioko.tvmaniac.domain.statistics

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.ratings.testing.FakeRatingsRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class SyncStatisticsInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val ratingsRepository = FakeRatingsRepository()

    private val interactor = SyncStatisticsInteractor(
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        ratingsRepository = ratingsRepository,
        dispatchers = dispatchers,
    )

    @Test
    fun `should read the watches and the ratings again`() = runTest(testDispatcher) {
        interactor.executeSync(SyncStatisticsInteractor.Params(forceRefresh = true))

        watchedEpisodeSyncRepository.syncAllInvocations() shouldBe listOf(true)
        ratingsRepository.syncUserRatingsCount shouldBe 1
    }

    @Test
    fun `should let the watch sync skip its own work given no refresh was asked for`() = runTest(testDispatcher) {
        interactor.executeSync(SyncStatisticsInteractor.Params(forceRefresh = false))

        watchedEpisodeSyncRepository.syncAllInvocations() shouldBe listOf(false)
    }

    @Test
    fun `should read the ratings given the watch sync failed`() = runTest(testDispatcher) {
        watchedEpisodeSyncRepository.setSyncAllError(IllegalStateException("no connection"))

        shouldThrow<IllegalStateException> {
            interactor.executeSync(SyncStatisticsInteractor.Params(forceRefresh = true))
        }

        ratingsRepository.syncUserRatingsCount shouldBe 1
    }
}
