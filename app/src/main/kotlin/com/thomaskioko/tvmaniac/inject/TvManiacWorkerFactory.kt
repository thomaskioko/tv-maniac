package com.thomaskioko.tvmaniac.inject

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.thomaskioko.tvmaniac.core.tasks.implementation.LibrarySyncWorker
import com.thomaskioko.tvmaniac.traktauth.implementation.task.TokenRefreshWorker
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
public class TvManiacWorkerFactory(
    private val tokenRefreshWorker: (Context, WorkerParameters) -> TokenRefreshWorker,
    private val librarySyncWorker: (Context, WorkerParameters) -> LibrarySyncWorker,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        val tokenRefreshWorkerName = name<TokenRefreshWorker>()
            ?: error("TokenRefreshWorker qualifiedName should not be null")
        val librarySyncWorkerName = name<LibrarySyncWorker>()
            ?: error("LibrarySyncWorker qualifiedName should not be null")

        return when (workerClassName) {
            tokenRefreshWorkerName -> tokenRefreshWorker(appContext, workerParameters)
            librarySyncWorkerName -> librarySyncWorker(appContext, workerParameters)
            else -> null
        }
    }

    private inline fun <reified C> name() = C::class.qualifiedName
}
