package com.thomaskioko.tvmaniac.showdetails.nav.model

import kotlinx.serialization.Serializable

@Serializable
public data class ShowSeasonDetailsParam(
    val showId: Long,
    val seasonId: Long,
    val seasonNumber: Long,
    val selectedSeasonIndex: Int,
)
