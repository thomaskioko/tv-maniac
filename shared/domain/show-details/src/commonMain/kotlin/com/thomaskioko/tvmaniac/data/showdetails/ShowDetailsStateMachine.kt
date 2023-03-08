package com.thomaskioko.tvmaniac.data.showdetails

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.data.showdetails.SeasonState.SeasonsLoaded.Companion.EmptySeasons
import com.thomaskioko.tvmaniac.data.showdetails.ShowDetailsState.ShowDetailsLoaded
import com.thomaskioko.tvmaniac.data.showdetails.SimilarShowsState.SimilarShowsLoaded.Companion.EmptyShows
import com.thomaskioko.tvmaniac.data.showdetails.TrailersState.TrailersError
import com.thomaskioko.tvmaniac.data.showdetails.TrailersState.TrailersLoaded
import com.thomaskioko.tvmaniac.data.showdetails.TrailersState.TrailersLoaded.Companion.EmptyTrailers
import com.thomaskioko.tvmaniac.data.showdetails.TrailersState.TrailersLoaded.Companion.playerErrorMessage
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktShowRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class ShowDetailsStateMachine constructor(
    private val traktShowRepository: TraktShowRepository,
    private val similarShowsRepository: SimilarShowsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val trailerRepository: TrailerRepository
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

                collectWhileInState(trailerRepository.isWebViewInstalled()) { result, state ->
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

        traktShowRepository.observeShow(action.traktId)
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

        traktShowRepository.observeShow(action.traktId)
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

        traktShowRepository.updateFollowedShow(
            traktId = action.traktId,
            addToWatchList = !action.addToWatchList
        )

        traktShowRepository.observeShow(action.traktId)
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
                        trailerState = TrailersError(it.resolveError())
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
                    copy(similarShowsState = SimilarShowsState.SimilarShowsError(it.resolveError()))
                }
            }
            .collect { result ->

                nextState = result.fold(
                    {
                        state.mutate {
                            copy(similarShowsState = SimilarShowsState.SimilarShowsError(it.errorMessage))
                        }
                    },
                    {
                        state.mutate {
                            copy(
                                similarShowsState = (similarShowsState as SimilarShowsState.SimilarShowsLoaded)
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

/**
 * A wrapper class around [ShowDetailsStateMachineWrapper] handling `Flow` and suspend functions on iOS.
 */
class ShowDetailsStateMachineWrapper(
    dispatcher: MainCoroutineDispatcher,
    private val stateMachine: ShowDetailsStateMachine,
    ) {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + dispatcher)

    fun start(stateChangeListener: (ShowDetailsState) -> Unit) {
        scope.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(action: ShowDetailsAction) {
        scope.launch {
            stateMachine.dispatch(action)
        }
    }

    fun cancel() {
        job.cancelChildren()
    }
}