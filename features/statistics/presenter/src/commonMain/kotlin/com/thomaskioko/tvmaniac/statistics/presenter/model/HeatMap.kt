package com.thomaskioko.tvmaniac.statistics.presenter.model

public data class HeatMap(
    val date: String,
    val level: Int,
    val episodeCount: Int,
    val isToday: Boolean = false,
)
