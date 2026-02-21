package com.thomaskioko.tvmaniac.core.tasks.implementation

import com.thomaskioko.tvmaniac.core.tasks.api.BackgroundWorker
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerFactory
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWorkerFactory(
    workers: Set<BackgroundWorker>,
) : WorkerFactory {
    private val registry: Map<String, BackgroundWorker> = workers.associateBy { it.workerName }

    override fun createWorker(workerName: String): BackgroundWorker? = registry[workerName]
}
