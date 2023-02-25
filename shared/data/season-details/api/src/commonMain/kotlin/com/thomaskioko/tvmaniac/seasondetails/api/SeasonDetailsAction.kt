package com.thomaskioko.tvmaniac.seasondetails.api

sealed class SeasonDetailsAction

data class LoadSeasonDetails(
    val showId: Long
) : SeasonDetailsAction()

data class ReloadSeasonDetails(
    val showId: Long
): SeasonDetailsAction()