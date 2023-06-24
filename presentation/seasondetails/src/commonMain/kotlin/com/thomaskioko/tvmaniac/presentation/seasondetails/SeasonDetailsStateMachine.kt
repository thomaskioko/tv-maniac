package com.thomaskioko.tvmaniac.presentation.seasondetails

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.episodeimages.api.EpisodeImageRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class SeasonDetailsStateMachine constructor(
    @Assisted private val traktId: Long,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val episodeImageRepository: EpisodeImageRepository,
) : FlowReduxStateMachine<SeasonDetailsState, SeasonDetailsAction>(initialState = Loading) {

    init {
        spec {
            inState<Loading> {
                onEnter { state ->
                    fetchSeasonDetails(state)
                }
            }

            inState<SeasonDetailsLoaded> {
                collectWhileInState(seasonDetailsRepository.observeSeasonDetails(traktId)) { result, state ->
                    result.fold(
                        {
                            state.override { LoadingError(it.errorMessage) }
                        },
                        {
                            state.mutate {
                                copy(seasonDetailsList = it.toSeasonWithEpisodes())
                            }
                        },
                    )
                }

                collectWhileInStateEffect(episodeImageRepository.updateEpisodeImage()) { _, _ ->
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
                                    seasonDetailsList = result.toSeasonWithEpisodes(),
                                )
                            }
                        }

                    state.override { Loading }
                }
            }
        }
    }

    private suspend fun fetchSeasonDetails(state: State<Loading>): ChangedState<SeasonDetailsState> {
        var nextState: SeasonDetailsState = Loading

        seasonDetailsRepository.observeSeasonDetailsStream(traktId)
            .collect { result ->

                nextState = result.fold(
                    { LoadingError(it.errorMessage) },
                    {
                        SeasonDetailsLoaded(
                            showTitle = it.getTitle(),
                            seasonDetailsList = it.toSeasonWithEpisodes(),
                        )
                    },
                )
            }

        return state.override { nextState }
    }
}
