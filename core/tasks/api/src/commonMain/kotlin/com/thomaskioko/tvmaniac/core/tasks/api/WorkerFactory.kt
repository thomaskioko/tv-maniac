package com.thomaskioko.tvmaniac.core.tasks.api

/**
 * Resolves [BackgroundWorker] instances by name at runtime.
 *
 * Platform schedulers delegate to this factory when the OS triggers background
 * execution. The default implementation ([DefaultWorkerFactory]) uses kotlin-inject
 * multibinding to collect all registered workers and look them up by
 * [BackgroundWorker.workerName].
 */
public interface WorkerFactory {

    public val workerNames: Set<String>

    /**
     * Returns the [BackgroundWorker] registered under [workerName], or `null`
     * if no worker matches.
     */
    public fun createWorker(workerName: String): BackgroundWorker?
}
