package com.thomaskioko.tvmaniac.presentation.trailers

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadResponse

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
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
                        is StoreReadResponse.Loading -> state.override { LoadingTrailers }
                        is StoreReadResponse.NoNewData -> state.noChange()
                        is StoreReadResponse.Data -> {
                            state.mutate {
                                copy(
                                    selectedVideoKey = response.requireData().toTrailerList()
                                        .firstOrNull()?.key,
                                    trailersList = response.requireData().toTrailerList(),
                                )
                            }
                        }

                        is StoreReadResponse.Error.Exception -> {
                            state.override { TrailerError("") }
                        }

                        is StoreReadResponse.Error.Message -> {
                            state.override { TrailerError(response.message) }
                        }
                    }
                }
            }

            inState<TrailerError> {

                on<ReloadTrailers> { _, state ->
                    reloadTrailers(state)
                }
            }
        }
    }

    private suspend fun reloadTrailers(state: State<TrailerError>): ChangedState<TrailersState> {
        var trailerState: ChangedState<TrailersState> = state.override { LoadingTrailers }
        repository.observeTrailersStoreResponse(traktShowId)
            .collect { response ->
                trailerState = when (response) {
                    is StoreReadResponse.Loading -> state.override { LoadingTrailers }
                    is StoreReadResponse.NoNewData -> state.noChange()
                    is StoreReadResponse.Data -> {
                        state.override {
                            TrailersContent(
                                selectedVideoKey = response.requireData().toTrailerList()
                                    .firstOrNull()?.key,
                                trailersList = response.requireData().toTrailerList(),
                            )
                        }
                    }

                    is StoreReadResponse.Error.Exception -> {
                        state.override { TrailerError("") }
                    }

                    is StoreReadResponse.Error.Message -> {
                        state.override { TrailerError(response.message) }
                    }
                }
            }
        return trailerState
    }
}
