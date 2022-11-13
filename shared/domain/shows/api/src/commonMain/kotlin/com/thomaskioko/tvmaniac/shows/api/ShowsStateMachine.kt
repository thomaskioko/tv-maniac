package com.thomaskioko.tvmaniac.shows.api

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory.ANTICIPATED
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory.FEATURED
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory.POPULAR
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory.TRENDING
import com.thomaskioko.tvmaniac.shows.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class ShowsStateMachine constructor(
    private val traktRepository: TraktRepository,
    private val tmdbRepository: TmdbRepository
) : FlowReduxStateMachine<ShowsState, ShowsAction>(initialState = FetchShows) {

    init {
        spec {
            inState<FetchShows> {
                onEnter { fetchShowData(it) }
            }

            inState<LoadShows> {
                collectWhileInState(observeShowData()) { result, state ->
                    state.override {
                        ShowsLoaded(
                            result = result,
                        )
                    }
                }
            }

            inState<ShowsLoaded> {
                collectWhileInState(observeShowData()) { result, state ->
                    state.mutate {
                        copy(
                            result = result.copy(
                                featuredShows = result.featuredShows,
                                trendingShows = result.trendingShows,
                                popularShows = result.popularShows,
                                anticipatedShows = result.anticipatedShows,
                                updateState = result.updateState
                            )
                        )
                    }
                }

                collectWhileInStateEffect(tmdbRepository.updateShowArtWork()) { _, _ ->
                    /** No need to do anything. Just trigger artwork download. **/
                }

            }

            inState<LoadingError> {
                on<RetryLoading> { _, state ->
                    state.override { FetchShows }
                }
            }
        }
    }

    private suspend fun fetchShowData(state: State<FetchShows>): ChangedState<ShowsState> {
        var nextState: ShowsState = state.snapshot

        combine(
            traktRepository.fetchShowsByCategoryId(TRENDING.id),
            traktRepository.fetchShowsByCategoryId(POPULAR.id),
            traktRepository.fetchShowsByCategoryId(ANTICIPATED.id),
            traktRepository.fetchShowsByCategoryId(FEATURED.id),
        ) { _, _, _, _ ->
        }
            .catch { nextState = LoadingError(it.message ?: "Something went wrong") }
            .collect {
                nextState = LoadShows
            }

        return state.override { nextState }
    }

    private fun observeShowData(): Flow<ShowResult> =
        combine(
            traktRepository.observeCachedShows(TRENDING.id),
            traktRepository.observeCachedShows(POPULAR.id),
            traktRepository.observeCachedShows(ANTICIPATED.id),
            traktRepository.observeCachedShows(FEATURED.id),
        ) { trending, popular, anticipated, featured ->

            val isEmpty = trending.isEmpty() && popular.isEmpty() && anticipated.isEmpty()
                    && featured.isEmpty()
            ShowResult(
                trendingShows = trending.toShowData(TRENDING),
                popularShows = popular.toShowData(POPULAR),
                anticipatedShows = anticipated.toShowData(ANTICIPATED),
                featuredShows = featured.toShowData(FEATURED, 5),
                updateState = if (isEmpty) ShowUpdateState.EMPTY else ShowUpdateState.IDLE
            )
        }
            .catch {
                LoadingError(it.message ?: "Something went wrong")
            }
}


/**
 * A wrapper class around [ShowsStateMachine] handling `Flow` and suspend functions on iOS.
 */
class ShowsStateMachineWrapper(
    private val stateMachine: ShowsStateMachine,
    private val scope: CoroutineScope,
) {
    fun dispatch(action: ShowsAction) {
        scope.launch {
            stateMachine.dispatch(action)
        }
    }

    fun start(stateChangeListener: (ShowsState) -> Unit) {
        scope.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }
}