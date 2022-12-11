package com.thomaskioko.tvmaniac.domain.trailers.api

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.domain.trailers.api.model.Trailer
import com.thomaskioko.tvmaniac.shared.domain.trailers.api.TrailerRepository
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
        var nextState: ChangedState<TrailersState> = state.noChange()
        trailerRepository.observeTrailersByShowId(action.showId)
            .collect { result ->
                nextState = when (result) {
                    is Resource.Error -> state.override { TrailerError(result.errorMessage) }
                    is Resource.Success -> state.override {
                        TrailersLoaded(
                            selectedVideoKey = action.trailerId,
                            trailersList = result.toTrailerList()
                        )
                    }
                }

            }
        return nextState
    }
}

fun Resource<List<Trailers>>.toTrailerList(): List<Trailer> {
    return data?.map {
        Trailer(
            showId = it.trakt_id,
            key = it.key,
            name = it.name,
            youtubeThumbnailUrl = "https://i.ytimg.com/vi/${it.key}/hqdefault.jpg"
        )
    } ?: emptyList()
}

/**
 * A wrapper class around [TrailersStateMachine] handling `Flow` and suspend functions on iOS.
 */
class TrailersStateMachineWrapper(
    private val stateMachine: TrailersStateMachine,
    dispatcher: MainCoroutineDispatcher,
) {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + dispatcher)

    fun dispatch(action: TrailersAction) {
        scope.launch {
            stateMachine.dispatch(action)
        }
    }

    fun start(stateChangeListener: (TrailersState) -> Unit) {
        scope.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun cancel() {
        job.cancelChildren()
    }
}