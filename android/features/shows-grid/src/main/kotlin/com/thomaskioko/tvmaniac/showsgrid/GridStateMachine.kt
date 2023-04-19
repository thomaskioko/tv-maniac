package com.thomaskioko.tvmaniac.showsgrid

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.tatarka.inject.annotations.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Inject
class GridStateMachine(
    private val repository: ShowsRepository,
) : FlowReduxStateMachine<GridState, GridActions>(initialState = LoadingContent) {

    init {
        spec {
            inState<LoadingContent> {
                on<LoadShows> { action, state ->
                    loadShowData(state, action)
                }
            }

            inState<ShowsLoaded> {
            }

            inState<LoadingContentError> {
                onActionEffect<ReloadShows> { action, _ ->
                    dispatch(LoadShows(action.category))
                }
            }
        }
    }

    private suspend fun loadShowData(
        state: State<LoadingContent>,
        action: LoadShows,
    ): ChangedState<GridState> {
        var nextState: ChangedState<GridState> = state.noChange()
        repository.observeCachedShows(action.category)
            .collect { result ->
                nextState = when (result) {
                    is Either.Left -> state.override { LoadingContentError(result.error.errorMessage) }
                    is Either.Right -> state.override {
                        ShowsLoaded(
                            list = result.data?.toTvShowList() ?: emptyList(),
                        )
                    }
                }
            }

        return nextState
    }
}
