package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.testing.FakeBackgroundTaskScheduler
import com.thomaskioko.tvmaniac.datastore.api.AutoBackupInterval
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test

internal class AutoBackupTasksInitializerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val initializerScope = CoroutineScope(testDispatcher + Job())
    private val scheduler = FakeBackgroundTaskScheduler()
    private val datastoreRepository = FakeDatastoreRepository()

    @AfterTest
    fun tearDown() {
        initializerScope.cancel()
    }

    private fun startInitializer() {
        AutoBackupTasksInitializer(
            scheduler = scheduler,
            datastoreRepository = lazyOf(datastoreRepository),
            logger = FakeLogger(),
            coroutineScope = initializerScope,
        ).init()
    }

    @Test
    fun `should schedule the backup given it is on and a location is chosen`() = runTest(testDispatcher) {
        datastoreRepository.setAutoBackupEnabled(true)
        datastoreRepository.saveAutoBackupLocation(LOCATION)

        startInitializer()
        testScheduler.advanceUntilIdle()

        scheduler.getScheduledRequests().single().id shouldBe AutoBackupWorker.WORKER_NAME
    }

    @Test
    fun `should schedule on the chosen interval given one is set`() = runTest(testDispatcher) {
        datastoreRepository.setAutoBackupEnabled(true)
        datastoreRepository.saveAutoBackupLocation(LOCATION)
        datastoreRepository.saveAutoBackupInterval(AutoBackupInterval.DAILY)

        startInitializer()
        testScheduler.advanceUntilIdle()

        scheduler.getScheduledRequests().single().intervalMs shouldBe ONE_DAY_MS
    }

    @Test
    fun `should schedule weekly given no interval has been chosen`() = runTest(testDispatcher) {
        datastoreRepository.setAutoBackupEnabled(true)
        datastoreRepository.saveAutoBackupLocation(LOCATION)

        startInitializer()
        testScheduler.advanceUntilIdle()

        scheduler.getScheduledRequests().single().intervalMs shouldBe SEVEN_DAYS_MS
    }

    @Test
    fun `should reschedule given the interval changes`() = runTest(testDispatcher) {
        datastoreRepository.setAutoBackupEnabled(true)
        datastoreRepository.saveAutoBackupLocation(LOCATION)

        startInitializer()
        testScheduler.advanceUntilIdle()

        datastoreRepository.saveAutoBackupInterval(AutoBackupInterval.MONTHLY)
        testScheduler.advanceUntilIdle()

        scheduler.getScheduledRequests().last().intervalMs shouldBe THIRTY_DAYS_MS
    }

    @Test
    fun `should schedule nothing given it is on but no location is chosen`() = runTest(testDispatcher) {
        datastoreRepository.setAutoBackupEnabled(true)

        startInitializer()
        testScheduler.advanceUntilIdle()

        scheduler.getScheduledRequests().shouldBeEmpty()
        scheduler.getCancelledIds() shouldBe listOf(AutoBackupWorker.WORKER_NAME)
    }

    @Test
    fun `should schedule nothing given a location is chosen but it is off`() = runTest(testDispatcher) {
        datastoreRepository.saveAutoBackupLocation(LOCATION)

        startInitializer()
        testScheduler.advanceUntilIdle()

        scheduler.getScheduledRequests().shouldBeEmpty()
    }

    @Test
    fun `should cancel the backup given it is switched off`() = runTest(testDispatcher) {
        datastoreRepository.setAutoBackupEnabled(true)
        datastoreRepository.saveAutoBackupLocation(LOCATION)

        startInitializer()
        testScheduler.advanceUntilIdle()

        datastoreRepository.setAutoBackupEnabled(false)
        testScheduler.advanceUntilIdle()

        scheduler.getCancelledIds() shouldBe listOf(AutoBackupWorker.WORKER_NAME)
    }

    @Test
    fun `should cancel the backup given the location is forgotten`() = runTest(testDispatcher) {
        datastoreRepository.setAutoBackupEnabled(true)
        datastoreRepository.saveAutoBackupLocation(LOCATION)

        startInitializer()
        testScheduler.advanceUntilIdle()

        datastoreRepository.saveAutoBackupLocation(null)
        testScheduler.advanceUntilIdle()

        scheduler.getCancelledIds() shouldBe listOf(AutoBackupWorker.WORKER_NAME)
    }

    private companion object {
        private const val LOCATION = "content://downloads/backup.json"
        private const val ONE_DAY_MS = 24L * 60 * 60 * 1000
        private const val SEVEN_DAYS_MS = 7 * ONE_DAY_MS
        private const val THIRTY_DAYS_MS = 30 * ONE_DAY_MS
    }
}
