package com.thomaskioko.tvmaniac.core.tasks.api

/**
 * Describes a periodic background task to be scheduled.
 *
 * Workers typically expose a companion `REQUEST` constant so that all call sites
 * share the same configuration:
 * ```kotlin
 * companion object {
 *   const val WORKER_NAME = "com.thomaskioko.tvmaniac.sync.library"
 *   val REQUEST = PeriodicTaskRequest(
 *     id = WORKER_NAME,
 *     intervalMs = 6.hours.inWholeMilliseconds,
 *     constraints = TaskConstraints(requiresNetwork = true),
 *   )
 * }
 * ```
 *
 * @property id Unique task identifier. Must match the [BackgroundWorker.workerName]
 *   of the worker that should execute this request.
 * @property intervalMs Minimum interval between executions in milliseconds.
 *   The OS may defer execution beyond this interval based on system conditions.
 *   On Android, WorkManager enforces a 15-minute minimum.
 * @property constraints Optional constraints that must be satisfied before
 *   the OS triggers execution.
 * @property longRunning When `true`, requests extended execution time from the OS.
 *   On iOS this uses `BGProcessingTaskRequest` (minutes of runtime, runs when idle)
 *   instead of `BGAppRefreshTaskRequest` (~30s). On Android this is a no-op since
 *   WorkManager handles execution time automatically.
 */
public data class PeriodicTaskRequest(
    val id: String,
    val intervalMs: Long,
    val constraints: TaskConstraints = TaskConstraints(),
    val longRunning: Boolean = false,
)
