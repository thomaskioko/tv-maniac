package com.thomaskioko.tvmaniac.presentation.seasondetails

sealed interface SeasonDetailsAction

data object SeasonDetailsBackClicked : SeasonDetailsAction

data object OnEpisodeHeaderClicked : SeasonDetailsAction

data object DismissSeasonDetailSnackBar : SeasonDetailsAction

data object DismissSeasonDialog : SeasonDetailsAction

data object UpdateSeasonWatchedState : SeasonDetailsAction

data object ShowMarkSeasonDialog : SeasonDetailsAction

data object SeasonGalleryClicked : SeasonDetailsAction

data class ReloadSeasonDetails(
  val showId: Long,
) : SeasonDetailsAction

data class UpdateEpisodeStatus(
  val id: Long,
) : SeasonDetailsAction

data class EpisodeClicked(
  val id: Long,
) : SeasonDetailsAction
