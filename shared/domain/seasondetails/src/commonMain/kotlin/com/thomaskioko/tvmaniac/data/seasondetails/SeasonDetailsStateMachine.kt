package com.thomaskioko.tvmaniac.data.seasondetails

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Inject
class SeasonDetailsStateMachine constructor(
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val episodeRepository: EpisodeRepository,
) : FlowReduxStateMachine<SeasonDetailsState, SeasonDetailsAction>(initialState = Loading) {


    init {
        spec {
            inState<Loading> {
                on<LoadSeasonDetails> { action, state ->
                    fetchSeasonDetails(state, action)
                }
            }

            inState<SeasonDetailsLoaded> {
                collectWhileInState(seasonDetailsRepository.observeSeasonDetails()) { result, state ->
                    result.fold(
                        {
                            state.override { LoadingError(it.errorMessage) }
                        },
                        {
                            state.mutate {
                                copy(seasonDetailsList = it.toSeasonWithEpisodes())
                            }
                        }
                    )
                }

                collectWhileInStateEffect(episodeRepository.updateEpisodeArtWork()) { _, _ ->
                    /** No need to do anything. Just trigger artwork download. **/
                }
            }

            inState<LoadingError> {
                on<ReloadSeasonDetails> { action, state ->
                    var nextState: SeasonDetailsState = state.snapshot

                    seasonDetailsRepository.observeSeasonDetailsStream(traktId = action.showId)
                        .collect { result ->
                            nextState = when (result) {
                                is Either.Left -> LoadingError(result.error.errorMessage)
                                is Either.Right -> SeasonDetailsLoaded(
                                    showTitle = result.getTitle(),
                                    seasonDetailsList = result.toSeasonWithEpisodes()
                                )
                            }
                        }

                    state.override { nextState }
                }
            }
        }
    }

    private suspend fun fetchSeasonDetails(
        state: State<Loading>,
        action: LoadSeasonDetails
    ): ChangedState<SeasonDetailsState> {
        lateinit var nextState: SeasonDetailsState

        seasonDetailsRepository.observeSeasonDetailsStream(traktId = action.showId)
            .collect { result ->

                nextState = result.fold(
                    { LoadingError(it.errorMessage) },
                    {
                        SeasonDetailsLoaded(
                            showTitle = it.getTitle(),
                            seasonDetailsList = it.toSeasonWithEpisodes()
                        )
                    }
                )
            }

        return state.override { nextState }
    }
}

/**
 * A wrapper class around [SeasonDetailsStateMachine] handling `Flow` and suspend functions on iOS.
 */
@Inject
class SeasonDetailsStateMachineWrapper(
    private val scope: AppCoroutineScope,
    private val stateMachine: SeasonDetailsStateMachine,
) {

    fun start(stateChangeListener: (SeasonDetailsState) -> Unit) {
        scope.main.launch {
            stateMachine.state.collect {
                stateChangeListener(it)
            }
        }
    }

    fun dispatch(action: SeasonDetailsAction) {
        scope.main.launch {
            stateMachine.dispatch(action)
        }
    }

    fun cancel() {
        scope.main.cancel()
    }
}


