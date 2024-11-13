package com.thomaskioko.tvmaniac.presentation.seasondetails

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SeasonDetailsPresenterFactory(
  val create : (
    componentContext:ComponentContext,
  param: SeasonDetailsUiParam,
  onBack: () -> Unit,
  onNavigateToEpisodeDetails: (id: Long) -> Unit,
) -> SeasonDetailsPresenter
)

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

  private val seasonDetailsParam: SeasonDetailsParam =
    SeasonDetailsParam(
      showId = param.showId,
      seasonId = param.seasonId,
      seasonNumber = param.seasonNumber,
    )
  private val coroutineScope = coroutineScope()
  private val _state: MutableStateFlow<SeasonDetailState> = MutableStateFlow(InitialSeasonsState)
  val state: StateFlow<SeasonDetailState> = _state.asStateFlow()

  init {
    coroutineScope.launch {
      observeSeasonDetails().collect { newState -> _state.update { newState } }
    }
  }

  fun dispatch(action: SeasonDetailsAction) {
    coroutineScope.launch {
      when (action) {
        is EpisodeClicked -> onEpisodeClick(action.id)
        SeasonDetailsBackClicked -> onBack()
        ReloadSeasonDetails -> observeSeasonDetails().collect { _state.update { it } }
        else -> _state.update { reducer(_state.value, action) }
      }
    }
  }

  private fun observeSeasonDetails(): Flow<SeasonDetailState> {
    return combine(
      seasonDetailsRepository.observeSeasonDetails(seasonDetailsParam),
      castRepository.observeSeasonCast(seasonDetailsParam.seasonId),
      seasonDetailsRepository.observeSeasonImages(seasonDetailsParam.seasonId),
    ) { seasonDetails, cast, images ->
      seasonDetails.fold(
        { error -> SeasonDetailsErrorState(errorMessage = error.errorMessage) },
        { details ->
          details?.let {
            SeasonDetailsLoaded(
              seasonId = it.seasonId,
              seasonName = it.name,
              seasonOverview = it.seasonOverview,
              episodeCount = it.episodeCount,
              imageUrl = it.imageUrl,
              episodeDetailsList = it.episodes.toEpisodes(),
              seasonImages = images.toImageList(),
              seasonCast = cast.toCastList(),
              isUpdating = false,
            )
          }
            ?: InitialSeasonsState
        },
      )
    }
  }
}
