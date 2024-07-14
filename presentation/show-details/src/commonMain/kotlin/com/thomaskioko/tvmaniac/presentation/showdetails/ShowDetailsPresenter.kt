package com.thomaskioko.tvmaniac.presentation.showdetails

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsRepository
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.presentation.showdetails.model.AdditionalContent
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowDetails
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowMetadata
import com.thomaskioko.tvmaniac.presentation.showdetails.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.shows.api.LibraryRepository
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias ShowDetailsPresenterPresenterFactory =
  (
    ComponentContext,
    id: Long,
    onBack: () -> Unit,
    onNavigateToShow: (id: Long) -> Unit,
    onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit,
    onNavigateToTrailer: (id: Long) -> Unit,
  ) -> ShowDetailsPresenter

class ShowDetailsPresenter
@Inject
constructor(
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
  private val _state = MutableStateFlow(ShowDetailsContent(showDetails = null))
  val state: StateFlow<ShowDetailsContent> = _state.asStateFlow()

  init {
    coroutineScope.launch { observeShowDetails() }
  }

  fun dispatch(action: ShowDetailsAction) {
    when (action) {
      is SeasonClicked -> {
        _state.update {
          it.copy(
            showInfo =
              it.showInfo?.copy(
                selectedSeasonIndex = action.params.selectedSeasonIndex,
              )
                ?: it.showInfo,
          )
        }
        onNavigateToSeason(action.params)
      }
      is DetailShowClicked -> onNavigateToShow(action.id)
      is WatchTrailerClicked -> onNavigateToTrailer(action.id)
      is FollowShowClicked -> {
        coroutineScope.launch {
          libraryRepository.updateLibrary(
            traktId = showId,
            addToLibrary = !action.addToLibrary,
          )
        }
      }
      ReloadShowDetails -> coroutineScope.launch { observeShowDetails(forceReload = true) }
      DetailBackClicked -> onBack()
      DismissErrorSnackbar ->
        coroutineScope.launch { _state.update { it.copy(errorMessage = null) } }
    }
  }

  private suspend fun observeShowDetails(forceReload: Boolean = false) {
    return combine(
        showDetailsRepository.observeShowDetails(showId, forceReload),
        trailerRepository.isYoutubePlayerInstalled(),
        observeShowMetadata(forceReload),
        observeAdditionalContent(forceReload),
      ) { showDetailsResult, isWebViewInstalled, showMetadata, additionalContent ->
        showDetailsResult.fold(
          { error ->
            _state.update {
              it.copy(
                isUpdating = false,
                errorMessage = error.errorMessage ?: "An unknown error occurred",
              )
            }
          },
          { result ->
            updateState(
              result?.toShowDetails(),
              isWebViewInstalled,
              showMetadata,
              additionalContent,
            )
          },
        )
      }
      .onStart { _state.update { it.copy(isUpdating = true) } }
      .collect()
  }

  private fun observeShowMetadata(forceReload: Boolean) =
    combine(
      seasonsRepository.observeSeasonsByShowId(showId).map { it.toSeasonsListOrEmpty() },
      castRepository.observeShowCast(showId).map { it.toCastList() },
      watchProviders.observeWatchProviders(showId, forceReload).map {
        it.toWatchProviderListOrEmpty()
      },
    ) { seasons, cast, providers ->
      ShowMetadata(seasons, cast, providers)
    }

  private fun observeAdditionalContent(forceReload: Boolean) =
    combine(
      similarShowsRepository.observeSimilarShows(showId, forceReload).map {
        it.toSimilarShowListOrEmpty()
      },
      recommendedShowsRepository.observeRecommendedShows(showId, forceReload).map {
        it.toRecommendedShowListOrEmpty()
      },
      trailerRepository.observeTrailers(showId).map { it.toTrailerListOrEmpty() },
    ) { similarShows, recommendedShows, trailers ->
      AdditionalContent(similarShows, recommendedShows, trailers)
    }

  private fun updateState(
    showDetails: ShowDetails?,
    isWebViewInstalled: Boolean,
    showMetadata: ShowMetadata,
    additionalContent: AdditionalContent,
  ) {
    _state.update { currentState ->
      val newShowInfoState =
        when {
          !isContentEmpty(showMetadata, additionalContent) -> {
            ShowDetailsContent.ShowInfoContent(
              hasWebViewInstalled = isWebViewInstalled,
              providers = showMetadata.providers.toImmutableList(),
              castsList = showMetadata.cast.toImmutableList(),
              seasonsList = showMetadata.seasons.toImmutableList(),
              similarShows = additionalContent.similarShows.toImmutableList(),
              recommendedShowList = additionalContent.recommendedShows.toImmutableList(),
              trailersList = additionalContent.trailers.toImmutableList(),
              openTrailersInYoutube = currentState.showInfo?.openTrailersInYoutube ?: false,
              selectedSeasonIndex = currentState.showInfo?.selectedSeasonIndex ?: 0,
            )
          }
          else -> null
        }

      currentState.copy(
        showDetails = showDetails ?: currentState.showDetails,
        showInfo = newShowInfoState,
        isUpdating = false,
        errorMessage = null,
      )
    }
  }

  private fun isContentEmpty(
    showMetadata: ShowMetadata,
    additionalContent: AdditionalContent,
  ): Boolean {
    return showMetadata.seasons.isEmpty() &&
      showMetadata.cast.isEmpty() &&
      showMetadata.providers.isEmpty() &&
      additionalContent.similarShows.isEmpty() &&
      additionalContent.recommendedShows.isEmpty() &&
      additionalContent.trailers.isEmpty()
  }
}
