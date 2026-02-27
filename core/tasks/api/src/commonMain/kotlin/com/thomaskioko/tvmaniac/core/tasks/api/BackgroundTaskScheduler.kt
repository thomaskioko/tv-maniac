package com.thomaskioko.tvmaniac.core.tasks.api

public interface BackgroundTaskScheduler {

    /**
     * Registers a periodic task with the OS scheduler. No work is executed immediately;
     * the OS decides when to trigger the task based on system conditions and the
     * requested [PeriodicTaskRequest.intervalMs].
     *
     * Use this on app launch to ensure background jobs are registered without
     * causing unnecessary network calls at startup.
     */
    public fun schedulePeriodic(request: PeriodicTaskRequest)

    /**
     * Registers a periodic task **and** runs it immediately. Use this for
     * user-initiated actions (e.g., toggling notifications on) where the user
     * expects an immediate result in addition to future periodic execution.
     */
    public fun scheduleAndExecute(request: PeriodicTaskRequest)

    /**
     * Cancels a previously scheduled task by its unique [id].
     *
     * @param id The worker name / task identifier used when scheduling.
     */
    public fun cancel(id: String)

    /**
     * Cancels all scheduled tasks managed by this scheduler.
     */
    public fun cancelAll()

    /**
     * Re-submits all currently active periodic requests to the OS scheduler.
     *
     * This is only meaningful on iOS, where `BGTaskScheduler` requires tasks to be
     * re-submitted each time the app enters the background. The default implementation
     * is a no-op for platforms that don't need this behavior.
     */
    public fun rescheduleBackgroundTask() {}
}
