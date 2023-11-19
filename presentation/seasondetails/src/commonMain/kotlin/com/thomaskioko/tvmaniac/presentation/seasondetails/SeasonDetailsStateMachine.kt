package com.thomaskioko.tvmaniac.presentation.seasondetails

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.thomaskioko.tvmaniac.episodeimages.api.EpisodeImageRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class SeasonDetailsStateMachine(
    @Assisted private val traktId: Long,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val episodeImageRepository: EpisodeImageRepository,
) : FlowReduxStateMachine<SeasonDetailsState, SeasonDetailsAction>(initialState = Loading) {

    init {
        spec {
            inState<Loading> {
                onEnter { state ->
                    val seasonList = seasonDetailsRepository.fetchSeasonDetails(traktId)

                    state.override {
                        SeasonDetailsLoaded(
                            showTitle = seasonList.getTitle(),
                            seasonDetailsList = seasonList.toSeasonWithEpisodes(),
                        )
                    }
                }
            }

            inState<SeasonDetailsLoaded> {
                collectWhileInState(seasonDetailsRepository.observeSeasonDetailsStream(traktId)) { result, state ->
                    result.fold(
                        {
                            state.mutate {
                                copy(errorMessage = it.errorMessage)
                            }
                        },
                        {
                            state.mutate {
                                copy(seasonDetailsList = it.toSeasonWithEpisodes())
                            }
                        },
                    )
                }

                collectWhileInStateEffect(episodeImageRepository.updateEpisodeImage(traktId)) { _, _ ->
                    /** No need to do anything. Just trigger artwork download. **/
                }
            }

            inState<LoadingError> {
                on<ReloadSeasonDetails> { _, state ->

                    state.override { Loading }
                }
            }
        }
    }
}
