package com.thomaskioko.tvmaniac.seasondetails.api

sealed class SeasonDetailsAction

data class LoadSeasonDetails(
    val showId: Int
) : SeasonDetailsAction()

data class ReloadSeasonDetails(
    val showId: Int
): SeasonDetailsAction()