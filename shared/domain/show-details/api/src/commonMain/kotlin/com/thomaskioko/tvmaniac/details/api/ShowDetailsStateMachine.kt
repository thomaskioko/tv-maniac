package com.thomaskioko.tvmaniac.details.api

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.details.api.SeasonState.SeasonsLoaded.Companion.EmptySeasons
import com.thomaskioko.tvmaniac.details.api.ShowDetailsState.ShowDetailsError
import com.thomaskioko.tvmaniac.details.api.ShowDetailsState.ShowDetailsLoaded
import com.thomaskioko.tvmaniac.details.api.SimilarShowsState.SimilarShowsLoaded.Companion.EmptyShows
import com.thomaskioko.tvmaniac.details.api.TrailersState.TrailersError
import com.thomaskioko.tvmaniac.details.api.TrailersState.TrailersLoaded
import com.thomaskioko.tvmaniac.details.api.TrailersState.TrailersLoaded.Companion.EmptyTrailers
import com.thomaskioko.tvmaniac.details.api.TrailersState.TrailersLoaded.Companion.playerErrorMessage
import com.thomaskioko.tvmaniac.domain.trailers.api.TrailerRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
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
    private val traktRepository: TraktRepository,
    private val similarShowsRepository: SimilarShowsRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val trailerRepository: TrailerRepository
) : FlowReduxStateMachine<ShowDetailsState, ShowDetailsAction>(
    initialState = ShowDetailsState.Loading
) {

    private var showId: MutableStateFlow<Int> = MutableStateFlow(0)

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
                                ?.copy(hasWebViewInstalled = result) ?: TrailersError(null)
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

            inState<ShowDetailsError> {
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

        traktRepository.observeShow(action.traktId)
            .catch {
                nextState = ShowDetailsError(it.resolveError())
            }
            .collect {
                nextState = when (it) {
                    is Resource.Error -> ShowDetailsError(it.errorMessage)
                    is Resource.Success -> ShowDetailsLoaded(
                        show = it.toTvShow(),
                        similarShowsState = EmptyShows,
                        seasonState = EmptySeasons,
                        trailerState = EmptyTrailers,
                        followShowState = FollowShowsState.Idle
                    )
                }
            }

        return state.override { nextState }
    }

    private suspend fun reloadShowData(
        action: ReloadShow,
        state: State<ShowDetailsError>
    ): ChangedState<ShowDetailsState> {
        showId.value = action.traktId
        var nextState: ShowDetailsState = ShowDetailsState.Loading

        traktRepository.observeShow(action.traktId)
            .catch {
                nextState = ShowDetailsError(it.resolveError())
            }
            .collect {
                nextState = when (it) {
                    is Resource.Error -> ShowDetailsError(it.errorMessage)
                    is Resource.Success -> ShowDetailsLoaded(
                        show = it.toTvShow(),
                        similarShowsState = EmptyShows,
                        seasonState = EmptySeasons,
                        trailerState = EmptyTrailers,
                        followShowState = FollowShowsState.Idle
                    )
                }
            }

        return state.override { nextState }
    }


    private suspend fun updateFollowState(
        action: FollowShow,
        state: State<ShowDetailsLoaded>
    ): ChangedState<ShowDetailsState> {

        var nextState: ChangedState<ShowDetailsState> = state.noChange()

        traktRepository.updateFollowedShow(
            traktId = action.traktId,
            addToWatchList = !action.addToWatchList
        )

        traktRepository.observeShow(action.traktId)
            .catch {
                nextState = state.mutate {
                    copy(
                        followShowState = FollowShowsState.FollowUpdateError(it.resolveError())
                    )
                }
            }
            .collect {
                nextState = state.mutate {
                    copy(
                        show = it.toTvShow()
                    )
                }
            }

        return nextState
    }

    private suspend fun loadSeasons(
        showId: Int,
        state: State<ShowDetailsLoaded>
    ): ChangedState<ShowDetailsState> {
        var nextState: ChangedState<ShowDetailsState> = state.noChange()
        seasonDetailsRepository.observeShowSeasons(showId)
            .catch {
                nextState = state.mutate {
                    copy(
                        seasonState = SeasonState.SeasonsError(it.resolveError())
                    )
                }
            }
            .collect { resource ->
                nextState = when (resource) {
                    is Resource.Error -> state.mutate {
                        copy(
                            seasonState = SeasonState.SeasonsError(resource.errorMessage)
                        )
                    }

                    is Resource.Success -> state.mutate {
                        copy(
                            seasonState = (seasonState as SeasonState.SeasonsLoaded).copy(
                                isLoading = false,
                                seasonsList = resource.toSeasonsEntityList()
                            )
                        )
                    }
                }
            }

        return nextState
    }

    private suspend fun loadTrailers(
        showId: Int,
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
            .collect { resource ->
                nextState = when (resource) {
                    is Resource.Error -> state.mutate {
                        copy(
                            trailerState = TrailersError(resource.errorMessage)
                        )
                    }

                    is Resource.Success -> state.mutate {
                        copy(
                            trailerState = (trailerState as TrailersLoaded).copy(
                                isLoading = false,
                                trailersList = resource.toTrailerList()
                            )
                        )
                    }
                }
            }

        return nextState
    }

    private suspend fun loadSimilarShows(
        showId: Int,
        state: State<ShowDetailsLoaded>
    ): ChangedState<ShowDetailsState> {
        var nextState: ChangedState<ShowDetailsState> = state.noChange()
        similarShowsRepository.observeSimilarShows(showId)
            .catch {
                nextState = state.mutate {
                    copy(
                        similarShowsState = SimilarShowsState.SimilarShowsError(it.resolveError())
                    )
                }
            }
            .collect { resource ->
                nextState = when (resource) {
                    is Resource.Error -> state.mutate {
                        copy(
                            similarShowsState = SimilarShowsState.SimilarShowsError(resource.errorMessage)
                        )
                    }

                    is Resource.Success -> state.mutate {
                        copy(
                            similarShowsState = (similarShowsState as SimilarShowsState.SimilarShowsLoaded).copy(
                                isLoading = false,
                                similarShows = resource.toSimilarShowList()
                            )
                        )
                    }
                }
            }

        return nextState
    }
}

/**
 * A wrapper class around [ShowDetailsStateMachineWrapper] handling `Flow` and suspend functions on iOS.
 */
class ShowDetailsStateMachineWrapper(
    private val stateMachine: ShowDetailsStateMachine,
    dispatcher: MainCoroutineDispatcher,
) {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + dispatcher)

    fun dispatch(action: ShowDetailsAction) {
        scope.launch {
            stateMachine.dispatch(action)
        }
    }

    fun start(stateChangeListener: (ShowDetailsState) -> Unit) {
        scope.launch {
            stateMachine.state
                .collect { stateChangeListener(it) }
        }
    }

    fun cancel() {
        job.cancelChildren()
    }
}