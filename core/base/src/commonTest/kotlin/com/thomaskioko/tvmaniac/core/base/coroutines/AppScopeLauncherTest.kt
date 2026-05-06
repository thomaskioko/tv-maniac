package com.thomaskioko.tvmaniac.core.base.coroutines

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class AppScopeLauncherTest {

    private val logger = RecordingLogger()

    @Test
    fun `should run block on injected scope`() = runTest {
        val launcher = DefaultAppScopeLauncher(backgroundScope, logger)
        var ran = false

        launcher.launch("Test") { ran = true }
        runCurrent()

        ran shouldBe true
        logger.errors shouldHaveSize 0
    }

    @Test
    fun `should log error with throwable when block fails`() = runTest {
        val launcher = DefaultAppScopeLauncher(backgroundScope, logger)
        val cause = IllegalStateException("boom")

        launcher.launch("FollowShowInteractor") { throw cause }
        runCurrent()

        logger.errors shouldBe listOf(
            LoggedError(
                tag = "FollowShowInteractor",
                message = "Background job failed",
                throwable = cause,
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
        logger.errors shouldHaveSize 0
    }
}
