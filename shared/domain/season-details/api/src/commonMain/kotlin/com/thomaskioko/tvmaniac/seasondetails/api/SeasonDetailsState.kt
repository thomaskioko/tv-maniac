package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetails

sealed class SeasonDetailsState

object Loading : SeasonDetailsState()

data class SeasonDetailsLoaded(
    val showTitle: String = "",
    val episodeList: List<SeasonDetails> = emptyList(),
) : SeasonDetailsState()

data class LoadingError(val message: String) : SeasonDetailsState()