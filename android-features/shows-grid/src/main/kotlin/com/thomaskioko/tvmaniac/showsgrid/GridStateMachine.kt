package com.thomaskioko.tvmaniac.showsgrid

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.shows.api.DiscoverRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadResponse

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Inject
class GridStateMachine(
    private val repository: DiscoverRepository,
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
        repository.observeShowsByCategory(action.category)
            .collect { result ->
                nextState = when (result) {
                    is StoreReadResponse.NoNewData -> state.noChange()
                    is StoreReadResponse.Loading -> state.override { LoadingContent }
                    is StoreReadResponse.Data -> state.override {
                        ShowsLoaded(
                            list = result.requireData().toTvShowList(),
                        )
                    }
                    is StoreReadResponse.Error.Exception -> state.override {
                        LoadingContentError(result.error.message)
                    }
                    is StoreReadResponse.Error.Message -> state.override {
                        LoadingContentError(result.message)
                    }
                }
            }

        return nextState
    }
}
