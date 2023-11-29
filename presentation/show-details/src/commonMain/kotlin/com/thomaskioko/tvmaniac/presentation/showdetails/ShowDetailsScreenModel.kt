package com.thomaskioko.tvmaniac.presentation.showdetails

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.thomaskioko.tvmaniac.core.db.SeasonsByShowId
import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsState.TrailersContent.Companion.playerErrorMessage
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.shows.api.DiscoverRepository
import com.thomaskioko.tvmaniac.shows.api.LibraryRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class ShowDetailsScreenModel @Inject constructor(
    @Assisted private val traktShowId: Long,
    private val discoverRepository: DiscoverRepository,
    private val similarShowsRepository: SimilarShowsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val trailerRepository: TrailerRepository,
    private val libraryRepository: LibraryRepository,
) : ScreenModel {

    private val _state = MutableStateFlow(ShowDetailsState.EMPTY_DETAIL_STATE)
    val state: StateFlow<ShowDetailsState> = _state.asStateFlow()

    init {
        screenModelScope.launch {
            fetchShowDetails()
            observeShowDetails()
        }
    }

    fun dispatch(action: ShowDetailsAction) {
        when (action) {
            DismissWebViewError -> {
                screenModelScope.launch {
                    _state.update {
                        it.copy(
                            trailersContent = it.trailersContent.copy(
                                playerErrorMessage = null,
                            ),
                        )
                    }
                }
            }

            is FollowShowClicked -> {
                screenModelScope.launch {
                    libraryRepository.updateLibrary(
                        traktId = traktShowId,
                        addToLibrary = !action.addToLibrary,
                    )
                }
            }

            is ReloadShowDetails -> {
                screenModelScope.launch {
                    fetchShowDetails()
                }
            }

            WebViewError -> {
                screenModelScope.launch {
                    _state.update {
                        it.copy(
                            trailersContent = it.trailersContent.copy(
                                playerErrorMessage = playerErrorMessage,
                            ),
                        )
                    }
                }
            }
        }
    }

    private suspend fun fetchShowDetails() {
        val show = discoverRepository.getShowById(traktShowId)
        val similar = similarShowsRepository.fetchSimilarShows(traktShowId)
        val season = seasonsRepository.fetchSeasonsByShowId(traktShowId)
        val trailers = trailerRepository.fetchTrailersByShowId(traktShowId)

        _state.update {
            it.copy(
                show = show.toTvShow(),
                similarShowsContent = ShowDetailsState.SimilarShowsContent(
                    isLoading = false,
                    similarShows = similar.toSimilarShowList(),
                    errorMessage = null,
                ),
                seasonsContent = ShowDetailsState.SeasonsContent(
                    isLoading = false,
                    seasonsList = season.toSeasonsList(),
                    errorMessage = null,
                ),
                trailersContent = ShowDetailsState.TrailersContent(
                    isLoading = false,
                    hasWebViewInstalled = false,
                    trailersList = trailers.toTrailerList(),
                    errorMessage = null,
                ),
                errorMessage = null,
            )
        }
    }

    private suspend fun observeShowDetails() {
        combine(
            discoverRepository.observeShow(traktShowId),
            seasonsRepository.observeSeasonsByShowId(traktShowId),
            trailerRepository.observeTrailersStoreResponse(traktShowId),
            similarShowsRepository.observeSimilarShows(traktShowId),
            trailerRepository.isYoutubePlayerInstalled(),
        ) { show, seasons, trailers, similarShows, isWebViewInstalled ->
            updateShowDetails(show)
            updateSeasonDetailsState(seasons)
            updateTrailerState(trailers, isWebViewInstalled)
            updateSimilarShowsState(similarShows)
        }.collect()
    }

    private fun updateShowDetails(
        response: Either<Failure, ShowById>,
    ) = when (response) {
        is Either.Left -> _state.update {
            it.copy(
                isLoading = false,
                errorMessage = response.error.errorMessage,
            )
        }

        is Either.Right -> _state.update {
            it.copy(
                isLoading = false,
                show = response.data.toTvShow(),
            )
        }
    }

    private fun updateSeasonDetailsState(
        response: Either<Failure, List<SeasonsByShowId>>,
    ) = when (response) {
        is Either.Left -> {
            _state.update {
                it.copy(
                    seasonsContent = it.seasonsContent.copy(
                        isLoading = true,
                        errorMessage = response.error.errorMessage,
                    ),
                )
            }
        }

        is Either.Right -> {
            _state.update {
                it.copy(
                    seasonsContent = it.seasonsContent.copy(
                        isLoading = false,
                        seasonsList = response.data.toSeasonsList(),
                    ),
                )
            }
        }
    }

    private fun updateTrailerState(
        response: Either<Failure, List<Trailers>>,
        isWebViewInstalled: Boolean,
    ) = when (response) {
        is Either.Left -> {
            _state.update {
                it.copy(
                    trailersContent = it.trailersContent.copy(
                        errorMessage = response.error.errorMessage,
                    ),
                )
            }
        }

        is Either.Right -> {
            _state.update {
                it.copy(
                    trailersContent = it.trailersContent.copy(
                        isLoading = false,
                        hasWebViewInstalled = isWebViewInstalled,
                        trailersList = response.data.toTrailerList(),
                    ),
                )
            }
        }
    }

    private fun updateSimilarShowsState(
        response: Either<Failure, List<SimilarShows>>,
    ) = when (response) {
        is Either.Left -> _state.update {
            it.copy(
                similarShowsContent = it.similarShowsContent.copy(
                    errorMessage = response.error.errorMessage,
                ),
            )
        }

        is Either.Right -> _state.update {
            it.copy(
                similarShowsContent = it.similarShowsContent.copy(
                    isLoading = false,
                    similarShows = response.data.toSimilarShowList(),
                ),
            )
        }
    }
}
