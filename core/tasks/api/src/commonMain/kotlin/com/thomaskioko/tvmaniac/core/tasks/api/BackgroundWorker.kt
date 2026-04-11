package com.thomaskioko.tvmaniac.core.tasks.api

/**
 * A unit of background work that can be executed by the task scheduler.
 *
 * Workers contain the shared business logic that runs on both platforms.
 * Each worker is registered via Metro multibinding and looked up
 * at runtime by [workerName] through [WorkerFactory].
 *
 * To add a new background job, implement this interface in `commonMain` and
 * annotate with `@ContributesIntoSet(AppScope::class)`.
 */
public interface BackgroundWorker {

    /**
     * Unique identifier for this worker. Must use reverse-DNS format
     * (e.g., `com.thomaskioko.tvmaniac.sync.library`) and match the
     * corresponding iOS `Info.plist` `BGTaskSchedulerPermittedIdentifiers` entry.
     */
    public val workerName: String

    /**
     * Executes the background work and returns the outcome.
     *
     * @return [WorkerResult.Success] if the work completed,
     *   [WorkerResult.Retry] if the work should be retried later, or
     *   [WorkerResult.Failure] if the work failed permanently.
     */
    public suspend fun doWork(): WorkerResult
}
