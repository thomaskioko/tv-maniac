package com.thomaskioko.tvmaniac.seasondetails.api

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SeasonDetailsStateMachine constructor(
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val episodeRepository: EpisodeRepository,
) : FlowReduxStateMachine<SeasonDetailsState, SeasonDetailsAction>(initialState = Loading) {

    private var showId: MutableStateFlow<Long> = MutableStateFlow(0)

    init {
        spec {
            inState<Loading> {
                on<LoadSeasonDetails> { action, state ->
                    var nextState: SeasonDetailsState = state.snapshot

                    seasonDetailsRepository.observeSeasonDetails(showId = action.showId)
                        .collect { result ->
                            showId.value = action.showId

                            nextState = when (result) {
                                is Either.Left -> LoadingError(result.error.errorMessage)
                                is Either.Right ->
                                    SeasonDetailsLoaded(
                                        showTitle = result.getTitle(),
                                        episodeList = result.toSeasonWithEpisodes()
                                    )
                            }
                        }

                    state.override { nextState }
                }
            }

            inState<SeasonDetailsLoaded> {

                collectWhileInState(showId) { id, state ->
                    var nextState: ChangedState<SeasonDetailsLoaded> = state.noChange()
                    seasonDetailsRepository.observeCachedSeasonDetails(id)
                        .collect {
                            when (it) {
                                is Either.Left -> state.override { LoadingError(it.error.errorMessage) }
                                is Either.Right -> nextState = state.mutate {
                                    copy(episodeList = it.toSeasonWithEpisodes())
                                }
                            }
                        }
                    nextState
                }

                collectWhileInStateEffect(showId) { id, _ ->
                    episodeRepository.updateEpisodeArtWork(id)
                }
            }

            inState<LoadingError> {
                on<ReloadSeasonDetails> { action, state ->
                    var nextState: SeasonDetailsState = state.snapshot

                    seasonDetailsRepository.observeSeasonDetails(showId = action.showId)
                        .collect { result ->
                            nextState = when (result) {
                                is Either.Left -> LoadingError(result.error.errorMessage)
                                is Either.Right -> SeasonDetailsLoaded(
                                    showTitle = result.getTitle(),
                                    episodeList = result.toSeasonWithEpisodes()
                                )
                            }
                        }

                    state.override { nextState }
                }
            }
        }
    }
}


