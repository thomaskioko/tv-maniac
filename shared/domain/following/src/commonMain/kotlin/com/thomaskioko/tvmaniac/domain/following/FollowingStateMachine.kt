package com.thomaskioko.tvmaniac.domain.following

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
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

/**
 * A wrapper class around [FollowingStateMachine] handling `Flow` and suspend functions on iOS.
 */
@Inject
class FollowingStateMachineWrapper(
    private val scope: AppCoroutineScope,
    private val stateMachine: FollowingStateMachine,
) {

    fun start(stateChangeListener: (FollowingState) -> Unit) {
        scope.main.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(action: FollowingAction) {
        scope.main.launch {
            stateMachine.dispatch(action)
        }
    }

    fun cancel() {
        scope.main.cancel()
    }
}

