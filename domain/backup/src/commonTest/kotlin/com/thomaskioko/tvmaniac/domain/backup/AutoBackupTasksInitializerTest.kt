package com.thomaskioko.tvmaniac.domain.backup

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.connectivity.testing.FakeInternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.testing.FakeBackgroundTaskScheduler
import com.thomaskioko.tvmaniac.data.backup.testing.FakeBackupDestination
import com.thomaskioko.tvmaniac.data.backup.testing.FakeBackupRepository
import com.thomaskioko.tvmaniac.data.backup.testing.FakeShowRefillReporter
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.datastore.api.AutoBackupInterval
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.shows.testing.FakeShowTraktIdResolver
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test

internal class AutoBackupTasksInitializerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val initializerScope = CoroutineScope(testDispatcher + Job())
    private val scheduler = FakeBackgroundTaskScheduler()
    private val datastoreRepository = FakeDatastoreRepository()
    private val backupDestination = FakeBackupDestination()
    private val internetConnectionChecker = FakeInternetConnectionChecker()
    private val backupRepository = FakeBackupRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val syncRestoredShowsInteractor = SyncRestoredShowsInteractor(
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
        logger = FakeLogger(),
    )

    @AfterTest
    fun tearDown() {
        initializerScope.cancel()
    }

    private fun startInitializer() {
        AutoBackupTasksInitializer(
            scheduler = scheduler,
            backupDestination = lazyOf(backupDestination),
            datastoreRepository = lazyOf(datastoreRepository),
            internetConnectionChecker = internetConnectionChecker,
            syncRestoredShowsInteractor = lazyOf(syncRestoredShowsInteractor),
            logger = FakeLogger(),
            coroutineScope = initializerScope,
        ).init()
    }

    private fun TestScope.reconnect() {
        internetConnectionChecker.setConnected(false)
        internetConnectionChecker.setConnected(true)
        testScheduler.runCurrent()
    }

    @Test
    fun `should use the default folder given the platform supplies one`() = runTest(testDispatcher) {
        backupDestination.setDefaultFolder(DEFAULT_FOLDER)

        startInitializer()
        testScheduler.advanceUntilIdle()

        datastoreRepository.getBackupFolder() shouldBe DEFAULT_FOLDER
    }

    @Test
    fun `should keep the chosen folder given the platform supplies a default`() = runTest(testDispatcher) {
        backupDestination.setDefaultFolder(DEFAULT_FOLDER)
        datastoreRepository.saveBackupFolder(LOCATION)

        startInitializer()
        testScheduler.advanceUntilIdle()

        datastoreRepository.getBackupFolder() shouldBe LOCATION
    }

    @Test
    fun `should keep no folder given the platform supplies no default`() = runTest(testDispatcher) {
        startInitializer()
        testScheduler.advanceUntilIdle()

        datastoreRepository.getBackupFolder() shouldBe null
    }

    @Test
    fun `should schedule the backup given it is on and a location is chosen`() = runTest(testDispatcher) {
        datastoreRepository.setAutoBackupEnabled(true)
        datastoreRepository.saveBackupFolder(LOCATION)

        startInitializer()
        testScheduler.advanceUntilIdle()

        scheduler.getScheduledRequests().single().id shouldBe AutoBackupWorker.WORKER_NAME
    }

    @Test
    fun `should schedule on the chosen interval given one is set`() = runTest(testDispatcher) {
        datastoreRepository.setAutoBackupEnabled(true)
        datastoreRepository.saveBackupFolder(LOCATION)
        datastoreRepository.saveAutoBackupInterval(AutoBackupInterval.DAILY)

        startInitializer()
        testScheduler.advanceUntilIdle()

        scheduler.getScheduledRequests().single().intervalMs shouldBe ONE_DAY_MS
    }

    @Test
    fun `should schedule weekly given no interval has been chosen`() = runTest(testDispatcher) {
        datastoreRepository.setAutoBackupEnabled(true)
        datastoreRepository.saveBackupFolder(LOCATION)

        startInitializer()
        testScheduler.advanceUntilIdle()

        scheduler.getScheduledRequests().single().intervalMs shouldBe SEVEN_DAYS_MS
    }

    @Test
    fun `should reschedule given the interval changes`() = runTest(testDispatcher) {
        datastoreRepository.setAutoBackupEnabled(true)
        datastoreRepository.saveBackupFolder(LOCATION)

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
        datastoreRepository.saveBackupFolder(LOCATION)

        startInitializer()
        testScheduler.advanceUntilIdle()

        scheduler.getScheduledRequests().shouldBeEmpty()
    }

    @Test
    fun `should cancel the backup given it is switched off`() = runTest(testDispatcher) {
        datastoreRepository.setAutoBackupEnabled(true)
        datastoreRepository.saveBackupFolder(LOCATION)

        startInitializer()
        testScheduler.advanceUntilIdle()

        datastoreRepository.setAutoBackupEnabled(false)
        testScheduler.advanceUntilIdle()

        scheduler.getCancelledIds() shouldBe listOf(AutoBackupWorker.WORKER_NAME)
    }

    @Test
    fun `should cancel the backup given the location is forgotten`() = runTest(testDispatcher) {
        datastoreRepository.setAutoBackupEnabled(true)
        datastoreRepository.saveBackupFolder(LOCATION)

        startInitializer()
        testScheduler.advanceUntilIdle()

        datastoreRepository.saveBackupFolder(null)
        testScheduler.advanceUntilIdle()

        scheduler.getCancelledIds() shouldBe listOf(AutoBackupWorker.WORKER_NAME)
    }

    @Test
    fun `should run the metadata refill given a reconnect`() = runTest(testDispatcher) {
        backupRepository.setShowsNeedingMetadata(listOf(SHOW_ID))

        startInitializer()
        testScheduler.advanceUntilIdle()
        reconnect()

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(SHOW_ID)
    }

    @Test
    fun `should run the metadata refill on reconnect given Sync and Update is off`() = runTest(testDispatcher) {
        datastoreRepository.setBackgroundSyncEnabled(false)
        backupRepository.setShowsNeedingMetadata(listOf(SHOW_ID))

        startInitializer()
        testScheduler.advanceUntilIdle()
        reconnect()

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(SHOW_ID)
    }

    @Test
    fun `should make no request on reconnect given no restored show needs metadata`() = runTest(testDispatcher) {
        startInitializer()
        testScheduler.advanceUntilIdle()
        reconnect()

        showDetailsRepository.fetchInvocations().shouldBeEmpty()
    }

    @Test
    fun `should keep collecting reconnects given a show refill fails`() = runTest(testDispatcher) {
        backupRepository.setShowsNeedingMetadata(listOf(SHOW_ID))
        showDetailsRepository.setFetchError(RuntimeException("Boom"))

        startInitializer()
        testScheduler.advanceUntilIdle()
        reconnect()

        showDetailsRepository.setFetchError(null)
        reconnect()

        showDetailsRepository.fetchInvocations().map { it.id } shouldBe listOf(SHOW_ID, SHOW_ID)
    }

    private companion object {
        private const val LOCATION = "content://downloads/tree"
        private const val DEFAULT_FOLDER = "/Documents"
        private const val SHOW_ID = 7L
        private const val ONE_DAY_MS = 24L * 60 * 60 * 1000
        private const val SEVEN_DAYS_MS = 7 * ONE_DAY_MS
        private const val THIRTY_DAYS_MS = 30 * ONE_DAY_MS
    }
}
