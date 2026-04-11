package com.thomaskioko.tvmaniac.app.util

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.thomaskioko.tvmaniac.core.tasks.implementation.SchedulerDispatchWorker
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
public class TvManiacWorkerFactory(
    private val schedulerDispatchWorkerFactory: SchedulerDispatchWorker.Factory,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            SchedulerDispatchWorker::class.qualifiedName -> schedulerDispatchWorkerFactory.create(appContext, workerParameters)
            else -> null
        }
    }
}
