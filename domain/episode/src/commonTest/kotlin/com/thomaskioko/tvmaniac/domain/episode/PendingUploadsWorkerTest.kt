package com.thomaskioko.tvmaniac.domain.episode

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRepository
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class PendingUploadsWorkerTest {

    private val syncRepository = FakeWatchedEpisodeSyncRepository()
    private val libraryRepository = FakeLibraryRepository()
    private val authRepository = FakeTraktAuthRepository()
    private val syncObserver = FakeSyncObserver()
    private val logger = FakeLogger()

    private val worker = PendingUploadsWorker(
        syncRepository = lazy { syncRepository },
        libraryRepository = lazy { libraryRepository },
        traktAuthRepository = lazy { authRepository },
        syncObserver = syncObserver,
        logger = logger,
    )

    @Test
    fun `should return Success when user is logged in and sync completes`() = runTest {
        authRepository.setState(AccountAuthState.LOGGED_IN)
        syncRepository.setPendingEpisodesError(null)

        val result = worker.doWork()

        result.shouldBeInstanceOf<WorkerResult.Success>()
    }

    @Test
    fun `should return Success without syncing when user is logged out`() = runTest {
        authRepository.setState(AccountAuthState.LOGGED_OUT)
        syncRepository.setPendingEpisodesError(RuntimeException("should never run"))

        val result = worker.doWork()

        result.shouldBeInstanceOf<WorkerResult.Success>()
        libraryRepository.syncPendingFollowedShowsInvocations() shouldBe 0
    }

    @Test
    fun `should flush pending followed shows when user is logged in`() = runTest {
        authRepository.setState(AccountAuthState.LOGGED_IN)

        worker.doWork().shouldBeInstanceOf<WorkerResult.Success>()

        libraryRepository.syncPendingFollowedShowsInvocations() shouldBe 1
    }

    @Test
    fun `should return Retry when syncPendingEpisodes throws`() = runTest {
        authRepository.setState(AccountAuthState.LOGGED_IN)
        syncRepository.setPendingEpisodesError(RuntimeException("network down"))

        val result = worker.doWork()

        result.shouldBeInstanceOf<WorkerResult.Retry>()
        result.message shouldBe "network down"
    }

    @Test
    fun `should retry once and succeed when error cleared between attempts`() = runTest {
        authRepository.setState(AccountAuthState.LOGGED_IN)

        syncRepository.setPendingEpisodesError(RuntimeException("flaky"))
        worker.doWork().shouldBeInstanceOf<WorkerResult.Retry>()

        syncRepository.setPendingEpisodesError(null)
        worker.doWork().shouldBeInstanceOf<WorkerResult.Success>()
    }

    @Test
    fun `should log BackgroundSyncFailed given syncPendingEpisodes throws`() = runTest {
        authRepository.setState(AccountAuthState.LOGGED_IN)
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
