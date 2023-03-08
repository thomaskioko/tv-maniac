package com.thomaskioko.tvmaniac.data.seasondetails

sealed interface SeasonDetailsAction

data class LoadSeasonDetails(
    val showId: Long
) : SeasonDetailsAction

data class ReloadSeasonDetails(
    val showId: Long
): SeasonDetailsAction