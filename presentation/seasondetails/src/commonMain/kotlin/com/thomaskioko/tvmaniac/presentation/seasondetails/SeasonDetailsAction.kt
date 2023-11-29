package com.thomaskioko.tvmaniac.presentation.seasondetails

sealed interface SeasonDetailsAction

data class ReloadSeasonDetails(
    val showId: Long,
) : SeasonDetailsAction

data class UpdateEpisodeStatus(
    val id: Long,
) : SeasonDetailsAction
