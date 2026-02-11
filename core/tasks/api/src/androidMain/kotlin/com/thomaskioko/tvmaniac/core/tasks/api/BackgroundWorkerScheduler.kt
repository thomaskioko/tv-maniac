package com.thomaskioko.tvmaniac.core.tasks.api

/**
 * Schedules and manages [BackgroundWorker] execution via WorkManager.
 *
 * Workers must be [registered][register] before they can be scheduled.
 */
public interface BackgroundWorkerScheduler {

    /**
     * Registers a [worker] so it can be looked up by [BackgroundWorker.workerName]
     * when execution is triggered.
     *
     * @param worker The [BackgroundWorker] to register.
     */
    public fun register(worker: BackgroundWorker)

    /**
     * Schedules recurring execution at the worker's configured [BackgroundWorker.interval].
     *
     * Calling this multiple times for the same worker updates the existing schedule
     * rather than creating duplicates.
     *
     * @param workerName Identifier of a previously [registered][register] worker.
     */
    public fun schedulePeriodic(workerName: String)

    /**
     * Schedules a one-time execution that runs as soon as conditions allow.
     *
     * @param workerName Identifier of a previously [registered][register] worker.
     */
    public fun scheduleImmediate(workerName: String)

    /**
     * Convenience that triggers [scheduleImmediate] followed by [schedulePeriodic].
     *
     * Use this when you want the work to run now *and* continue on a recurring schedule.
     *
     * @param workerName Identifier of a previously [registered][register] worker.
     */
    public fun scheduleAndExecute(workerName: String)

    /**
     * Cancels all scheduled work (both periodic and immediate) for the given worker.
     *
     * @param workerName Identifier of a previously [registered][register] worker.
     */
    public fun cancel(workerName: String)
}
