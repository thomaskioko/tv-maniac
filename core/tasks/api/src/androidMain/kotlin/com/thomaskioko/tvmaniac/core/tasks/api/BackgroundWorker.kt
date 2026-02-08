package com.thomaskioko.tvmaniac.core.tasks.api

import kotlin.time.Duration

/**
 * Contract for a unit of background work on Android.
 *
 * Implementations register themselves with a [BackgroundWorkerScheduler] and
 * are looked up by [workerName] when the scheduler triggers execution.
 *
 * @property workerName Stable identifier for this worker. Must be unique across
 *     all registered workers.
 * @property interval How often the periodic work should repeat.
 * @property constraints Conditions that must be met before execution.
 */
public interface BackgroundWorker {
    public val workerName: String
    public val interval: Duration
    public val constraints: WorkerConstraints

    /**
     * Performs the background work.
     *
     * @return [WorkerResult] indicating whether the work succeeded, should retry, or failed.
     */
    public suspend fun execute(): WorkerResult
}
