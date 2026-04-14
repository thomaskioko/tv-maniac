package com.thomaskioko.tvmaniac.core.tasks.implementation

import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWorkerFactory(
    workers: Set<BackgroundWorker>,
) : WorkerFactory {
    private val registry: Map<String, BackgroundWorker> = workers.associateBy { it.workerName }

    override val workerNames: Set<String> get() = registry.keys

    override fun createWorker(workerName: String): BackgroundWorker? = registry[workerName]
}
