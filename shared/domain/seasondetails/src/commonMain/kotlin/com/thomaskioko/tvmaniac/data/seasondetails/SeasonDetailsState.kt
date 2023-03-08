package com.thomaskioko.tvmaniac.data.seasondetails

import com.thomaskioko.tvmaniac.data.seasondetails.model.SeasonDetails


sealed interface SeasonDetailsState

object Loading : SeasonDetailsState

data class SeasonDetailsLoaded(
    val showTitle: String = "",
    val episodeList: List<SeasonDetails> = emptyList(),
) : SeasonDetailsState

data class LoadingError(val message: String) : SeasonDetailsState