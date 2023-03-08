package com.thomaskioko.tvmaniac.show_grid

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowGridViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val stateMachine: GridStateMachine
) : ViewModel() {

    val showType: Long = savedStateHandle["showType"]!!

    val state: MutableStateFlow<GridState> = MutableStateFlow(LoadingContent)

    init {
        viewModelScope.launch {
            stateMachine.state.collect { newState ->
                state.value = newState
            }
        }

        viewModelScope.launch {
            stateMachine.dispatch(LoadShows(showType))
        }

    }

    fun dispatch(action: GridActions) {
        viewModelScope.launch {
            stateMachine.dispatch(action)
        }
    }
}
