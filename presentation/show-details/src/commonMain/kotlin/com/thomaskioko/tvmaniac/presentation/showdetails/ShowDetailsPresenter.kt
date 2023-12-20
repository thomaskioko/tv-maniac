package com.thomaskioko.tvmaniac.presentation.showdetails

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.db.RecommendedShows
import com.thomaskioko.tvmaniac.core.db.ShowCast
import com.thomaskioko.tvmaniac.core.db.ShowSeasons
import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.core.db.TvshowDetails
import com.thomaskioko.tvmaniac.core.db.WatchProviders
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsRepository
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.shows.api.LibraryRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import com.thomaskioko.tvmaniac.util.decompose.asValue
import com.thomaskioko.tvmaniac.util.decompose.coroutineScope
import com.thomaskioko.tvmaniac.util.extensions.combine
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias ShowDetailsPresenterPresenterFactory = (
    ComponentContext,
    id: Long,
    onBack: () -> Unit,
    onNavigateToShow: (id: Long) -> Unit,
    onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit,
    onNavigateToTrailer: (id: Long) -> Unit,
) -> ShowDetailsPresenter

class ShowDetailsPresenter @Inject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val showId: Long,
    @Assisted private val onBack: () -> Unit,
    @Assisted private val onNavigateToShow: (id: Long) -> Unit,
    @Assisted private val onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit,
    @Assisted private val onNavigateToTrailer: (id: Long) -> Unit,
    private val castRepository: CastRepository,
    private val libraryRepository: LibraryRepository,
    private val recommendedShowsRepository: RecommendedShowsRepository,
    private val seasonsRepository: SeasonsRepository,
    private val showDetailsRepository: ShowDetailsRepository,
    private val similarShowsRepository: SimilarShowsRepository,
    private val trailerRepository: TrailerRepository,
    private val watchProviders: WatchProviderRepository,
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
            is SeasonClicked -> {
                _state.update {
                    it.copy(selectedSeasonIndex = action.params.selectedSeasonIndex)
                }
                onNavigateToSeason(action.params)
            }

            is DetailShowClicked -> onNavigateToShow(action.id)
            is WatchTrailerClicked -> onNavigateToTrailer(action.id)
            DismissWebViewError -> {
                coroutineScope.launch {
                    _state.update {
                        it.copy(showPlayerErrorMessage = false)
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
                        it.copy(showPlayerErrorMessage = true)
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
        val recommended = recommendedShowsRepository.fetchRecommendedShows(showId)
        val watchProviders = watchProviders.fetchWatchProviders(showId)
        val castList = castRepository.fetchShowCast(showId)

        _state.update {
            it.copy(
                showDetails = showDetails.toShowDetails(),
                providers = watchProviders.toWatchProviderList(),
                castsList = castList.toCastList(),
                seasonsList = season.toSeasonsList(),
                recommendedShowList = recommended.toRecommendedShowList(),
                trailersList = trailers.toTrailerList(),
                similarShows = similar.toSimilarShowList(),
            )
        }
    }

    private suspend fun observeShowDetails() {
        combine(
            showDetailsRepository.observeShowDetails(showId),
            seasonsRepository.observeSeasonsByShowId(showId),
            similarShowsRepository.observeSimilarShows(showId),
            recommendedShowsRepository.observeRecommendedShows(showId),
            castRepository.observeShowCast(showId),
            watchProviders.observeWatchProviders(showId),
            trailerRepository.observeTrailersStoreResponse(showId),
            trailerRepository.isYoutubePlayerInstalled(),
        ) { show, seasons, similarShows, recommendedShows, cast, watchProviders, trailers, isWebViewInstalled ->
            updateShowDetails(show)
            updateShowSeasons(seasons)
            updateTrailerState(trailers, isWebViewInstalled)
            updateSimilarShowsState(similarShows)
            updateRecommendedShowsState(recommendedShows)
            updateCastState(cast)
            updateWatchProviders(watchProviders)
        }.collect()
    }

    private fun updateShowDetails(
        response: Either<Failure, TvshowDetails>,
    ) = updateState(response) {
        when (response) {
            is Either.Left -> copy(errorMessage = response.left.errorMessage)
            is Either.Right -> copy(showDetails = response.right.toShowDetails())
        }
    }

    private fun updateTrailerState(
        response: Either<Failure, List<Trailers>>,
        isWebViewInstalled: Boolean,
    ) = updateState(response) {
        when (response) {
            is Either.Left -> copy(errorMessage = response.left.errorMessage)
            is Either.Right -> copy(
                hasWebViewInstalled = isWebViewInstalled,
                trailersList = response.right.toTrailerList(),
            )
        }
    }

    private fun updateShowSeasons(response: Either<Failure, List<ShowSeasons>>) =
        updateState(response) {
            when (response) {
                is Either.Left -> copy(errorMessage = response.left.errorMessage)
                is Either.Right -> copy(
                    seasonsList = response.right.toSeasonsList(),
                )
            }
        }

    private fun updateSimilarShowsState(
        response: Either<Failure, List<SimilarShows>>,
    ) = updateState(response) {
        when (response) {
            is Either.Left -> copy(errorMessage = response.left.errorMessage)
            is Either.Right -> copy(
                similarShows = response.right.toSimilarShowList(),
            )
        }
    }

    private fun updateRecommendedShowsState(
        response: Either<Failure, List<RecommendedShows>>,
    ) = updateState(response) {
        when (response) {
            is Either.Left -> copy(errorMessage = response.left.errorMessage)
            is Either.Right -> copy(
                recommendedShowList = response.right.toRecommendedShowList(),
            )
        }
    }

    private fun updateWatchProviders(response: Either<Failure, List<WatchProviders>>) =
        updateState(response) {
            when (response) {
                is Either.Left -> copy(errorMessage = response.left.errorMessage)
                is Either.Right -> copy(
                    providers = response.right.toWatchProviderList(),
                )
            }
        }

    private fun updateCastState(list: List<ShowCast>) {
        _state.update {
            it.copy(
                castsList = list.toCastList(),
            )
        }
    }

    private inline fun <T> updateState(
        response: Either<Failure, T>,
        updateBlock: ShowDetailsState.(T) -> ShowDetailsState,
    ) = _state.update {
        when (response) {
            is Either.Left -> it.copy(
                errorMessage = response.left.errorMessage,
            )

            is Either.Right -> it.updateBlock(response.right)
        }
    }
}
