package com.thomaskioko.tvmaniac.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.shows.api.FetchShows
import com.thomaskioko.tvmaniac.shows.api.ShowsAction
import com.thomaskioko.tvmaniac.shows.api.ShowsState
import com.thomaskioko.tvmaniac.shows.api.ShowsStateMachine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val stateMachine: ShowsStateMachine
) : ViewModel() {

    val state: MutableStateFlow<ShowsState> = MutableStateFlow(FetchShows)

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
