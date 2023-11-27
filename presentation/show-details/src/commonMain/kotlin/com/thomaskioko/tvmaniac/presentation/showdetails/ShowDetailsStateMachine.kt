package com.thomaskioko.tvmaniac.presentation.showdetails

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.core.db.SeasonsByShowId
import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsLoaded.TrailersContent.Companion.playerErrorMessage
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.shows.api.DiscoverRepository
import com.thomaskioko.tvmaniac.shows.api.LibraryRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class ShowDetailsStateMachine(
    @Assisted private val traktShowId: Long,
    private val discoverRepository: DiscoverRepository,
    private val similarShowsRepository: SimilarShowsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val trailerRepository: TrailerRepository,
    private val libraryRepository: LibraryRepository,
) : FlowReduxStateMachine<ShowDetailsState, ShowDetailsAction>(
    initialState = ShowDetailsLoaded.EMPTY_DETAIL_STATE,
) {

    init {
        spec {

            inState<ShowDetailsLoaded> {

                onEnter { state ->
                    fetchShowDetails(state)
                }

                untilIdentityChanges({ state -> state.show.traktId }) {
                    collectWhileInState(discoverRepository.observeShow(traktShowId)) { result, state ->
                        updateShowDetails(result, state)
                    }
                }

                collectWhileInState(discoverRepository.observeShow(traktShowId)) { response, state ->
                    updateShowDetails(response, state)
                }

                collectWhileInState(seasonsRepository.observeSeasonsByShowId(traktShowId)) { result, state ->
                    updateSeasonDetailsState(result, state)
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
                            trailersContent = trailersContent.copy(hasWebViewInstalled = result),
                        )
                    }
                }

                onActionEffect<FollowShowClicked> { action, _ ->
                    libraryRepository.updateLibrary(
                        traktId = traktShowId,
                        addToLibrary = !action.addToLibrary,
                    )
                }

                on<WebViewError> { _, state ->
                    state.mutate {
                        copy(
                            trailersContent = trailersContent.copy(
                                playerErrorMessage = playerErrorMessage,
                            ),
                        )
                    }
                }

                on<DismissWebViewError> { _, state ->
                    state.mutate {
                        copy(
                            trailersContent = trailersContent
                                .copy(playerErrorMessage = null),
                        )
                    }
                }
            }
        }
    }

    private fun updateShowDetails(
        response: Either<Failure, ShowById>,
        state: State<ShowDetailsLoaded>,
    ) = when (response) {
        is Either.Left -> state.mutate {
            copy(
                isLoading = false,
                errorMessage = response.error.errorMessage,
            )
        }

        is Either.Right -> state.mutate {
            copy(
                isLoading = false,
                show = response.data.toTvShow(),
            )
        }
    }

    private fun updateSimilarShowsState(
        response: Either<Failure, List<SimilarShows>>,
        state: State<ShowDetailsLoaded>,
    ) = when (response) {
        is Either.Left -> state.mutate {
            copy(
                similarShowsContent = similarShowsContent.copy(
                    errorMessage = response.error.errorMessage,
                ),
            )
        }

        is Either.Right -> state.mutate {
            copy(
                similarShowsContent = similarShowsContent.copy(
                    isLoading = false,
                    similarShows = response.data.toSimilarShowList(),
                ),
            )
        }
    }

    private fun updateTrailerState(
        response: Either<Failure, List<Trailers>>,
        state: State<ShowDetailsLoaded>,
    ) = when (response) {
        is Either.Left -> {
            state.mutate {
                copy(trailersContent = trailersContent.copy(errorMessage = response.error.errorMessage))
            }
        }

        is Either.Right -> {
            state.mutate {
                copy(
                    trailersContent = trailersContent.copy(
                        isLoading = false,
                        trailersList = response.data.toTrailerList(),
                    ),
                )
            }
        }
    }

    private fun updateSeasonDetailsState(
        response: Either<Failure, List<SeasonsByShowId>>,
        state: State<ShowDetailsLoaded>,
    ) = when (response) {
        is Either.Left -> {
            state.mutate {
                copy(
                    seasonsContent = seasonsContent.copy(
                        isLoading = true,
                        errorMessage = response.error.errorMessage,
                    ),
                )
            }
        }

        is Either.Right -> {
            state.mutate {
                copy(
                    seasonsContent = seasonsContent.copy(
                        isLoading = false,
                        seasonsList = response.data.toSeasonsList(),
                    ),
                )
            }
        }
    }

    private suspend fun fetchShowDetails(state: State<ShowDetailsLoaded>): ChangedState<ShowDetailsState> {
        val show = discoverRepository.getShowById(traktShowId)
        val similar = similarShowsRepository.fetchSimilarShows(traktShowId)
        val season = seasonsRepository.fetchSeasonsByShowId(traktShowId)
        val trailers = trailerRepository.fetchTrailersByShowId(traktShowId)

        return state.mutate {
            copy(
                show = show.toTvShow(),
                similarShowsContent = ShowDetailsLoaded.SimilarShowsContent(
                    isLoading = false,
                    similarShows = similar.toSimilarShowList(),
                    errorMessage = null,
                ),
                seasonsContent = ShowDetailsLoaded.SeasonsContent(
                    isLoading = false,
                    seasonsList = season.toSeasonsList(),
                    errorMessage = null,
                ),
                trailersContent = ShowDetailsLoaded.TrailersContent(
                    isLoading = false,
                    hasWebViewInstalled = false,
                    trailersList = trailers.toTrailerList(),
                    errorMessage = null,
                ),
                errorMessage = null,
            )
        }
    }
}
