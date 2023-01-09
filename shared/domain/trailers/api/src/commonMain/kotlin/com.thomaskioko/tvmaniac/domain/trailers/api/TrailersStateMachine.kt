package com.thomaskioko.tvmaniac.domain.trailers.api

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.domain.trailers.api.model.Trailer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch


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
    fun dispatch(action: TrailersAction) {
        scope.launch {
            stateMachine.dispatch(action)
        }
    }
}