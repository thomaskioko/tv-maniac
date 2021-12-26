package com.thomaskioko.tvmaniac.core.usecase.scope

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

actual interface CoroutineScopeOwner {
    actual val coroutineScope: CoroutineScope

    actual fun getWorkerDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
