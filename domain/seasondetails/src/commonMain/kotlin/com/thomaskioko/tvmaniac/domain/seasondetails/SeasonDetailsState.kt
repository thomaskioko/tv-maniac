package com.thomaskioko.tvmaniac.domain.seasondetails

import com.thomaskioko.tvmaniac.domain.seasondetails.model.SeasonDetails

sealed interface SeasonDetailsState

object Loading : SeasonDetailsState

data class SeasonDetailsLoaded(
    val showTitle: String = "",
    val seasonDetailsList: List<SeasonDetails> = emptyList(),
) : SeasonDetailsState

data class LoadingError(val message: String) : SeasonDetailsState
