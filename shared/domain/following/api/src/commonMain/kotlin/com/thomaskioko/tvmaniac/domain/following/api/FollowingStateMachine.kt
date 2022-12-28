package com.thomaskioko.tvmaniac.domain.following.api

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.core.db.SelectFollowedShows
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class FollowingStateMachine constructor(
    private val repository: TraktRepository,
) : FlowReduxStateMachine<FollowingState, FollowingAction>(initialState = LoadingShows) {

    init {
        spec {
            inState<LoadingShows> {
                onEnter { state -> loadFollowedShows(state) }
            }

            inState<FollowingContent> {
                collectWhileInState(repository.observeFollowedShows()) { result, state ->
                    when (result) {
                        is Either.Left -> state.override { ErrorLoadingShows(result.error.errorMessage) }
                        is Either.Right -> state.mutate { copy(list = result.toTvShowList()) }
                    }
                }
            }

            inState<ErrorLoadingShows> {
                on<ReloadFollowedShows> { _, state ->
                    state.override { LoadingShows }
                }
            }
        }
    }

    private  fun loadFollowedShows(state: State<LoadingShows>): ChangedState<FollowingState> {

        val result = repository.getFollowedShows()

        return state.override { FollowingContent(result.toTvShowList()) }
    }
}

/**
 * A wrapper class around [FollowingStateMachine] handling `Flow` and suspend functions on iOS.
 */
class FollowingStateMachineWrapper(
    private val stateMachine: FollowingStateMachine,
    dispatcher: MainCoroutineDispatcher,
) {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + dispatcher)

    fun dispatch(action: FollowingAction) {
        scope.launch {
            stateMachine.dispatch(action)
        }
    }

    fun start(stateChangeListener: (FollowingState) -> Unit) {
        scope.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun cancel() {
        job.cancelChildren()
    }
}

fun Either.Right<List<SelectFollowedShows>>.toTvShowList(): List<FollowedShow> {
    return data?.map {
        FollowedShow(
            traktId = it.id,
            tmdbId = it.tmdb_id,
            title = it.title,
            posterImageUrl = it.poster_url,
        )
    } ?: emptyList()
}

fun List<SelectFollowedShows>.toTvShowList(): List<FollowedShow> {
    return map {
        FollowedShow(
            traktId = it.id,
            tmdbId = it.tmdb_id,
            title = it.title,
            posterImageUrl = it.poster_url,
        )
    }
}