package com.thomaskioko.tvmaniac.core.tasks.implementation

import com.thomaskioko.tvmaniac.core.logger.CrashReportKeys
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerFactory
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class IosTaskSchedulerTest {

    private val logger = FakeLogger()
    private val scheduler = IosTaskScheduler(
        workerFactory = EmptyWorkerFactory,
        appCoroutineScope = TestScope(),
        logger = logger,
    )

    @Test
    fun `should record an error with worker and attempt keys given the worker fails with a cause`() = runTest {
        val cause = IllegalStateException("gone")

        val success = scheduler.runWorker("task-failure", RecordingWorker(WorkerResult.Failure("gone", cause)))

        success shouldBe false
        logger.recordedErrors.size shouldBe 1
        logger.recordedErrors[0].throwable shouldBe cause
        logger.recordedErrors[0].keys shouldBe mapOf(
            CrashReportKeys.WORKER to "task-failure",
            CrashReportKeys.ATTEMPT to "1",
            CrashReportKeys.RESULT to "failure",
        )
    }

    @Test
    fun `should record an error with worker and attempt keys given the worker throws`() = runTest {
        val cause = IllegalStateException("boom")

        val success = scheduler.runWorker("task-threw", ThrowingWorker(cause))

        success shouldBe false
        logger.recordedErrors.size shouldBe 1
        logger.recordedErrors[0].throwable shouldBe cause
        logger.recordedErrors[0].keys shouldBe mapOf(
            CrashReportKeys.WORKER to "task-threw",
            CrashReportKeys.ATTEMPT to "1",
            CrashReportKeys.RESULT to "threw",
        )
    }

    @Test
    fun `should write a breadcrumb only given the worker requests a retry`() = runTest {
        val cause = IllegalStateException("later")

        val success = scheduler.runWorker("task-retry", RecordingWorker(WorkerResult.Retry("later", cause)))

        success shouldBe false
        logger.recordedErrors.size shouldBe 0
        logger.recordedWarnings.size shouldBe 1
        logger.recordedWarnings[0].throwable shouldBe cause
        logger.recordedWarnings[0].keys shouldBe mapOf(
            CrashReportKeys.WORKER to "task-retry",
            CrashReportKeys.ATTEMPT to "1",
            CrashReportKeys.RESULT to "retry",
        )
    }

    @Test
    fun `should let a cancellation through and record nothing given the worker is cancelled`() = runTest {
        val outcome = runCatching { scheduler.runWorker("task-cancelled", ThrowingWorker(CancellationException("stopped"))) }

        outcome.exceptionOrNull().let { it is CancellationException } shouldBe true
        logger.recordedErrors.size shouldBe 0
        logger.recordedWarnings.size shouldBe 0
    }

    @Test
    fun `should increment the attempt on each run and clear it on success`() = runTest {
        val cause = IllegalStateException("later")

        scheduler.runWorker("task-attempts", RecordingWorker(WorkerResult.Retry("later", cause)))
        scheduler.runWorker("task-attempts", RecordingWorker(WorkerResult.Retry("later", cause)))
        logger.recordedWarnings[1].keys[CrashReportKeys.ATTEMPT] shouldBe "2"

        scheduler.runWorker("task-attempts", RecordingWorker(WorkerResult.Success))
        scheduler.runWorker("task-attempts", RecordingWorker(WorkerResult.Retry("later", cause)))
        logger.recordedWarnings[2].keys[CrashReportKeys.ATTEMPT] shouldBe "1"
    }
}

private object EmptyWorkerFactory : WorkerFactory {
    override val workerNames: Set<String> = emptySet()
    override fun createWorker(workerName: String): BackgroundWorker? = null
}

private class RecordingWorker(private val result: WorkerResult) : BackgroundWorker {
    override val workerName: String = "recording"
    override suspend fun doWork(): WorkerResult = result
}

private class ThrowingWorker(private val error: Throwable) : BackgroundWorker {
    override val workerName: String = "throwing"
    override suspend fun doWork(): WorkerResult = throw error
}
