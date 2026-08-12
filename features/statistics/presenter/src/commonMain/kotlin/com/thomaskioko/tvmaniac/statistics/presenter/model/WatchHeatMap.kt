package com.thomaskioko.tvmaniac.statistics.presenter.model

public data class WatchHeatMap(
    val cells: List<HeatMap>,
    val leadingBlankCells: Int,
    val activeDays: Int,
    val quietDays: Int,
)
