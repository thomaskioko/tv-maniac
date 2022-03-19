package com.thomaskioko.tvmaniac.shared.core.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual abstract class BaseViewModel : KoinComponent {

    private val mainDispatcher: MainDispatcher by inject()

    private val job = SupervisorJob()

    actual abstract val state: StateFlow<ViewState>

    protected actual val vmScope: CoroutineScope
        get() = CoroutineScope(job + mainDispatcher.main)

    actual abstract fun attach()

    actual abstract fun dispatch(action: Action)

    actual open fun detach() {
        job.cancelChildren()
    }

    fun <T> Flow<T>.observe(onChange: ((T) -> Unit)) {
        onEach { onChange(it) }
            .launchIn(vmScope)
    }
}
