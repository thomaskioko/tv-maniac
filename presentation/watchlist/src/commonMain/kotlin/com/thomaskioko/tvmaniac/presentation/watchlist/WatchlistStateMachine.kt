package com.thomaskioko.tvmaniac.presentation.watchlist

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.tatarka.inject.annotations.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Inject
class WatchlistStateMachine(
    private val repository: WatchlistRepository,
) : FlowReduxStateMachine<WatchlistState, WatchlistAction>(initialState = LoadingShows) {

    init {
        spec {
            inState<LoadingShows> {
                onEnter { state ->
                    val result = repository.getWatchlist()

                    state.override { WatchlistContent(result.entityToWatchlist()) }
                }
            }

            inState<WatchlistContent> {
                collectWhileInState(repository.observeWatchList()) { result, state ->
                    result.fold(
                        { state.override { ErrorLoadingShows(it.errorMessage) } },
                        { state.mutate { copy(list = it.entityToWatchlist()) } },
                    )
                }
            }

            inState<ErrorLoadingShows> {
                on<ReloadWatchlist> { _, state ->
                    state.override { LoadingShows }
                }
            }
        }
    }
}
