package com.thomaskioko.tvmaniac.presenter.showdetails.model

import kotlinx.serialization.Serializable

@Serializable
public data class ShowSeasonDetailsParam(
    val showTraktId: Long,
    val seasonId: Long,
    val seasonNumber: Long,
    val selectedSeasonIndex: Int,
)
