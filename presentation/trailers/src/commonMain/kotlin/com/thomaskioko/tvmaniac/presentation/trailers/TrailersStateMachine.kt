package com.thomaskioko.tvmaniac.presentation.trailers

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Inject
class TrailersStateMachine(
    @Assisted private val traktShowId: Long,
    private val repository: TrailerRepository,
) : FlowReduxStateMachine<TrailersState, TrailersAction>(initialState = LoadingTrailers) {

    init {
        spec {
            inState<LoadingTrailers> {
                onEnter { state -> loadTrailers(state) }

                on<VideoPlayerError> { action, state ->
                    state.override { TrailerError(action.errorMessage) }
                }

                on<ReloadTrailers> { _, state ->
                    var nextState: TrailersState = state.snapshot

                    repository.observeTrailersByShowId(traktShowId)
                        .collect { result ->
                            nextState = result.fold(
                                { TrailerError(it.errorMessage) },
                                {
                                    TrailersContent(
                                        selectedVideoKey = it.toTrailerList().firstOrNull()?.key,
                                        trailersList = it.toTrailerList(),
                                    )
                                },
                            )
                        }
                    state.override { nextState }
                }
            }

            inState<TrailersContent> {
                on<TrailerSelected> { action, state ->
                    state.mutate {
                        copy(selectedVideoKey = action.trailerKey)
                    }
                }

                collectWhileInState(repository.observeTrailersByShowId(traktShowId)) { result, state ->
                    result.fold(
                        { state.override { TrailerError(it.errorMessage) } },
                        { state.mutate { copy(trailersList = it.toTrailerList()) } },
                    )
                }

                on<ReloadTrailers> { _, state ->
                    reloadTrailers(state)
                }
            }
        }
    }

    private suspend fun loadTrailers(state: State<LoadingTrailers>): ChangedState<TrailersState> {
        var trailerState: TrailersState = LoadingTrailers
        repository.observeTrailersByShowId(traktShowId)
            .collect { result ->
                trailerState = result.fold(
                    {
                        TrailerError(it.errorMessage)
                    },
                    {
                        TrailersContent(
                            trailersList = it.toTrailerList(),
                            selectedVideoKey = it.toTrailerList().firstOrNull()?.key,
                        )
                    },
                )
            }
        return state.override { trailerState }
    }

    private suspend fun reloadTrailers(
        state: State<TrailersContent>,
    ): ChangedState<TrailersState> {
        var nextState: TrailersState = state.snapshot

        repository.observeTrailersByShowId(traktShowId)
            .collect { result ->
                nextState = result.fold(
                    { TrailerError(it.errorMessage) },
                    {
                        TrailersContent(
                            selectedVideoKey = it.toTrailerList().firstOrNull()?.key,
                            trailersList = it.toTrailerList(),
                        )
                    },
                )
            }
        return state.override { nextState }
    }
}
