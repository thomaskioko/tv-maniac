package com.thomaskioko.tvmaniac.presentation.discover

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.showimages.api.ShowImagesRepository
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import com.thomaskioko.tvmaniac.util.ExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class DiscoverStateMachine(
    private val exceptionHandler: ExceptionHandler,
    private val showsRepository: ShowsRepository,
    private val showImagesRepository: ShowImagesRepository,
) : FlowReduxStateMachine<DiscoverState, ShowsAction>(initialState = Loading) {

    init {
        spec {
            inState<Loading> {
                onEnter { state ->
                    fetchShowData(state)
                }
            }

            inState<DiscoverContent> {
                collectWhileInState(observeShowData()) { result, state ->
                    state.mutate {
                        copy(
                            contentState = result,
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

            inState<ContentError> {
                on<RetryLoading> { _, state ->
                    state.override { Loading }
                }
            }
        }
    }

    private suspend fun fetchShowData(state: State<Loading>): ChangedState<DiscoverContent> {
        val trendingResponse = showsRepository.fetchShows(Category.TRENDING)
        val recommendedResponse = showsRepository.fetchShows(Category.RECOMMENDED)
        val popularResponse = showsRepository.fetchShows(Category.POPULAR)
        val anticipatedResponse = showsRepository.fetchShows(Category.ANTICIPATED)

        return state.override {
            DiscoverContent(
                contentState = DiscoverContent.DataLoaded(
                    trendingShows = trendingResponse.toTvShowList(),
                    popularShows = popularResponse.toTvShowList(),
                    anticipatedShows = anticipatedResponse.toTvShowList(),
                    recommendedShows = recommendedResponse.toTvShowList().take(5),
                ),
            )
        }
    }

    private fun observeShowData(): Flow<DiscoverContent.DiscoverContentState> =
        combine(
            showsRepository.observeTrendingShows(),
            showsRepository.observePopularShows(),
            showsRepository.observeAnticipatedShows(),
            showsRepository.observeFeaturedShows(),
        ) { trending, popular, anticipated, featured ->
            toShowResultState(trending, popular, anticipated, featured)
        }
            .catch {
                ContentError(exceptionHandler.resolveError(it))
            }
}
