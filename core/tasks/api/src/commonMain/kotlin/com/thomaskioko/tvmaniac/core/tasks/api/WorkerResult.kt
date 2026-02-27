package com.thomaskioko.tvmaniac.core.tasks.api

/**
 * Outcome of a [BackgroundWorker.doWork] execution.
 *
 * Platform schedulers map these results to their native equivalents
 * (e.g., `ListenableWorker.Result` on Android, `BGTask.setTaskCompleted` on iOS).
 */
public sealed class WorkerResult {

    /** The work completed successfully. */
    public data object Success : WorkerResult()

    /** The work failed transiently and should be retried by the OS. */
    public data class Retry(val message: String? = null) : WorkerResult()

    /** The work failed permanently. The OS will not retry automatically. */
    public data class Failure(val message: String? = null) : WorkerResult()
}
