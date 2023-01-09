package com.thomaskioko.tvmaniac.shows.api

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.tmdb.api.TmdbRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


class ShowsStateMachine constructor(
    private val traktRepository: TraktRepository,
    private val tmdbRepository: TmdbRepository
) : FlowReduxStateMachine<ShowsState, ShowsAction>(initialState = Loading) {

    init {
        spec {
            inState<Loading> {
                onEnter { fetchShowData(it) }
            }

            inState<ShowsLoaded> {
                collectWhileInState(observeShowData()) { result, state ->
                    state.mutate {
                        copy(
                            result = result.copy(
                                featuredCategoryState = result.featuredCategoryState,
                                trendingCategoryState = result.trendingCategoryState,
                                popularCategoryState = result.popularCategoryState,
                                anticipatedCategoryState = result.anticipatedCategoryState,
                            )
                        )
                    }
                }

                collectWhileInStateEffect(tmdbRepository.updateShowArtWork()) { _, _ ->
                    /** No need to do anything. Just trigger artwork download. **/
                }

                on<ReloadCategory> { action, state ->
                    // TODO:: Implement reloading category data
                    state.noChange()
                }

            }

            inState<LoadingError> {
                on<RetryLoading> { _, state ->
                    state.override { Loading }
                }
            }
        }
    }

    private suspend fun fetchShowData(state: State<Loading>): ChangedState<ShowsState> {
        var nextState: ShowsState = state.snapshot

        combine(
            traktRepository.fetchTrendingShows(),
            traktRepository.fetchPopularShows(),
            traktRepository.fetchAnticipatedShows(),
            traktRepository.fetchFeaturedShows(),
        ) { trending, popular, anticipated, featured ->

            ShowResult(
                trendingCategoryState = trending.toShowData(),
                popularCategoryState = popular.toShowData(),
                anticipatedCategoryState = anticipated.toShowData(),
                featuredCategoryState = featured.toShowData(5),
            )
        }
            .catch { nextState = LoadingError(it.message ?: "Something went wrong") }
            .collect {
                nextState = ShowsLoaded(result = it)
            }

        return state.override { nextState }
    }

    private fun observeShowData(): Flow<ShowResult> =
        combine(
            traktRepository.observeTrendingCachedShows(),
            traktRepository.observePopularCachedShows(),
            traktRepository.observeAnticipatedCachedShows(),
            traktRepository.observeFeaturedCachedShows(),
        ) { trending, popular, anticipated, featured ->

            ShowResult(
                trendingCategoryState = trending.toShowData(),
                popularCategoryState = popular.toShowData(),
                anticipatedCategoryState = anticipated.toShowData(),
                featuredCategoryState = featured.toShowData(5),
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
}