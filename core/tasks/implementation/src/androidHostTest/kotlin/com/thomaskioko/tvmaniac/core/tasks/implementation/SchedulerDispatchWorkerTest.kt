package com.thomaskioko.tvmaniac.core.tasks.implementation

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.thomaskioko.tvmaniac.core.logger.CrashReportKeys
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerFactory
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SchedulerDispatchWorkerTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val workerFactory = RecordingWorkerFactory()

    @Test
    fun `should report success given the worker succeeds`() = runTest {
        workerFactory.register(RecordingWorker(WORKER_NAME, WorkerResult.Success))

        val result = buildWorker(WORKER_NAME).doWork()

        result shouldBe ListenableWorker.Result.success()
    }

    @Test
    fun `should ask for a retry given the worker asks for one`() = runTest {
        workerFactory.register(RecordingWorker(WORKER_NAME, WorkerResult.Retry("later")))

        val result = buildWorker(WORKER_NAME).doWork()

        result shouldBe ListenableWorker.Result.retry()
    }

    @Test
    fun `should write a breadcrumb only given the worker requests a retry with a cause`() = runTest {
        val logger = FakeLogger()
        val cause = IllegalStateException("later")
        workerFactory.register(RecordingWorker(WORKER_NAME, WorkerResult.Retry("later", cause)))

        buildWorker(WORKER_NAME, logger).doWork()

        logger.recordedWarnings.size shouldBe 1
        logger.recordedWarnings[0].throwable shouldBe cause
        logger.recordedWarnings[0].keys shouldBe mapOf(
            CrashReportKeys.WORKER to WORKER_NAME,
            CrashReportKeys.ATTEMPT to "1",
            CrashReportKeys.RESULT to "retry",
        )
        logger.recordedErrors.size shouldBe 0
    }

    @Test
    fun `should report failure given the worker fails permanently`() = runTest {
        workerFactory.register(RecordingWorker(WORKER_NAME, WorkerResult.Failure("gone")))

        val result = buildWorker(WORKER_NAME).doWork()

        result shouldBe ListenableWorker.Result.failure()
    }

    @Test
    fun `should record an error with worker and attempt keys given the worker fails with a cause`() = runTest {
        val logger = FakeLogger()
        val cause = IllegalStateException("gone")
        workerFactory.register(RecordingWorker(WORKER_NAME, WorkerResult.Failure("gone", cause)))

        buildWorker(WORKER_NAME, logger).doWork()

        logger.recordedErrors.size shouldBe 1
        logger.recordedErrors[0].throwable shouldBe cause
        logger.recordedErrors[0].keys shouldBe mapOf(
            CrashReportKeys.WORKER to WORKER_NAME,
            CrashReportKeys.ATTEMPT to "1",
            CrashReportKeys.RESULT to "failure",
        )
    }

    @Test
    fun `should write a breadcrumb only given the worker fails with no cause`() = runTest {
        val logger = FakeLogger()
        workerFactory.register(RecordingWorker(WORKER_NAME, WorkerResult.Failure("gone")))

        buildWorker(WORKER_NAME, logger).doWork()

        logger.recordedErrors.size shouldBe 0
        logger.breadcrumbs shouldBe listOf("[$TAG] Task [$WORKER_NAME] failed")
    }

    @Test
    fun `should report failure given no worker name is passed`() = runTest {
        val result = buildWorker(workerName = null).doWork()

        result shouldBe ListenableWorker.Result.failure()
    }

    @Test
    fun `should report failure given no worker is registered under that name`() = runTest {
        val result = buildWorker("com.thomaskioko.tvmaniac.unregistered").doWork()

        result shouldBe ListenableWorker.Result.failure()
    }

    @Test
    fun `should report failure given the worker throws`() = runTest {
        workerFactory.register(ThrowingWorker(WORKER_NAME, IllegalStateException("boom")))

        val result = buildWorker(WORKER_NAME).doWork()

        result shouldBe ListenableWorker.Result.failure()
    }

    @Test
    fun `should record an error with worker and attempt keys given the worker throws`() = runTest {
        val logger = FakeLogger()
        val cause = IllegalStateException("boom")
        workerFactory.register(ThrowingWorker(WORKER_NAME, cause))

        buildWorker(WORKER_NAME, logger).doWork()

        logger.recordedErrors.size shouldBe 1
        logger.recordedErrors[0].throwable shouldBe cause
        logger.recordedErrors[0].keys shouldBe mapOf(
            CrashReportKeys.WORKER to WORKER_NAME,
            CrashReportKeys.ATTEMPT to "1",
            CrashReportKeys.RESULT to "threw",
        )
    }

    @Test
    fun `should let a cancellation through given the worker is cancelled`() = runTest {
        workerFactory.register(ThrowingWorker(WORKER_NAME, CancellationException("stopped")))

        val worker = buildWorker(WORKER_NAME)

        runCatching { worker.doWork() }
            .exceptionOrNull()
            .let { it is CancellationException } shouldBe true
    }

    @Test
    fun `should record nothing given the worker is cancelled`() = runTest {
        val logger = FakeLogger()
        workerFactory.register(ThrowingWorker(WORKER_NAME, CancellationException("stopped")))

        runCatching { buildWorker(WORKER_NAME, logger).doWork() }

        logger.recordedErrors.size shouldBe 0
        logger.recordedWarnings.size shouldBe 0
        logger.breadcrumbs.size shouldBe 0
    }

    @Test
    fun `should run the worker named in the input data given several are registered`() = runTest {
        val wanted = RecordingWorker(WORKER_NAME, WorkerResult.Success)
        val other = RecordingWorker(OTHER_WORKER_NAME, WorkerResult.Success)
        workerFactory.register(wanted)
        workerFactory.register(other)

        buildWorker(OTHER_WORKER_NAME).doWork()

        wanted.ran shouldBe false
        other.ran shouldBe true
    }

    private fun buildWorker(workerName: String?, logger: FakeLogger = FakeLogger()): SchedulerDispatchWorker {
        val inputData = when (workerName) {
            null -> Data.EMPTY
            else -> Data.Builder().putString("worker_name", workerName).build()
        }
        return TestListenableWorkerBuilder<SchedulerDispatchWorker>(context)
            .setInputData(inputData)
            .setWorkerFactory(
                object : androidx.work.WorkerFactory() {
                    override fun createWorker(
                        appContext: Context,
                        workerClassName: String,
                        workerParameters: WorkerParameters,
                    ): ListenableWorker = SchedulerDispatchWorker(
                        context = appContext,
                        params = workerParameters,
                        workerFactory = workerFactory,
                        logger = logger,
                    )
                },
            )
            .build()
    }

    private companion object {
        private const val WORKER_NAME = "com.thomaskioko.tvmaniac.autobackup"
        private const val OTHER_WORKER_NAME = "com.thomaskioko.tvmaniac.librarysync"
        private const val TAG = "SchedulerDispatchWorker"
    }
}

private class RecordingWorkerFactory : WorkerFactory {
    private val workers = mutableMapOf<String, BackgroundWorker>()

    fun register(worker: BackgroundWorker) {
        workers[worker.workerName] = worker
    }

    override val workerNames: Set<String> get() = workers.keys

    override fun createWorker(workerName: String): BackgroundWorker? = workers[workerName]
}

private class RecordingWorker(
    override val workerName: String,
    private val result: WorkerResult,
) : BackgroundWorker {
    var ran: Boolean = false
        private set

    override suspend fun doWork(): WorkerResult {
        ran = true
        return result
    }
}

private class ThrowingWorker(
    override val workerName: String,
    private val error: Throwable,
) : BackgroundWorker {
    override suspend fun doWork(): WorkerResult = throw error
}
