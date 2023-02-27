package com.thomaskioko.tvmaniac.data.seasondetails

sealed class SeasonDetailsAction

data class LoadSeasonDetails(
    val showId: Long
) : SeasonDetailsAction()

data class ReloadSeasonDetails(
    val showId: Long
): SeasonDetailsAction()