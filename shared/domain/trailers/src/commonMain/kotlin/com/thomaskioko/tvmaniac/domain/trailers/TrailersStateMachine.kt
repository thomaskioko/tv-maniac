package com.thomaskioko.tvmaniac.domain.trailers

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.domain.trailers.LoadingTrailers
import com.thomaskioko.tvmaniac.domain.trailers.TrailersAction
import com.thomaskioko.tvmaniac.domain.trailers.TrailersState
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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