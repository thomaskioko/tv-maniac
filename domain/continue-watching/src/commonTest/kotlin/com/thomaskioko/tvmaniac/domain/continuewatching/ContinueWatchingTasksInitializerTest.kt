package com.thomaskioko.tvmaniac.domain.continuewatching

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingRepository
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.connectivity.testing.FakeInternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.testing.FakeBackgroundTaskScheduler
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.data.watchproviders.testing.FakeWatchProviderRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.episode.PendingUploadsWorker
import com.thomaskioko.tvmaniac.domain.episode.SyncPendingUploadsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowMetadataSyncHelper
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.syncactivity.SyncActivityInteractor
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.lists.testing.FakeListRepository
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
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
internal class ContinueWatchingTasksInitializerTest {

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

    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val continueWatchingRepository = FakeContinueWatchingRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val requestManagerRepository = FakeRequestManagerRepository(initialRequestValid = false)

    private val syncContinueWatchingInteractor = SyncContinueWatchingInteractor(
        accountManager = accountManager,
        syncActivityInteractor = SyncActivityInteractor(
            traktActivityRepository = FakeTraktActivityRepository(),
            dispatchers = dispatchers,
        ),
        continueWatchingRepository = continueWatchingRepository,
        syncShowMetadataInteractor = SyncShowMetadataInteractor(
            showDetailsRepository = showDetailsRepository,
            seasonDetailsRepository = seasonDetailsRepository,
            watchProviderRepository = FakeWatchProviderRepository(),
            dispatchers = dispatchers,
        ),
        showMetadataSyncHelper = ShowMetadataSyncHelper(episodeRepository),
        watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
        activeProviderFeatures = { FakeProviderFeatures(supportsContinueWatchingFetch = true) },
        requestManagerRepository = requestManagerRepository,
        dispatchers = dispatchers,
        logger = FakeLogger(),
    )

    private val pendingSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val pendingLibraryRepository = FakeLibraryRepository()
    private val listRepository = FakeListRepository()
    private val userRepository = FakeUserRepository()

    private val syncPendingUploadsInteractor = SyncPendingUploadsInteractor(
        syncRepository = pendingSyncRepository,
        libraryRepository = pendingLibraryRepository,
        listRepository = listRepository,
        userRepository = userRepository,
        activeProviderFeatures = { FakeProviderFeatures(supportsLists = true) },
        accountManager = accountManager,
        logger = FakeLogger(),
    )

    @AfterTest
    fun tearDown() {
        collectScope.cancel()
    }

    private fun buildInitializer() = ContinueWatchingTasksInitializer(
        scheduler = scheduler,
        logger = FakeLogger(),
        internetConnectionChecker = internetConnectionChecker,
        coroutineScope = collectScope,
        syncContinueWatchingInteractor = lazyOf(syncContinueWatchingInteractor),
        syncPendingUploadsInteractor = lazyOf(syncPendingUploadsInteractor),
        datastoreRepo = lazyOf(datastoreRepository),
        accountManagerLazy = lazyOf(accountManager),
    )

    private fun TestScope.triggerReconnect() {
        internetConnectionChecker.setConnected(false)
        internetConnectionChecker.setConnected(true)
        runCurrent()
    }

    @Test
    fun `should run the pending uploads push and continue watching sync given a reconnect while connected and sync enabled`() =
        runTest(testDispatcher) {
            accountManager.setActiveProvider(SyncProviderSource.TRAKT)
            datastoreRepository.setBackgroundSyncEnabled(true)

            buildInitializer().init()
            runCurrent()

            triggerReconnect()

            pendingSyncRepository.syncPendingCallCount() shouldBe 1
            pendingLibraryRepository.syncPendingFollowedShowsInvocations() shouldBe 1
            watchedEpisodeSyncRepository.syncAllInvocations() shouldBe listOf(false)
        }

    @Test
    fun `should keep collecting reconnects given a reconnect sync throws`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        datastoreRepository.setBackgroundSyncEnabled(true)
        pendingSyncRepository.setPendingEpisodesError(IllegalStateException("push failed"))

        buildInitializer().init()
        runCurrent()

        triggerReconnect()
        pendingSyncRepository.setPendingEpisodesError(null)
        triggerReconnect()

        pendingSyncRepository.syncPendingCallCount() shouldBe 2
        watchedEpisodeSyncRepository.syncAllInvocations() shouldBe listOf(false)
    }

    @Test
    fun `should run nothing given no account is connected`() = runTest(testDispatcher) {
        datastoreRepository.setBackgroundSyncEnabled(true)

        buildInitializer().init()
        runCurrent()

        triggerReconnect()

        pendingSyncRepository.syncPendingCallCount() shouldBe 0
        watchedEpisodeSyncRepository.syncAllInvocations().shouldBeEmpty()
    }

    @Test
    fun `should run nothing given sync and update is off`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        datastoreRepository.setBackgroundSyncEnabled(false)

        buildInitializer().init()
        runCurrent()

        triggerReconnect()

        pendingSyncRepository.syncPendingCallCount() shouldBe 0
        watchedEpisodeSyncRepository.syncAllInvocations().shouldBeEmpty()
    }

    @Test
    fun `should schedule the periodic workers given connected and sync enabled`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        datastoreRepository.setBackgroundSyncEnabled(true)

        buildInitializer().init()
        runCurrent()

        scheduler.getScheduledRequests().map { it.id } shouldContainAll listOf(
            ContinueWatchingSyncWorker.WORKER_NAME,
            PendingUploadsWorker.WORKER_NAME,
        )
    }

    @Test
    fun `should cancel the periodic workers given no account is connected`() = runTest(testDispatcher) {
        datastoreRepository.setBackgroundSyncEnabled(true)

        buildInitializer().init()
        runCurrent()

        scheduler.getCancelledIds() shouldContainAll listOf(
            ContinueWatchingSyncWorker.WORKER_NAME,
            PendingUploadsWorker.WORKER_NAME,
        )
    }
}
