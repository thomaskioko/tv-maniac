package com.thomaskioko.tvmaniac.presentation.trailers

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class TrailersStateMachine(
    @Assisted private val traktShowId: Long,
    private val repository: TrailerRepository,
) : FlowReduxStateMachine<TrailersState, TrailersAction>(initialState = LoadingTrailers) {

    init {
        spec {
            inState<LoadingTrailers> {
                onEnter { state ->
                    val result = repository.fetchTrailersByShowId(traktShowId)

                    state.override {
                        TrailersContent(
                            selectedVideoKey = result.toTrailerList().firstOrNull()?.key,
                            trailersList = result.toTrailerList(),
                        )
                    }
                }

                untilIdentityChanges({ state -> state }) {
                    collectWhileInState(repository.observeTrailersStoreResponse(traktShowId)) { response, state ->
                        when (response) {
                            is Either.Left -> {
                                state.override { TrailerError(response.error.errorMessage) }
                            }

                            is Either.Right -> {
                                state.override {
                                    TrailersContent(
                                        selectedVideoKey = response.data.toTrailerList()
                                            .firstOrNull()?.key,
                                        trailersList = response.data.toTrailerList(),
                                    )
                                }
                            }
                        }
                    }
                }
                on<VideoPlayerError> { action, state ->
                    state.override { TrailerError(action.errorMessage) }
                }
            }

            inState<TrailersContent> {
                on<TrailerSelected> { action, state ->
                    state.mutate {
                        copy(selectedVideoKey = action.trailerKey)
                    }
                }

                collectWhileInState(repository.observeTrailersStoreResponse(traktShowId)) { response, state ->
                    when (response) {
                        is Either.Left -> {
                            state.override { TrailerError(response.error.errorMessage) }
                        }

                        is Either.Right -> {
                            state.mutate {
                                copy(
                                    selectedVideoKey = response.data.toTrailerList()
                                        .firstOrNull()?.key,
                                    trailersList = response.data.toTrailerList(),
                                )
                            }
                        }
                    }
                }

                on<ReloadTrailers> { _, state ->
                    state.override { LoadingTrailers }
                }
            }

            inState<TrailerError> {

                on<ReloadTrailers> { _, state ->
                    state.override { LoadingTrailers }
                }
            }
        }
    }
}
