package com.thomaskioko.tvmaniac.shared.core.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

open class ViewState
interface Action
interface Effect

interface Store<State : Any, Action : Any, E : Effect> {

    val state: StateFlow<State>

    fun observeState(): StateFlow<State>
    fun observeSideEffect(): Flow<E>
    fun dispatch(action: Action)
}
