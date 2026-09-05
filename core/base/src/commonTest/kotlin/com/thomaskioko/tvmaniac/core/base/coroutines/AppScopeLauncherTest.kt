package com.thomaskioko.tvmaniac.core.base.coroutines

import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class AppScopeLauncherTest {

    private val logger = FakeLogger()

    @Test
    fun `should run block on injected scope`() = runTest {
        val launcher = DefaultAppScopeLauncher(backgroundScope, logger)
        var ran = false

        launcher.launch("Test") { ran = true }
        runCurrent()

        ran shouldBe true
        logger.recordedErrors shouldHaveSize 0
    }

    @Test
    fun `should log error with throwable when block fails`() = runTest {
        val launcher = DefaultAppScopeLauncher(backgroundScope, logger)
        val cause = IllegalStateException("boom")

        launcher.launch("FollowShowInteractor") { throw cause }
        runCurrent()

        logger.recordedErrors shouldBe listOf(
            FakeLogger.RecordedError(
                tag = "FollowShowInteractor",
                message = "Background job failed",
                throwable = cause,
                keys = emptyMap(),
            ),
        )
    }

    @Test
    fun `should not log when block is cancelled`() = runTest {
        val launcher = DefaultAppScopeLauncher(backgroundScope, logger)

        val job = launcher.launch("Test") {
            throw CancellationException("caller went away")
        }
        runCurrent()

        job.isCancelled shouldBe true
        logger.recordedErrors shouldHaveSize 0
    }
}
