package com.thomaskioko.tvmaniac.presentation.showdetails

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.core.db.Seasons
import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.presentation.showdetails.SeasonState.SeasonsLoaded.Companion.EmptySeasons
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsState.ShowDetailsLoaded
import com.thomaskioko.tvmaniac.presentation.showdetails.SimilarShowsState.SimilarShowsError
import com.thomaskioko.tvmaniac.presentation.showdetails.SimilarShowsState.SimilarShowsLoaded
import com.thomaskioko.tvmaniac.presentation.showdetails.SimilarShowsState.SimilarShowsLoaded.Companion.EmptyShows
import com.thomaskioko.tvmaniac.presentation.showdetails.TrailersState.TrailersError
import com.thomaskioko.tvmaniac.presentation.showdetails.TrailersState.TrailersLoaded
import com.thomaskioko.tvmaniac.presentation.showdetails.TrailersState.TrailersLoaded.Companion.EmptyTrailers
import com.thomaskioko.tvmaniac.presentation.showdetails.TrailersState.TrailersLoaded.Companion.playerErrorMessage
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadResponse

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Inject
class ShowDetailsStateMachine constructor(
    @Assisted private val traktShowId: Long,
    private val showsRepository: ShowsRepository,
    private val similarShowsRepository: SimilarShowsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val trailerRepository: TrailerRepository,
) : FlowReduxStateMachine<ShowDetailsState, ShowDetailsAction>(
    initialState = ShowDetailsState.Loading,
) {

    init {
        spec {
            inState<ShowDetailsState.Loading> {
                onEnter { state ->
                    fetchShowDetails(state)
                }
            }

            inState<ShowDetailsLoaded> {

                collectWhileInState(seasonDetailsRepository.observeSeasonsStream(traktShowId)) { result, state ->
                    updateShowDetailsState(result, state)
                }

                collectWhileInState(trailerRepository.observeTrailersStoreResponse(traktShowId)) { response, state ->
                    updateTrailerState(response, state)
                }

                collectWhileInState(similarShowsRepository.observeSimilarShows(traktShowId)) { result, state ->
                    updateSimilarShowsState(result, state)
                }

                collectWhileInState(trailerRepository.isYoutubePlayerInstalled()) { result, state ->
                    state.mutate {
                        copy(
                            trailerState = (trailerState as? TrailersLoaded)
                                ?.copy(hasWebViewInstalled = result) ?: TrailersError(
                                null,
                            ),
                        )
                    }
                }

                onActionEffect<FollowShowClicked> { action, _ ->
                    showsRepository.updateFollowedShow(
                        traktId = action.traktId,
                        addToWatchList = !action.addToFollowed,
                    )
                }

                on<WebViewError> { _, state ->
                    state.mutate {
                        copy(
                            trailerState = (trailerState as? TrailersLoaded)?.copy(
                                playerErrorMessage = playerErrorMessage,
                            ) ?: TrailersError(playerErrorMessage),
                        )
                    }
                }

                on<DismissWebViewError> { _, state ->
                    state.mutate {
                        copy(
                            trailerState = (trailerState as TrailersLoaded)
                                .copy(playerErrorMessage = null),
                        )
                    }
                }
            }

            inState<ShowDetailsState.ShowDetailsError> {
                on<ReloadShowDetails> { action, state ->
                    reloadShowData(action, state)
                }
            }
        }
    }

    private fun updateSimilarShowsState(
        result: Either<Failure, List<SelectSimilarShows>>,
        state: State<ShowDetailsLoaded>,
    ) = result.fold(
        {
            state.mutate {
                copy(similarShowsState = SimilarShowsError(it.errorMessage))
            }
        },
        {
            state.mutate {
                copy(
                    similarShowsState = (similarShowsState as SimilarShowsLoaded)
                        .copy(
                            isLoading = false,
                            similarShows = it.toSimilarShowList(),
                        ),
                )
            }
        },
    )

    private fun updateTrailerState(
        response: StoreReadResponse<List<Trailers>>,
        state: State<ShowDetailsLoaded>,
    ) = when (response) {
        is StoreReadResponse.NoNewData -> state.noChange()
        is StoreReadResponse.Loading -> state.mutate {
            copy(
                trailerState = (trailerState as TrailersLoaded).copy(
                    isLoading = true,
                ),
            )
        }
        is StoreReadResponse.Data -> {
            state.mutate {
                copy(
                    trailerState = (trailerState as TrailersLoaded).copy(
                        isLoading = false,
                        trailersList = response.requireData().toTrailerList(),
                    ),
                )
            }
        }

        is StoreReadResponse.Error.Exception -> {
            state.mutate {
                copy(trailerState = TrailersError(response.error.message))
            }
        }

        is StoreReadResponse.Error.Message -> {
            state.mutate {
                copy(trailerState = TrailersError(response.message))
            }
        }
    }

    private fun updateShowDetailsState(
        result: Either<Failure, List<Seasons>>,
        state: State<ShowDetailsLoaded>,
    ) = result.fold(
        {
            state.mutate {
                copy(seasonState = SeasonState.SeasonsError(it.errorMessage))
            }
        },
        {
            state.mutate {
                copy(
                    seasonState = (seasonState as SeasonState.SeasonsLoaded).copy(
                        isLoading = false,
                        seasonsList = it.toSeasonsList(),
                    ),
                )
            }
        },
    )

    private suspend fun fetchShowDetails(state: State<ShowDetailsState.Loading>): ChangedState<ShowDetailsState> {
        var detailState: ShowDetailsState = ShowDetailsState.Loading

        showsRepository.observeShow(traktShowId)
            .collect { result ->
                detailState = result.fold(
                    {
                        ShowDetailsState.ShowDetailsError(it.errorMessage)
                    },
                    {
                        ShowDetailsLoaded(
                            showState = result.toShowState(),
                            similarShowsState = EmptyShows,
                            seasonState = EmptySeasons,
                            trailerState = EmptyTrailers,
                            followShowState = FollowShowsState.Idle,
                        )
                    },
                )
            }

        return state.override { detailState }
    }

    private suspend fun reloadShowData(
        action: ReloadShowDetails,
        state: State<ShowDetailsState.ShowDetailsError>,
    ): ChangedState<ShowDetailsState> {
        var detailState: ShowDetailsState = ShowDetailsState.Loading

        showsRepository.observeShow(action.traktId)
            .collect { result ->
                detailState = result.fold(
                    { ShowDetailsState.ShowDetailsError(it.errorMessage) },
                    {
                        ShowDetailsLoaded(
                            showState = result.toShowState(),
                            similarShowsState = EmptyShows,
                            seasonState = EmptySeasons,
                            trailerState = EmptyTrailers,
                            followShowState = FollowShowsState.Idle,
                        )
                    },
                )
            }

        return state.override { detailState }
    }
}
