package com.thomaskioko.tvmaniac.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.shared.domain.discover.DiscoverStateMachine
import com.thomaskioko.tvmaniac.shared.domain.discover.Loading
import com.thomaskioko.tvmaniac.shared.domain.discover.ShowsAction
import com.thomaskioko.tvmaniac.shared.domain.discover.ShowsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val stateMachine: DiscoverStateMachine
) : ViewModel() {

    val state: MutableStateFlow<ShowsState> = MutableStateFlow(Loading)

    init {
        viewModelScope.launch {
            stateMachine.state.collect { newState ->
                state.value = newState
            }
        }
    }

    fun dispatch(action : ShowsAction) {
        viewModelScope.launch {
            stateMachine.dispatch(action)
        }
    }
}
