package com.thomaskioko.tvmaniac.shared.core

import com.thomaskioko.tvmaniac.shared.core.store.Action
import com.thomaskioko.tvmaniac.shared.core.store.ViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

expect abstract class BaseViewModel() {

    abstract val state: StateFlow<ViewState>

    protected val vmScope: CoroutineScope

    abstract fun attach()

    open fun detach()

    abstract fun dispatch(action: Action)
}
