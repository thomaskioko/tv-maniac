package com.thomaskioko.tvmaniac.shared.domain.discover

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import com.thomaskioko.tvmaniac.tmdb.api.TmdbRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Inject
class DiscoverStateMachine(
    private val showsRepository: ShowsRepository,
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

                on<ReloadCategory> { _, state ->
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
            showsRepository.fetchTrendingShows(),
            showsRepository.fetchPopularShows(),
            showsRepository.fetchAnticipatedShows(),
            showsRepository.fetchFeaturedShows(),
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
            showsRepository.observeTrendingCachedShows(),
            showsRepository.observePopularCachedShows(),
            showsRepository.observeAnticipatedCachedShows(),
            showsRepository.observeFeaturedCachedShows(),
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
 * A wrapper class around [DiscoverStateMachine] handling `Flow` and suspend functions on iOS.
 */
@Inject
class DiscoverStateMachineWrapper(
    private val scope: AppCoroutineScope,
    private val stateMachine: DiscoverStateMachine,
) {

    fun start(stateChangeListener: (ShowsState) -> Unit) {
        scope.main.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(action: ShowsAction) {
        scope.main.launch {
            stateMachine.dispatch(action)
        }
    }

    fun cancel() {
        scope.main.cancel()
    }

}