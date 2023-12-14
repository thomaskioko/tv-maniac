package com.thomaskioko.tvmaniac.presentation.showdetails

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.db.ShowSeasons
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.db.TvshowDetails
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsState.TrailersContent.Companion.playerErrorMessage
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.shows.api.LibraryRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.util.decompose.asValue
import com.thomaskioko.tvmaniac.util.decompose.coroutineScope
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias ShowDetailsPresenterPresenterFactory = (
    ComponentContext,
    id: Long,
    onBack: () -> Unit,
    onNavigateToShow: (id: Long) -> Unit,
    onNavigateToSeason: (id: Long, title: String) -> Unit,
    onNavigateToTrailer: (id: Long) -> Unit,
) -> ShowDetailsPresenter

class ShowDetailsPresenter @Inject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val showId: Long,
    @Assisted private val onBack: () -> Unit,
    @Assisted private val onNavigateToShow: (id: Long) -> Unit,
    @Assisted private val onNavigateToSeason: (id: Long, title: String) -> Unit,
    @Assisted private val onNavigateToTrailer: (id: Long) -> Unit,
    private val showDetailsRepository: ShowDetailsRepository,
    private val similarShowsRepository: SimilarShowsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val trailerRepository: TrailerRepository,
    private val libraryRepository: LibraryRepository,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val _state = MutableStateFlow(ShowDetailsState.EMPTY_DETAIL_STATE)
    val state: Value<ShowDetailsState> = _state
        .asValue(initialValue = _state.value, lifecycle = lifecycle)

    init {
        coroutineScope.launch {
            fetchShowDetails()
            observeShowDetails()
        }
    }

    fun dispatch(action: ShowDetailsAction) {
        when (action) {
            DetailBackClicked -> onBack()
            is SeasonClicked -> onNavigateToSeason(action.id, action.title)
            is DetailShowClicked -> onNavigateToShow(action.id)
            is WatchTrailerClicked -> onNavigateToTrailer(action.id)
            DismissWebViewError -> {
                coroutineScope.launch {
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
                coroutineScope.launch {
                    libraryRepository.updateLibrary(
                        traktId = showId,
                        addToLibrary = !action.addToLibrary,
                    )
                }
            }

            is ReloadShowDetails -> {
                coroutineScope.launch {
                    fetchShowDetails()
                }
            }

            WebViewError -> {
                coroutineScope.launch {
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
        val showDetails = showDetailsRepository.getShowDetails(showId)
        val season = seasonsRepository.fetchSeasonsByShowId(showId)
        val trailers = trailerRepository.fetchTrailersByShowId(showId)
        val similar = similarShowsRepository.fetchSimilarShows(showId)

        _state.update {
            it.copy(
                showDetails = showDetails.toShowDetails(),
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
                similarShowsContent = ShowDetailsState.SimilarShowsContent(
                    isLoading = false,
                    similarSimilarShows = similar.toSimilarShowList(),
                    errorMessage = null,
                ),
                errorMessage = null,
            )
        }
    }

    private suspend fun observeShowDetails() {
        combine(
            showDetailsRepository.observeShowDetails(showId),
            seasonsRepository.observeSeasonsByShowId(showId),
            similarShowsRepository.observeSimilarShows(showId),
            trailerRepository.observeTrailersStoreResponse(showId),
            trailerRepository.isYoutubePlayerInstalled(),
        ) { show, seasons, similarShows, trailers, isWebViewInstalled ->
            updateShowDetails(show)
            updateShowSeasons(seasons)
            updateTrailerState(trailers, isWebViewInstalled)
            updateSimilarShowsState(similarShows)
        }.collect()
    }

    private fun updateShowDetails(
        response: Either<Failure, TvshowDetails>,
    ) = updateState(response) {
        copy(showDetails = response.getOrNull()!!.toShowDetails())
    }

    private fun updateTrailerState(
        response: Either<Failure, List<Trailers>>,
        isWebViewInstalled: Boolean,
    ) = updateState(response) {
        copy(
            trailersContent = trailersContent.copy(
                isLoading = false,
                hasWebViewInstalled = isWebViewInstalled,
                trailersList = response.getOrNull().toTrailerList(),
            ),
        )
    }

    private fun updateShowSeasons(response: Either<Failure, List<ShowSeasons>>) =
        updateState(response) {
            copy(
                seasonsContent = seasonsContent.copy(
                    seasonsList = response.getOrNull().toSeasonsList(),
                ),
            )
        }

    private fun updateSimilarShowsState(
        response: Either<Failure, List<SimilarShows>>,
    ) = updateState(response) {
        copy(
            similarShowsContent = similarShowsContent.copy(
                isLoading = false,
                similarSimilarShows = response.getOrNull().toSimilarShowList(),
            ),
        )
    }

    private inline fun <T> updateState(
        response: Either<Failure, T>,
        updateBlock: ShowDetailsState.(T) -> ShowDetailsState,
    ) = _state.update {
        when (response) {
            is Either.Left -> it.copy(
                seasonsContent = it.seasonsContent.copy(
                    isLoading = true,
                    errorMessage = response.error.errorMessage,
                ),
            )

            is Either.Right -> it.updateBlock(response.data)
        }
    }
}
