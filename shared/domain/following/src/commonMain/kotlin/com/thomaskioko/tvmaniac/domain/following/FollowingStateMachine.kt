package com.thomaskioko.tvmaniac.domain.following

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.tatarka.inject.annotations.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Inject
class FollowingStateMachine(
    private val repository: ShowsRepository,
) : FlowReduxStateMachine<FollowingState, FollowingAction>(initialState = LoadingShows) {

    init {
        spec {
            inState<LoadingShows> {
                onEnter { state -> loadFollowedShows(state) }
            }

            inState<FollowingContent> {
                collectWhileInState(repository.observeFollowedShows()) { result, state ->
                    result.fold(
                        { state.override { ErrorLoadingShows(it.errorMessage) } },
                        { state.mutate { copy(list = it.followedShowList()) } }
                    )
                }
            }

            inState<ErrorLoadingShows> {
                on<ReloadFollowedShows> { _, state ->
                    state.override { LoadingShows }
                }
            }
        }
    }

    private fun loadFollowedShows(state: State<LoadingShows>): ChangedState<FollowingState> {

        val result = repository.getFollowedShows()

        return state.override { FollowingContent(result.followedShowList()) }
    }
}

