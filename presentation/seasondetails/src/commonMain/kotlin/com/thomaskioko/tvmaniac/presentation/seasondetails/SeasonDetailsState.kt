package com.thomaskioko.tvmaniac.presentation.seasondetails

import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetailsModel

sealed interface SeasonDetailsState

data object Loading : SeasonDetailsState

data class SeasonDetailsLoaded(
    val showTitle: String = "",
    val selectedSeason: String? = "",
    val seasonDetailsModel: SeasonDetailsModel,
    val errorMessage: String? = null,
    val isLoading: Boolean = true,
) : SeasonDetailsState

data class LoadingError(val message: String? = null) : SeasonDetailsState
