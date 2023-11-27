package com.thomaskioko.tvmaniac.presentation.watchlist

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.thomaskioko.tvmaniac.shows.api.LibraryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.tatarka.inject.annotations.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Inject
class LibraryStateMachine(
    private val repository: LibraryRepository,
) : FlowReduxStateMachine<LibraryState, WatchlistAction>(initialState = LoadingShows) {

    init {
        spec {
            inState<LoadingShows> {
                onEnter { state ->
                    val result = repository.getLibraryShows()

                    state.override { LibraryContent(result.entityToWatchlist()) }
                }
            }

            inState<LibraryContent> {
                collectWhileInState(repository.observeLibrary()) { result, state ->
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
