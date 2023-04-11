package com.thomaskioko.tvmaniac.data.trailers

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject


@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Inject
class TrailersStateMachine(
    private val trailerRepository: TrailerRepository
) : FlowReduxStateMachine<TrailersState, TrailersAction>(initialState = LoadingTrailers) {

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
@Inject
class TrailersStateMachineWrapper(
    private val scope: AppCoroutineScope,
    private val stateMachine: TrailersStateMachine,
) {

    fun start(stateChangeListener: (TrailersState) -> Unit) {
        scope.main.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(action: TrailersAction) {
        scope.main.launch {
            stateMachine.dispatch(action)
        }
    }

    fun cancel() {
        scope.main.cancel()
    }
}