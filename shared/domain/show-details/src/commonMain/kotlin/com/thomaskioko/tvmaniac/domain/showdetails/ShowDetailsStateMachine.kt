package com.thomaskioko.tvmaniac.domain.showdetails

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.util.ExceptionHandler
import com.thomaskioko.tvmaniac.domain.showdetails.SeasonState.SeasonsLoaded.Companion.EmptySeasons
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsState.ShowDetailsLoaded
import com.thomaskioko.tvmaniac.domain.showdetails.SimilarShowsState.SimilarShowsError
import com.thomaskioko.tvmaniac.domain.showdetails.SimilarShowsState.SimilarShowsLoaded
import com.thomaskioko.tvmaniac.domain.showdetails.SimilarShowsState.SimilarShowsLoaded.Companion.EmptyShows
import com.thomaskioko.tvmaniac.domain.showdetails.TrailersState.TrailersError
import com.thomaskioko.tvmaniac.domain.showdetails.TrailersState.TrailersLoaded
import com.thomaskioko.tvmaniac.domain.showdetails.TrailersState.TrailersLoaded.Companion.EmptyTrailers
import com.thomaskioko.tvmaniac.domain.showdetails.TrailersState.TrailersLoaded.Companion.playerErrorMessage
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.shows.api.ShowsRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import me.tatarka.inject.annotations.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Inject
class ShowDetailsStateMachine constructor(
    private val showsRepository: ShowsRepository,
    private val similarShowsRepository: SimilarShowsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val trailerRepository: TrailerRepository,
    private val exceptionHandler: ExceptionHandler
) : FlowReduxStateMachine<ShowDetailsState, ShowDetailsAction>(
    initialState = ShowDetailsState.Loading
) {

    private var showId: MutableStateFlow<Long> = MutableStateFlow(0)

    init {
        spec {
            inState<ShowDetailsState.Loading> {
                on<LoadShowDetails> { action, state ->
                    fetchShowData(action, state)
                }
            }

            inState<ShowDetailsLoaded> {

                collectWhileInState(showId) { id, state ->
                    loadSeasons(id, state)
                }

                collectWhileInState(showId) { id, state ->
                    loadTrailers(id, state)
                }

                collectWhileInState(showId) { id, state ->
                    loadSimilarShows(id, state)
                }

                collectWhileInState(trailerRepository.isYoutubePlayerInstalled()) { result, state ->
                    state.mutate {
                        copy(
                            trailerState = (trailerState as? TrailersLoaded)
                                ?.copy(hasWebViewInstalled = result) ?: TrailersError(
                                null
                            )
                        )
                    }
                }

                on<FollowShow> { action, state ->
                    updateFollowState(action, state)
                }

                on<WebViewError> { _, state ->
                    state.mutate {
                        copy(
                            trailerState = (trailerState as TrailersLoaded)
                                .copy(playerErrorMessage = playerErrorMessage)
                        )
                    }
                }

                on<DismissWebViewError> { _, state ->
                    state.mutate {
                        copy(
                            trailerState = (trailerState as TrailersLoaded)
                                .copy(playerErrorMessage = null)
                        )
                    }
                }
            }

            inState<ShowDetailsState.ShowDetailsError> {
                on<ReloadShow> { action, state ->
                    reloadShowData(action, state)
                }
            }
        }
    }

    private suspend fun fetchShowData(
        action: LoadShowDetails,
        state: State<ShowDetailsState.Loading>
    ): ChangedState<ShowDetailsState> {
        showId.value = action.traktId
        var nextState: ShowDetailsState = ShowDetailsState.Loading

        showsRepository.observeShow(action.traktId)
            .collect { result ->
                nextState = result.fold(
                    {
                        ShowDetailsState.ShowDetailsError(it.errorMessage)
                    },
                    {
                        ShowDetailsLoaded(
                            showState = result.toShowState(),
                            similarShowsState = EmptyShows,
                            seasonState = EmptySeasons,
                            trailerState = EmptyTrailers,
                            followShowState = FollowShowsState.Idle
                        )
                    }
                )
            }

        return state.override { nextState }
    }

    private suspend fun reloadShowData(
        action: ReloadShow,
        state: State<ShowDetailsState.ShowDetailsError>
    ): ChangedState<ShowDetailsState> {
        showId.value = action.traktId
        var nextState: ShowDetailsState = ShowDetailsState.Loading

        showsRepository.observeShow(action.traktId)
            .collect { result ->
                nextState = result.fold(
                    { ShowDetailsState.ShowDetailsError(it.errorMessage) },
                    {
                        ShowDetailsLoaded(
                            showState = result.toShowState(),
                            similarShowsState = EmptyShows,
                            seasonState = EmptySeasons,
                            trailerState = EmptyTrailers,
                            followShowState = FollowShowsState.Idle
                        )
                    })
            }

        return state.override { nextState }
    }

    private suspend fun updateFollowState(
        action: FollowShow,
        state: State<ShowDetailsLoaded>
    ): ChangedState<ShowDetailsState> {

        var nextState: ChangedState<ShowDetailsState> = state.noChange()

        showsRepository.updateFollowedShow(
            traktId = action.traktId,
            addToWatchList = !action.addToWatchList
        )

        showsRepository.observeShow(action.traktId)
            .collect {
                nextState = it.fold(
                    {
                        state.mutate {
                            copy(
                                followShowState = FollowShowsState.FollowUpdateError(it.errorMessage)
                            )
                        }
                    },
                    {
                        state.mutate {
                            copy(
                                showState = (showState as ShowState.ShowLoaded)
                                    .copy(show = it.toTvShow())
                            )
                        }
                    }
                )
            }

        return nextState
    }

    private suspend fun loadSeasons(
        showId: Long,
        state: State<ShowDetailsLoaded>
    ): ChangedState<ShowDetailsState> {
        var nextState: ChangedState<ShowDetailsState> = state.noChange()
        seasonDetailsRepository.observeSeasonsStream(showId)
            .collect { result ->
                nextState = result.fold(
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
                                    seasonsList = it.toSeasonsList()
                                )
                            )
                        }
                    }
                )
            }

        return nextState
    }

    private suspend fun loadTrailers(
        showId: Long,
        state: State<ShowDetailsLoaded>
    ): ChangedState<ShowDetailsState> {
        var nextState: ChangedState<ShowDetailsState> = state.noChange()
        trailerRepository.observeTrailersByShowId(showId)
            .catch {
                nextState = state.mutate {
                    copy(
                        trailerState = TrailersError(exceptionHandler.resolveError(it))
                    )
                }
            }
            .collect { result ->
                nextState = result.fold(
                    {
                        state.mutate {
                            copy(trailerState = TrailersError(it.errorMessage))
                        }
                    },
                    {
                        state.mutate {
                            copy(
                                trailerState = (trailerState as TrailersLoaded).copy(
                                    isLoading = false,
                                    trailersList = it.toTrailerList()
                                )
                            )
                        }
                    }
                )
            }

        return nextState
    }

    private suspend fun loadSimilarShows(
        showId: Long,
        state: State<ShowDetailsLoaded>
    ): ChangedState<ShowDetailsState> {
        var nextState: ChangedState<ShowDetailsState> = state.noChange()
        similarShowsRepository.observeSimilarShows(showId)
            .catch {
                nextState = state.mutate {
                    copy(similarShowsState = SimilarShowsError(exceptionHandler.resolveError(it)))
                }
            }
            .collect { result ->

                nextState = result.fold(
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
                                        similarShows = it.toSimilarShowList()
                                    )
                            )
                        }
                    }
                )
            }

        return nextState
    }
}