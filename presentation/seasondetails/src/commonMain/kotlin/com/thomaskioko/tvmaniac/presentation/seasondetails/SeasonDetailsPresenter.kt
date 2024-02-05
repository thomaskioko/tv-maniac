package com.thomaskioko.tvmaniac.presentation.seasondetails

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsContent.Companion.DEFAULT_SEASON_STATE
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.util.decompose.asValue
import com.thomaskioko.tvmaniac.util.decompose.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias SeasonDetailsPresenterFactory =
  (
    ComponentContext,
    param: SeasonDetailsUiParam,
    onBack: () -> Unit,
    onNavigateToEpisodeDetails: (id: Long) -> Unit,
  ) -> SeasonDetailsPresenter

class SeasonDetailsPresenter
@Inject
constructor(
  @Assisted componentContext: ComponentContext,
  @Assisted private val param: SeasonDetailsUiParam,
  @Assisted private val onBack: () -> Unit,
  @Assisted private val onEpisodeClick: (id: Long) -> Unit,
  private val seasonDetailsRepository: SeasonDetailsRepository,
  private val castRepository: CastRepository,
) : ComponentContext by componentContext {

  private var seasonDetailsParam: SeasonDetailsParam =
    SeasonDetailsParam(
      showId = param.showId,
      seasonId = param.seasonId,
      seasonNumber = param.seasonNumber,
    )
  private val coroutineScope = coroutineScope()
  private val _state = MutableStateFlow(DEFAULT_SEASON_STATE)
  val state: StateFlow<SeasonDetailsContent> = _state.asStateFlow()

  // TODO:: Create SwiftUI flow wrapper and get rid of this.
  val value: Value<SeasonDetailsContent> =
    _state.asValue(initialValue = _state.value, lifecycle = lifecycle)

  init {
    coroutineScope.launch {
      fetchSeasonDetails()
      observeSeasonDetails()
    }
  }

  fun dispatch(action: SeasonDetailsAction) {
    coroutineScope.launch {
      when (action) {
        SeasonDetailsBackClicked -> onBack()
        is EpisodeClicked -> onEpisodeClick(action.id)
        is ReloadSeasonDetails -> fetchSeasonDetails()
        is UpdateEpisodeStatus -> {
          // TODO:: Add implementation
        }
        SeasonGalleryClicked ->
          coroutineScope.launch {
            _state.update { state ->
              (state as? SeasonDetailsContent)?.copy(
                showGalleryBottomSheet = !state.showGalleryBottomSheet,
              )
                ?: state
            }
          }
        ShowMarkSeasonDialog -> {
          coroutineScope.launch {
            _state.update { state ->
              state.copy(
                showSeasonWatchStateDialog = !state.showSeasonWatchStateDialog,
              )
            }
          }
        }
        DismissSeasonDialog ->
          coroutineScope.launch {
            _state.update { state -> state.copy(showSeasonWatchStateDialog = false) }
          }
        UpdateSeasonWatchedState -> {
          // TODO:: Invoice service to update season watched state
          coroutineScope.launch {
            _state.update { state -> state.copy(showSeasonWatchStateDialog = false) }
          }
        }
        OnEpisodeHeaderClicked -> {
          coroutineScope.launch {
            _state.update { state -> state.copy(expandEpisodeItems = !state.expandEpisodeItems) }
          }
        }
        DismissSeasonDetailSnackBar -> {
          coroutineScope.launch { _state.update { state -> state.copy(errorMessage = null) } }
        }
      }
    }
  }

  private suspend fun fetchSeasonDetails() {
    val seasonDetails = seasonDetailsRepository.fetchSeasonDetails(seasonDetailsParam)
    val imageList = seasonDetailsRepository.fetchSeasonImages(seasonDetailsParam.seasonId)
    val castList = castRepository.fetchSeasonCast(seasonDetailsParam.seasonId)

    _state.update {
      SeasonDetailsContent(
        seasonId = seasonDetails.seasonId,
        seasonName = seasonDetails.name,
        seasonOverview = seasonDetails.seasonOverview,
        watchProgress = 0f,
        isSeasonWatched = false,
        episodeCount = seasonDetails.episodeCount,
        imageUrl = seasonDetails.imageUrl,
        episodeDetailsList = seasonDetails.episodes.toEpisodes(),
        seasonImages = imageList.toImageList(),
        seasonCast = castList.toCastList(),
      )
    }
  }

  private suspend fun observeSeasonDetails() {
    combine(
        seasonDetailsRepository.observeSeasonDetails(seasonDetailsParam),
        castRepository.observeSeasonCast(seasonDetailsParam.seasonId),
        seasonDetailsRepository.observeSeasonImages(seasonDetailsParam.seasonId),
      ) { seasonDetails, cast, images ->
        seasonDetails.fold(
          {
            _state.update { state ->
              state.copy(
                errorMessage = it.errorMessage,
                isLoading = false,
              )
            }
          },
          { result ->
            _state.update { state ->
              result?.let {
                state.copy(
                  seasonId = result.seasonId,
                  seasonName = result.name,
                  seasonOverview = result.seasonOverview,
                  watchProgress = 0f,
                  isSeasonWatched = false,
                  episodeCount = result.episodeCount,
                  imageUrl = result.imageUrl,
                  episodeDetailsList = result.episodes.toEpisodes(),
                  seasonImages = images.toImageList(),
                  seasonCast = cast.toCastList(),
                )
              }
                ?: state
            }
          },
        )
      }
      .collect()
  }
}
