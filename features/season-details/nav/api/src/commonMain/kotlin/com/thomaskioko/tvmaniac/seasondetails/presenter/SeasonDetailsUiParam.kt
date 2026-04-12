package com.thomaskioko.tvmaniac.seasondetails.presenter

import kotlinx.serialization.Serializable

@Serializable
public data class SeasonDetailsUiParam(
    val showTraktId: Long,
    val seasonId: Long,
    val seasonNumber: Long,
    val forceRefresh: Boolean = false,
)
