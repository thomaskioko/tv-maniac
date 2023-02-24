package com.thomaskioko.tvmaniac.details.api

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.core.db.SelectByShowId
import com.thomaskioko.tvmaniac.core.db.SelectSimilarShows
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.util.ExceptionHandler.resolveError
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import com.thomaskioko.tvmaniac.details.api.ShowDetailsState.ShowDetailsError
import com.thomaskioko.tvmaniac.details.api.ShowDetailsState.ShowDetailsLoaded
import com.thomaskioko.tvmaniac.details.api.TrailersState.TrailersError
import com.thomaskioko.tvmaniac.details.api.TrailersState.TrailersLoaded
import com.thomaskioko.tvmaniac.details.api.TrailersState.TrailersLoaded.Companion.playerErrorMessage
import com.thomaskioko.tvmaniac.domain.trailers.api.TrailerRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktShowRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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

    init {
        spec {
            inState<ShowDetailsState.Loading> {
                on<LoadShowDetails> { action, state ->
                    loadShowData(action, state)
                }
            }

            inState<ShowDetailsLoaded> {

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
                    state.override { ShowDetailsState.Loading }
                }
            }
        }
    }

    private suspend fun loadShowData(
        action: LoadShowDetails,
        state: State<ShowDetailsState.Loading>
    ): ChangedState<ShowDetailsState> {
        var nextState: ShowDetailsState = ShowDetailsState.Loading

        combine(
            traktShowRepository.observeShow(action.traktId),
            seasonDetailsRepository.observeSeasons(action.traktId),
            trailerRepository.observeTrailersByShowId(action.traktId),
            similarShowsRepository.observeSimilarShows(action.traktId)
        ) { show, seasons, trailers, similarShows ->

            ShowDetailsLoaded(
                showState = show.toShowState(),
                seasonState = seasons.toSeasonState(),
                trailerState = trailers.toTrailerState(),
                similarShowsState = similarShows.toSimilarShowsState(),
                followShowState = FollowShowsState.Idle
            )
        }
            .catch {
                nextState = ShowDetailsError(it.resolveError())
            }
            .collect {
                nextState = it
            }


        return state.override { nextState }

    }

    private fun Either<Failure, SelectByShowId>.toShowState(): ShowState = fold(
        {
            ShowState.ShowError(it.errorMessage)
        },
        {
            ShowState.ShowLoaded(
                show = it.toTvShow(),
            )
        }
    )

    private fun Either<Failure, List<Season>>.toSeasonState() = fold(
        {
            SeasonState.SeasonsError(it.errorMessage)
        },
        {
            SeasonState.SeasonsLoaded(
                isLoading = false,
                seasonsList = it.toSeasonsList()
            )

        }
    )

    private fun Either<Failure, List<Trailers>>.toTrailerState() = fold(
        {
            TrailersError(it.errorMessage)
        },
        {
            TrailersLoaded(
                isLoading = false,
                hasWebViewInstalled = false,
                trailersList = it.toTrailerList()
            )

        }
    )

    private fun Either<Failure, List<SelectSimilarShows>>.toSimilarShowsState() = fold(
        {
            SimilarShowsState.SimilarShowsError(it.errorMessage)
        },
        {
            SimilarShowsState.SimilarShowsLoaded(
                isLoading = false,
                similarShows = it.toSimilarShowList()
            )

        }
    )

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
                    { show ->
                        state.mutate {
                            copy(
                                showState = (showState as ShowState.ShowLoaded)
                                    .copy(show = show.toTvShow())
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
    private val stateMachine: ShowDetailsStateMachine,
    private val scope: CoroutineScope,
) {
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
}