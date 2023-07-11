package com.thomaskioko.tvmaniac.presentation.showdetails

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.core.db.Seasons
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsLoaded.TrailersContent.Companion.playerErrorMessage
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.shows.api.DiscoverRepository
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadResponse

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class ShowDetailsStateMachine constructor(
    @Assisted private val traktShowId: Long,
    private val discoverRepository: DiscoverRepository,
    private val similarShowsRepository: SimilarShowsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val trailerRepository: TrailerRepository,
    private val watchlistRepository: WatchlistRepository,
) : FlowReduxStateMachine<ShowDetailsState, ShowDetailsAction>(
    initialState = ShowDetailsLoaded.EMPTY_DETAIL_STATE,
) {

    init {
        spec {

            inState<ShowDetailsLoaded> {

                onEnter { state ->
                    fetchShowDetails(state)
                }

                collectWhileInState(discoverRepository.observeShow(traktShowId)) { response, state ->
                    when (response) {
                        is StoreReadResponse.NoNewData -> state.noChange()
                        is StoreReadResponse.Loading -> state.mutate {
                            copy(isLoading = true)
                        }

                        is StoreReadResponse.Data -> state.mutate {
                            copy(
                                isLoading = false,
                                show = response.requireData().toTvShow(),
                            )
                        }

                        is StoreReadResponse.Error.Exception ->
                            state.mutate {
                                copy(
                                    isLoading = false,
                                    errorMessage = response.errorMessageOrNull(),
                                )
                            }

                        is StoreReadResponse.Error.Message -> state.mutate {
                            copy(
                                isLoading = false,
                                errorMessage = response.message,
                            )
                        }
                    }
                }

                collectWhileInState(seasonsRepository.observeSeasonsStoreResponse(traktShowId)) { result, state ->
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
                    watchlistRepository.updateWatchlist(
                        traktId = traktShowId,
                        addToWatchList = !action.addToFollowed,
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

    private fun updateSimilarShowsState(
        response: StoreReadResponse<List<SimilarShows>>,
        state: State<ShowDetailsLoaded>,
    ) = when (response) {
        is StoreReadResponse.NoNewData -> state.noChange()
        is StoreReadResponse.Loading -> {
            state.mutate {
                copy(
                    similarShowsContent = similarShowsContent
                        .copy(isLoading = true),
                )
            }
        }

        is StoreReadResponse.Data -> {
            state.mutate {
                copy(
                    similarShowsContent = similarShowsContent.copy(
                        isLoading = false,
                        similarShows = response.requireData().toSimilarShowList(),
                    ),
                )
            }
        }

        is StoreReadResponse.Error.Exception -> {
            state.mutate {
                copy(
                    similarShowsContent = similarShowsContent.copy(
                        errorMessage = response.error.message,
                    ),
                )
            }
        }

        is StoreReadResponse.Error.Message -> {
            state.mutate {
                copy(
                    similarShowsContent = similarShowsContent.copy(
                        errorMessage = response.message,
                    ),
                )
            }
        }
    }

    private fun updateTrailerState(
        response: StoreReadResponse<List<Trailers>>,
        state: State<ShowDetailsLoaded>,
    ) = when (response) {
        is StoreReadResponse.NoNewData -> state.noChange()
        is StoreReadResponse.Loading -> state.mutate {
            copy(
                trailersContent = trailersContent.copy(
                    isLoading = true,
                ),
            )
        }

        is StoreReadResponse.Data -> {
            state.mutate {
                copy(
                    trailersContent = trailersContent.copy(
                        isLoading = false,
                        trailersList = response.requireData().toTrailerList(),
                    ),
                )
            }
        }

        is StoreReadResponse.Error.Exception -> {
            state.mutate {
                copy(
                    trailersContent = trailersContent.copy(
                        errorMessage = response.error.message,
                    ),
                )
            }
        }

        is StoreReadResponse.Error.Message -> {
            state.mutate {
                copy(trailersContent = trailersContent.copy(errorMessage = response.message))
            }
        }
    }

    private fun updateSeasonDetailsState(
        response: StoreReadResponse<List<Seasons>>,
        state: State<ShowDetailsLoaded>,
    ) = when (response) {
        is StoreReadResponse.NoNewData -> state.noChange()
        is StoreReadResponse.Loading -> {
            state.mutate {
                copy(
                    seasonsContent = seasonsContent.copy(
                        isLoading = true,
                    ),
                )
            }
        }

        is StoreReadResponse.Data -> {
            state.mutate {
                copy(
                    seasonsContent = seasonsContent.copy(
                        isLoading = false,
                        seasonsList = response.requireData().toSeasonsList(),
                    ),
                )
            }
        }

        is StoreReadResponse.Error.Exception -> {
            state.mutate {
                copy(
                    seasonsContent = seasonsContent.copy(
                        isLoading = true,
                        errorMessage = response.error.message,
                    ),
                )
            }
        }

        is StoreReadResponse.Error.Message -> {
            state.mutate {
                copy(
                    seasonsContent = seasonsContent.copy(
                        isLoading = true,
                        errorMessage = response.message,
                    ),
                )
            }
        }
    }

    private suspend fun fetchShowDetails(state: State<ShowDetailsLoaded>): ChangedState<ShowDetailsState> {
        val show = discoverRepository.getShowById(traktShowId)
        val similar = similarShowsRepository.fetchSimilarShows(traktShowId)
        val season = seasonsRepository.getSeasons(traktShowId)
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
