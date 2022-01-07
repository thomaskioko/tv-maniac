package com.thomaskioko.tvmaniac.shared.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

expect abstract class BaseViewModel() {

    abstract val state: StateFlow<ViewState>

    protected val vmScope: CoroutineScope

    abstract fun attach()

    open fun detach()
}

open class ViewState