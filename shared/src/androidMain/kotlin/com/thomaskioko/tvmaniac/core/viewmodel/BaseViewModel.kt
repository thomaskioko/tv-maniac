package com.thomaskioko.tvmaniac.core.viewmodel

import com.thomaskioko.tvmaniac.core.usecase.scope.CoroutineScopeOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent

actual abstract class BaseViewModel : CoroutineScopeOwner, KoinComponent {

    private val job = SupervisorJob()

    protected actual val vmScope: CoroutineScope
        get() = CoroutineScope(job + Dispatchers.IO)

    actual abstract val state: StateFlow<ViewState>

    actual abstract fun attach()

    actual open fun detach() {
        job.cancelChildren()
    }
}
