package com.thomaskioko.tvmaniac.core.tasks.api

/**
 * Contract for a unit of background work on iOS.
 *
 * Implementations register themselves with a [BackgroundTaskRegistry] and
 * are looked up by [taskId] when the registry triggers execution.
 *
 * @property taskId Unique reverse-DNS identifier for this task
 *     (e.g., `com.thomaskioko.tvmaniac.librarysync`).
 * @property interval Minimum number of seconds between executions.
 *     The system may delay execution beyond this interval.
 */
public interface BackgroundTask {
    public val taskId: String
    public val interval: Double

    /**
     * Performs the background work.
     */
    public suspend fun execute()
}
