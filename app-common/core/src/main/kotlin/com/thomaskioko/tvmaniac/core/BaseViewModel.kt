package com.thomaskioko.tvmaniac.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseViewModel<A : ViewAction, S : ViewState>(
    initialViewState: S,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val viewModelJob = SupervisorJob()
    val ioScope = CoroutineScope(dispatcher + viewModelJob)

    protected val mutableViewState: MutableStateFlow<S> =
        MutableStateFlow(initialViewState)

    val stateFlow: Flow<S> get() = mutableViewState

    abstract fun handleAction(action: A)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun dispatchAction(action: A) {
        handleAction(action)
    }
}
