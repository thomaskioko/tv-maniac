package com.thomaskioko.tvmaniac.presentation.seasondetails

sealed interface SeasonDetailsAction

data class LoadSeasonDetails(
    val showId: Long,
) : SeasonDetailsAction

data class ReloadSeasonDetails(
    val showId: Long,
) : SeasonDetailsAction
