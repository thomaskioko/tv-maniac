package com.thomaskioko.tvmaniac.presentation.discover

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.showimages.api.ShowImagesRepository
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import me.tatarka.inject.annotations.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Inject
class DiscoverStateMachine(
    private val showsRepository: ShowsRepository,
    private val showImagesRepository: ShowImagesRepository,
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
                            ),
                        )
                    }
                }

                collectWhileInStateEffect(showImagesRepository.updateShowArtWork()) { _, _ ->
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
