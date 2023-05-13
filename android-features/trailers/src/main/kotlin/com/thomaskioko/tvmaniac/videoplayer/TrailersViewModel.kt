package com.thomaskioko.tvmaniac.videoplayer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.presentation.trailers.LoadingTrailers
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersAction
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersState
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersStateMachine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class TrailersViewModel(
    @Assisted savedStateHandle: SavedStateHandle,
    private val stateMachine: (Long) -> TrailersStateMachine,
) : ViewModel() {

    private val showId: Long = savedStateHandle["showId"]!!

    val state: MutableStateFlow<TrailersState> = MutableStateFlow(LoadingTrailers)

    init {
        viewModelScope.launch {
            stateMachine(showId).state
                .collect {
                    state.value = it
                }
        }
    }

    fun dispatch(action: TrailersAction) {
        viewModelScope.launch {
            stateMachine(showId).dispatch(action)
        }
    }
}
