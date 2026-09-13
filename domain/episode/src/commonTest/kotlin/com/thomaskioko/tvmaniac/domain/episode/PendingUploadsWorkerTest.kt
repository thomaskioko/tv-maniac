package com.thomaskioko.tvmaniac.domain.episode

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.lists.testing.FakeListRepository
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class PendingUploadsWorkerTest {

    private val syncRepository = FakeWatchedEpisodeSyncRepository()
    private val libraryRepository = FakeLibraryRepository()
    private val listRepository = FakeListRepository()
    private val userRepository = FakeUserRepository()
    private val accountManager = FakeAccountManager()
    private val syncObserver = FakeSyncObserver()
    private val logger = FakeLogger()
    private var providerFeatures = FakeProviderFeatures(supportsLists = true)

    private val interactor = SyncPendingUploadsInteractor(
        syncRepository = syncRepository,
        libraryRepository = libraryRepository,
        listRepository = listRepository,
        userRepository = userRepository,
        activeProviderFeatures = { providerFeatures },
        accountManager = accountManager,
        logger = logger,
    )

    private val worker = PendingUploadsWorker(
        syncPendingUploadsInteractor = lazy { interactor },
        syncObserver = syncObserver,
        logger = logger,
    )

    @Test
    fun `should return Success when user is logged in and sync completes`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        syncRepository.setPendingEpisodesError(null)

        val result = worker.doWork()

        result.shouldBeInstanceOf<WorkerResult.Success>()
    }

    @Test
    fun `should return Success without syncing when user is logged out`() = runTest {
        accountManager.setActiveProvider(null)
        syncRepository.setPendingEpisodesError(RuntimeException("should never run"))

        val result = worker.doWork()

        result.shouldBeInstanceOf<WorkerResult.Success>()
        libraryRepository.syncPendingFollowedShowsInvocations() shouldBe 0
    }

    @Test
    fun `should flush pending followed shows when user is logged in`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)

        worker.doWork().shouldBeInstanceOf<WorkerResult.Success>()

        libraryRepository.syncPendingFollowedShowsInvocations() shouldBe 1
    }

    @Test
    fun `should return Retry when syncPendingEpisodes throws`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        val cause = RuntimeException("network down")
        syncRepository.setPendingEpisodesError(cause)

        val result = worker.doWork()

        result.shouldBeInstanceOf<WorkerResult.Retry>()
        result.message shouldBe "network down"
        result.cause shouldBe null
    }

    @Test
    fun `should not log the failure itself given syncPendingEpisodes throws`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        syncRepository.setPendingEpisodesError(RuntimeException("network down"))

        worker.doWork()

        logger.recordedErrors.size shouldBe 0
        logger.breadcrumbs.size shouldBe 0
    }

    @Test
    fun `should retry once and succeed when error cleared between attempts`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)

        syncRepository.setPendingEpisodesError(RuntimeException("flaky"))
        worker.doWork().shouldBeInstanceOf<WorkerResult.Retry>()

        syncRepository.setPendingEpisodesError(null)
        worker.doWork().shouldBeInstanceOf<WorkerResult.Success>()
    }

    @Test
    fun `should sync pending lists given trakt provider supports lists`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        providerFeatures = FakeProviderFeatures(supportsLists = true)

        worker.doWork().shouldBeInstanceOf<WorkerResult.Success>()

        listRepository.syncPendingListsCalls() shouldBe listOf("test-user")
    }

    @Test
    fun `should skip pending lists sync given provider does not support lists`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.SIMKL)
        providerFeatures = FakeProviderFeatures(supportsLists = false)

        worker.doWork().shouldBeInstanceOf<WorkerResult.Success>()

        listRepository.syncPendingListsCalls().shouldBeEmpty()
    }

    @Test
    fun `should skip pending lists sync given no current user`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        providerFeatures = FakeProviderFeatures(supportsLists = true)
        userRepository.setUserProfile(null)

        worker.doWork().shouldBeInstanceOf<WorkerResult.Success>()

        listRepository.syncPendingListsCalls().shouldBeEmpty()
    }

    @Test
    fun `should log BackgroundSyncFailed given syncPendingEpisodes throws`() = runTest {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        val cause = RuntimeException("rate limit 429")
        syncRepository.setPendingEpisodesError(cause)

        syncObserver.errors.test {
            worker.doWork().shouldBeInstanceOf<WorkerResult.Retry>()
            val event = awaitItem()
            event.shouldBeInstanceOf<SyncError.BackgroundSyncFailed>()
            event.operationId shouldBe PendingUploadsWorker.WORKER_NAME
            event.cause shouldBe cause
        }
    }
}
