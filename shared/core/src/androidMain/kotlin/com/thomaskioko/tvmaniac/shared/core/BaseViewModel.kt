package com.thomaskioko.tvmaniac.shared.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.StateFlow

actual abstract class BaseViewModel : CoroutineScopeOwner {

    private val job = SupervisorJob()

    protected actual val vmScope: CoroutineScope
        get() = CoroutineScope(job + Dispatchers.IO)

    actual abstract val state: StateFlow<ViewState>

    actual abstract fun attach()

    actual open fun detach() {
        job.cancelChildren()
    }
}