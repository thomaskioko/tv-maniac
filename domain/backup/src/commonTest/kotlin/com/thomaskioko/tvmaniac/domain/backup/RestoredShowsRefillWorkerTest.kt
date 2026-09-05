package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.data.backup.testing.FakeBackupRepository
import com.thomaskioko.tvmaniac.data.backup.testing.FakeShowRefillReporter
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.shows.testing.FakeShowTraktIdResolver
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RestoredShowsRefillWorkerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val backupRepository = FakeBackupRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val logger = FakeLogger()

    private val interactor = SyncRestoredShowsInteractor(
        backupRepository = backupRepository,
        syncShowMetadataInteractor = SyncShowMetadataInteractor(
            showDetailsRepository = showDetailsRepository,
            seasonDetailsRepository = FakeSeasonDetailsRepository(),
            watchProviderRepository = FakeWatchProviderRepository(),
            dispatchers = dispatchers,
        ),
        traktIdResolver = FakeShowTraktIdResolver(),
        refillReporter = FakeShowRefillReporter(),
        dispatchers = dispatchers,
        logger = logger,
    )

    private val worker = RestoredShowsRefillWorker(
        syncRestoredShowsInteractor = lazyOf(interactor),
        logger = logger,
    )

    @Test
    fun `should return success given no shows need metadata`() = runTest(testDispatcher) {
        worker.doWork() shouldBe WorkerResult.Success
    }

    @Test
    fun `should return success given the metadata refill completes`() = runTest(testDispatcher) {
        backupRepository.setShowsNeedingMetadata(listOf(7L))

        worker.doWork() shouldBe WorkerResult.Success
    }

    @Test
    fun `should let a cancellation through given the metadata fan-out is cancelled`() = runTest(testDispatcher) {
        backupRepository.setShowsNeedingMetadata(listOf(7L))
        showDetailsRepository.setFetchError(CancellationException("cancelled"))

        val result = worker.doWork()

        result shouldBe WorkerResult.Retry("Cancelled, will retry")
    }
}
