package com.thomaskioko.tvmaniac.inject

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.thomaskioko.tvmaniac.core.tasks.implementation.DispatchingWorker
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
public class TvManiacWorkerFactory(
    private val dispatchingWorker: (Context, WorkerParameters) -> DispatchingWorker,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            DispatchingWorker::class.qualifiedName -> dispatchingWorker(appContext, workerParameters)
            else -> null
        }
    }
}
