package com.thomaskioko.tvmaniac.videoplayer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.thomaskioko.tvmaniac.core.util.CoroutineScopeOwner
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.ObserveTrailerInteractor
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerListAction
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerListEffect
import com.thomaskioko.tvmaniac.shared.core.ui.Store
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerListAction.Error
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerListAction.VideoPlayerInitialized
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerListEffect.TrailerListError
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.model.Trailer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeTrailerInteractor: ObserveTrailerInteractor
) : Store<TrailerListState, TrailerListAction, TrailerListEffect>, CoroutineScopeOwner,
    ViewModel() {

    private val showId: Long = savedStateHandle["showId"]!!
    private val videoKey: String? = savedStateHandle["videoKey"]

    private val uiEffects = MutableSharedFlow<TrailerListEffect>(extraBufferCapacity = 100)

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    override val state: MutableStateFlow<TrailerListState> =
        MutableStateFlow(TrailerListState.Empty)

    init {
        coroutineScope.launch {
            dispatch(TrailerListAction.LoadTrailers(showId))

            videoKey?.let {
                dispatch(TrailerListAction.TrailerSelected(it))
            }
        }
    }
    override fun observeState(): StateFlow<TrailerListState> = state

    override fun observeSideEffect(): Flow<TrailerListEffect> = uiEffects

    override fun dispatch(action: TrailerListAction) {
        when (action) {
            is TrailerListAction.LoadTrailers -> loadTrailers()
            is TrailerListAction.TrailerSelected -> {
                coroutineScope.launch {
                    state.emit(
                        state.value.copy(
                            selectedVideoKey = action.trailerKey
                        )
                    )
                }
            }
            is Error -> {
                coroutineScope.launch {
                    uiEffects.emit(TrailerListError(action.message))
                }
            }
            is VideoPlayerInitialized -> {
                coroutineScope.launch {
                    state.emit(
                        state.value.copy(
                            youTubePlayer = action.youtubePlayer as YouTubePlayer
                        )
                    )
                }
            }
        }
    }

    private fun loadTrailers() {
        with(state) {
            observeTrailerInteractor.execute(showId) {
                onStart {
                    coroutineScope.launch { state.emit(value.copy(isLoading = true)) }
                }
                onNext {
                    coroutineScope.launch {
                        emit(
                            value.copy(
                                isLoading = false,
                                trailersList = it
                            )
                        )
                    }
                }

                onError {
                    coroutineScope.launch {
                        emit(
                            value.copy(
                                isLoading = false,
                                errorMessage = it.message ?: "Something went wrong"
                            )
                        )

                        dispatch(Error(it.message ?: "Something went wrong"))
                    }
                }
            }
        }
    }
}


data class TrailerListState(
    val youTubePlayer: YouTubePlayer? = null,
    val selectedVideoKey: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val trailersList: List<Trailer> = emptyList(),
) {

    companion object {
        val Empty = TrailerListState()
    }
}
