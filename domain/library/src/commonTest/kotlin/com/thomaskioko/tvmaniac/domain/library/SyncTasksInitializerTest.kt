package com.thomaskioko.tvmaniac.domain.library

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.connectivity.testing.FakeInternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.testing.FakeBackgroundTaskScheduler
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.showdetails.ShowMetadataSyncHelper
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.syncactivity.SyncActivityInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class SyncTasksInitializerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val collectScope = CoroutineScope(testDispatcher + Job())
    private val scheduler = FakeBackgroundTaskScheduler()
    private val datastoreRepository = FakeDatastoreRepository()
    private val accountManager = FakeAccountManager()
    private val internetConnectionChecker = FakeInternetConnectionChecker()

    private val showDetailsRepository = FakeShowDetailsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val followedShowsRepository = FakeFollowedShowsRepository()
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()

    private val syncLibraryInteractor = SyncLibraryInteractor(
        accountManager = accountManager,
        libraryRepository = FakeLibraryRepository(),
        followedShowsRepository = followedShowsRepository,
        syncActivityInteractor = SyncActivityInteractor(
            traktActivityRepository = FakeTraktActivityRepository(),
            dispatchers = dispatchers,
        ),
        syncShowMetadataInteractor = SyncShowMetadataInteractor(
            showDetailsRepository = showDetailsRepository,
            seasonDetailsRepository = seasonDetailsRepository,
            watchProviderRepository = FakeWatchProviderRepository(),
            dispatchers = dispatchers,
        ),
        showMetadataSyncHelper = ShowMetadataSyncHelper(episodeRepository),
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        showDetailsRepository = showDetailsRepository,
        syncRepository = FakeActivitySyncRepository(),
        datastoreRepository = FakeDatastoreRepository(),
        dateTimeProvider = FakeDateTimeProvider(),
        dispatchers = dispatchers,
        syncObserver = FakeSyncObserver(),
        logger = FakeLogger(),
    )

    @AfterTest
    fun tearDown() {
        collectScope.cancel()
    }

    private fun buildInitializer() = SyncTasksInitializer(
        scheduler = scheduler,
        internetConnectionChecker = internetConnectionChecker,
        logger = FakeLogger(),
        coroutineScope = collectScope,
        syncLibraryInteractor = lazyOf(syncLibraryInteractor),
        datastoreRepo = lazyOf(datastoreRepository),
        accountManagerLazy = lazyOf(accountManager),
    )

    private fun TestScope.triggerReconnect() {
        internetConnectionChecker.setConnected(false)
        internetConnectionChecker.setConnected(true)
        runCurrent()
    }

    @Test
    fun `should run the library sync non-forced given a reconnect while connected and sync enabled`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        datastoreRepository.setBackgroundSyncEnabled(true)
        watchedEpisodeSyncRepository.setWatchedShowsMissingGenres(listOf(11L))

        buildInitializer().init()
        runCurrent()

        triggerReconnect()

        showDetailsRepository.fetchInvocations().map { it.id } shouldContainAll listOf(11L)
    }

    @Test
    fun `should keep collecting reconnects given a reconnect sync throws`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        datastoreRepository.setBackgroundSyncEnabled(true)
        watchedEpisodeSyncRepository.setWatchedShowsMissingGenres(listOf(11L))
        watchedEpisodeSyncRepository.setSyncAllError(IllegalStateException("sync failed"))

        buildInitializer().init()
        runCurrent()

        triggerReconnect()
        watchedEpisodeSyncRepository.setSyncAllError(null)
        triggerReconnect()

        showDetailsRepository.fetchInvocations().map { it.id } shouldContainAll listOf(11L)
    }

    @Test
    fun `should run nothing given no account is connected`() = runTest(testDispatcher) {
        datastoreRepository.setBackgroundSyncEnabled(true)
        watchedEpisodeSyncRepository.setWatchedShowsMissingGenres(listOf(11L))

        buildInitializer().init()
        runCurrent()

        triggerReconnect()

        showDetailsRepository.fetchInvocations().shouldBeEmpty()
    }

    @Test
    fun `should run nothing given sync and update is off`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        datastoreRepository.setBackgroundSyncEnabled(false)
        watchedEpisodeSyncRepository.setWatchedShowsMissingGenres(listOf(11L))

        buildInitializer().init()
        runCurrent()

        triggerReconnect()

        showDetailsRepository.fetchInvocations().shouldBeEmpty()
    }

    @Test
    fun `should schedule the periodic worker given connected and sync enabled`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        datastoreRepository.setBackgroundSyncEnabled(true)

        buildInitializer().init()
        runCurrent()

        scheduler.getScheduledRequests().single().id shouldBe LibrarySyncWorker.WORKER_NAME
    }

    @Test
    fun `should cancel the periodic worker given no account is connected`() = runTest(testDispatcher) {
        datastoreRepository.setBackgroundSyncEnabled(true)

        buildInitializer().init()
        runCurrent()

        scheduler.getCancelledIds() shouldBe listOf(LibrarySyncWorker.WORKER_NAME)
    }
}
