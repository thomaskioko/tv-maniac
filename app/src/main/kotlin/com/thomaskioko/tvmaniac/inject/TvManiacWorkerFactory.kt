package com.thomaskioko.tvmaniac.inject

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.thomaskioko.tvmaniac.core.tasks.implementation.SchedulerDispatchWorker
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
public class TvManiacWorkerFactory(
    private val schedulerDispatchWorker: (Context, WorkerParameters) -> SchedulerDispatchWorker,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            SchedulerDispatchWorker::class.qualifiedName -> schedulerDispatchWorker(appContext, workerParameters)
            else -> null
        }
    }
}
