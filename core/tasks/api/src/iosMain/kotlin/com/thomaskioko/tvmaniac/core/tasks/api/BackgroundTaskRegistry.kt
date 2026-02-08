package com.thomaskioko.tvmaniac.core.tasks.api

/**
 * Registers, schedules, and cancels [BackgroundTask] execution on iOS.
 *
 * Tasks must be [registered][register] before they can be scheduled.
 */
public interface BackgroundTaskRegistry {

    /**
     * Registers a [task] so it can be looked up by its [BackgroundTask.taskId]
     * when the system triggers a background refresh.
     *
     * @param task The [BackgroundTask] to register.
     */
    public fun register(task: BackgroundTask)

    /**
     * Schedules the task for background execution at its configured
     * [BackgroundTask.interval].
     *
     * @param taskId Identifier of a previously [registered][register] task.
     */
    public fun schedule(taskId: String)

    /**
     * Schedules the task for recurring background execution, then runs it
     * immediately in-process.
     *
     * Use this when you want the work to run now *and* continue on a recurring schedule.
     *
     * @param taskId Identifier of a previously [registered][register] task.
     */
    public fun scheduleAndExecute(taskId: String)

    /**
     * Cancels any pending background execution for the given [taskId].
     *
     * @param taskId Identifier of a previously [registered][register] task.
     */
    public fun cancel(taskId: String)
}
