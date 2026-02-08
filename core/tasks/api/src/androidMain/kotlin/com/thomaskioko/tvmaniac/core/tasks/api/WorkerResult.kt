package com.thomaskioko.tvmaniac.core.tasks.api

/**
 * Outcome of a [BackgroundWorker.execute] invocation.
 */
public sealed class WorkerResult {
    /** Work completed successfully. */
    public data object Success : WorkerResult()

    /** Work failed but should be retried according to the backoff policy. */
    public data object Retry : WorkerResult()

    /** Work failed permanently and should not be retried. */
    public data object Failure : WorkerResult()
}
