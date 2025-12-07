package com.thomaskioko.tvmaniac.traktauth.implementation.task

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
public class TraktAuthWorkerFactory(
    private val tokenRefreshWorker: (Context, WorkerParameters) -> TokenRefreshWorker,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        val tokenRefreshWorkerName = name<TokenRefreshWorker>()
            ?: error("TokenRefreshWorker qualifiedName should not be null")

        return when (workerClassName) {
            tokenRefreshWorkerName -> tokenRefreshWorker(appContext, workerParameters)
            else -> null
        }
    }

    private inline fun <reified C> name() = C::class.qualifiedName
}
