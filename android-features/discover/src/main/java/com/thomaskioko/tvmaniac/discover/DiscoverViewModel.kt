package com.thomaskioko.tvmaniac.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverState
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverStateMachine
import com.thomaskioko.tvmaniac.presentation.discover.Loading
import com.thomaskioko.tvmaniac.presentation.discover.ShowsAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class DiscoverViewModel(
    private val stateMachine: DiscoverStateMachine,
) : ViewModel() {

    val state: MutableStateFlow<DiscoverState> = MutableStateFlow(Loading)

    init {
        viewModelScope.launch {
            stateMachine.state.collect { newState ->
                state.value = newState
            }
        }
    }

    fun dispatch(action: ShowsAction) {
        viewModelScope.launch {
            stateMachine.dispatch(action)
        }
    }
}
