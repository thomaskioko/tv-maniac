package com.thomaskioko.tvmaniac.data.trailers

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch


@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class TrailersStateMachine constructor(
    private val trailerRepository: TrailerRepository
) : FlowReduxStateMachine<TrailersState, TrailersAction>(
    initialState = LoadingTrailers
) {

    init {
        spec {
            inState<LoadingTrailers> {
                on<LoadTrailers> { action, state ->
                    loadTrailers(action, state)
                }

                on<VideoPlayerError> { action, state ->
                    state.override { TrailerError(action.errorMessage) }
                }

            }

            inState<TrailersLoaded> {
                on<TrailerSelected> { action, state ->
                    state.mutate {
                        copy(selectedVideoKey = action.trailerKey)
                    }
                }

            }

            inState<TrailerError> {
                on<ReloadTrailers> { _, state ->
                    state.override { LoadingTrailers }
                }
            }
        }
    }

    private suspend fun loadTrailers(
        action: LoadTrailers,
        state: State<LoadingTrailers>
    ): ChangedState<TrailersState> {
        var nextState: TrailersState = state.snapshot

        trailerRepository.observeTrailersByShowId(action.showId)
            .collect { result ->
                nextState = result.fold(
                    { TrailerError(it.errorMessage) },
                    {
                        TrailersLoaded(
                            selectedVideoKey = action.trailerId,
                            trailersList = it.toTrailerList()
                        )
                    }
                )
            }
        return state.override { nextState }
    }
}

/**
 * A wrapper class around [TrailersStateMachine] handling `Flow` and suspend functions on iOS.
 */
class TrailersStateMachineWrapper(
    private val stateMachine: TrailersStateMachine,
    private val scope: CoroutineScope,
) {
    fun start(stateChangeListener: (TrailersState) -> Unit) {
        scope.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(action: TrailersAction) {
        scope.launch {
            stateMachine.dispatch(action)
        }
    }
}