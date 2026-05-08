package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class PendingUploadsWorkerTest {

    private val syncRepository = FakeWatchedEpisodeSyncRepository()
    private val authRepository = FakeTraktAuthRepository()
    private val logger = FakeLogger()

    private val worker = PendingUploadsWorker(
        syncRepository = lazy { syncRepository },
        traktAuthRepository = lazy { authRepository },
        logger = logger,
    )

    @Test
    fun `should return Success when user is logged in and sync completes`() = runTest {
        authRepository.setState(TraktAuthState.LOGGED_IN)
        syncRepository.setPendingEpisodesError(null)

        val result = worker.doWork()

        result.shouldBeInstanceOf<WorkerResult.Success>()
    }

    @Test
    fun `should return Success without syncing when user is logged out`() = runTest {
        authRepository.setState(TraktAuthState.LOGGED_OUT)
        syncRepository.setPendingEpisodesError(RuntimeException("should never run"))

        val result = worker.doWork()

        result.shouldBeInstanceOf<WorkerResult.Success>()
    }

    @Test
    fun `should return Retry when syncPendingEpisodes throws`() = runTest {
        authRepository.setState(TraktAuthState.LOGGED_IN)
        syncRepository.setPendingEpisodesError(RuntimeException("network down"))

        val result = worker.doWork()

        result.shouldBeInstanceOf<WorkerResult.Retry>()
        result.message shouldBe "network down"
    }

    @Test
    fun `should retry once and succeed when error cleared between attempts`() = runTest {
        authRepository.setState(TraktAuthState.LOGGED_IN)

        syncRepository.setPendingEpisodesError(RuntimeException("flaky"))
        worker.doWork().shouldBeInstanceOf<WorkerResult.Retry>()

        syncRepository.setPendingEpisodesError(null)
        worker.doWork().shouldBeInstanceOf<WorkerResult.Success>()
    }
}
