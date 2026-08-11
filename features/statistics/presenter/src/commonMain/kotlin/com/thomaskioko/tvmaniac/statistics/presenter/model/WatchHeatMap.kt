package com.thomaskioko.tvmaniac.statistics.presenter.model

/**
 * Daily watch counts laid out for a seven row grid, oldest column first. [leadingBlankCells] pads
 * the first column so every row lines up with the same day of the week.
 */
public data class WatchHeatMap(
    val cells: List<HeatMap>,
    val leadingBlankCells: Int,
    val activeDays: Int,
    val quietDays: Int,
    val busiestDayCount: Int = 0,
)
