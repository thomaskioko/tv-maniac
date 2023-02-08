package com.thomaskioko.tvmaniac.videoplayer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.domain.trailers.api.LoadTrailers
import com.thomaskioko.tvmaniac.domain.trailers.api.LoadingTrailers
import com.thomaskioko.tvmaniac.domain.trailers.api.TrailersAction
import com.thomaskioko.tvmaniac.domain.trailers.api.TrailersState
import com.thomaskioko.tvmaniac.domain.trailers.api.TrailersStateMachine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val stateMachine: TrailersStateMachine
) : ViewModel() {

    private val showId: Long = savedStateHandle["showId"]!!
    private val videoKey: String? = savedStateHandle["videoKey"]

    val state: MutableStateFlow<TrailersState> = MutableStateFlow(LoadingTrailers)

    init {
        viewModelScope.launch {
            stateMachine.state
                .collect {
                    state.value = it
                }
        }

        viewModelScope.launch {
            stateMachine.dispatch(LoadTrailers(showId, videoKey!!))
        }

    }

    fun dispatch(action: TrailersAction) {
        viewModelScope.launch {
            stateMachine.dispatch(action)
        }
    }
}
