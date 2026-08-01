package com.thomaskioko.tvmaniac.core.base.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.concurrent.Volatile

public object CoroutineCrashUtil {

    @Volatile
    private var onException: (Throwable) -> Unit = { it.printStackTrace() }

    public val handler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        try {
            onException(throwable)
        } catch (_: Throwable) {
            throwable.printStackTrace()
        }
    }

    public fun setUncaughtException(onException: (Throwable) -> Unit) {
        this.onException = onException
    }
}
