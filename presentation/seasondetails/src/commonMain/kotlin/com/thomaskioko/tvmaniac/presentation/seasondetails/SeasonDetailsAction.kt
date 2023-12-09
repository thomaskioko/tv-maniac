package com.thomaskioko.tvmaniac.presentation.seasondetails

sealed interface SeasonDetailsAction

data object BackClicked : SeasonDetailsAction
data class ReloadSeasonDetails(
    val showId: Long,
) : SeasonDetailsAction

data class UpdateEpisodeStatus(
    val id: Long,
) : SeasonDetailsAction

data class EpisodeClicked(
    val id: Long,
) : SeasonDetailsAction
