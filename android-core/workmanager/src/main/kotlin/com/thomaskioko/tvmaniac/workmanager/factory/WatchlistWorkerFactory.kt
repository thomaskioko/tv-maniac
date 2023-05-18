package com.thomaskioko.tvmaniac.workmanager.factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.thomaskioko.tvmaniac.workmanager.SyncWatchlist
import me.tatarka.inject.annotations.Inject

@Inject
class WatchlistWorkerFactory(
    private val syncWatchlist: (Context, WorkerParameters) -> SyncWatchlist,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? = when (workerClassName) {
        name<SyncWatchlist>() -> syncWatchlist(appContext, workerParameters)
        else -> null
    }

    private inline fun <reified C> name() = C::class.qualifiedName
}
