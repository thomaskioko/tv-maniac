package com.thomaskioko.tvmaniac.seasondetails.presenter.model

import kotlinx.serialization.Serializable

@Serializable
public data class SeasonDetailsUiParam(
    val showId: Long,
    val seasonId: Long,
    val seasonNumber: Long,
)
