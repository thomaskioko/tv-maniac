package com.thomaskioko.tvmaniac.presentation.seasondetails

internal fun reducer(
  currentState: SeasonDetailState,
  action: SeasonDetailsAction
): SeasonDetailState {
  return when (action) {
    is EpisodeClicked -> currentState // No state change
    is UpdateEpisodeStatus -> currentState // TODO: Implement this
    SeasonDetailsBackClicked -> currentState // No state change
    ReloadSeasonDetails -> currentState // State will be updated by observeSeasonDetails
    SeasonGalleryClicked -> toggleGalleryBottomSheet(currentState)
    ShowMarkSeasonDialog -> toggleSeasonWatchStateDialog(currentState)
    DismissSeasonDialog -> dismissSeasonDialog(currentState)
    UpdateSeasonWatchedState -> dismissSeasonDialog(currentState)
    OnEpisodeHeaderClicked -> toggleExpandEpisodeItems(currentState)
    DismissSeasonGallery -> dismissGalleryBottomSheet(currentState)
  }
}

private fun dismissGalleryBottomSheet(state: SeasonDetailState): SeasonDetailState {
  return when (state) {
    is SeasonDetailsLoaded -> state.copy(showGalleryBottomSheet = false)
    else -> state
  }
}

private fun toggleGalleryBottomSheet(state: SeasonDetailState): SeasonDetailState {
  return when (state) {
    is SeasonDetailsLoaded -> state.copy(showGalleryBottomSheet = !state.showGalleryBottomSheet)
    else -> state
  }
}

private fun toggleSeasonWatchStateDialog(state: SeasonDetailState): SeasonDetailState {
  return when (state) {
    is SeasonDetailsLoaded ->
      state.copy(showSeasonWatchStateDialog = !state.showSeasonWatchStateDialog)
    else -> state
  }
}

private fun dismissSeasonDialog(state: SeasonDetailState): SeasonDetailState {
  return when (state) {
    is SeasonDetailsLoaded -> state.copy(showSeasonWatchStateDialog = false)
    else -> state
  }
}

private fun toggleExpandEpisodeItems(state: SeasonDetailState): SeasonDetailState {
  return when (state) {
    is SeasonDetailsLoaded -> state.copy(expandEpisodeItems = !state.expandEpisodeItems)
    else -> state
  }
}
