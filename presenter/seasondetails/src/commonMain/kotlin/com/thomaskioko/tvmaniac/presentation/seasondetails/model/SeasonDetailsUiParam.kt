package com.thomaskioko.tvmaniac.presentation.seasondetails.model

import kotlinx.serialization.Serializable

@Serializable
data class SeasonDetailsUiParam(
    val showId: Long,
    val seasonId: Long,
    val seasonNumber: Long,
)
