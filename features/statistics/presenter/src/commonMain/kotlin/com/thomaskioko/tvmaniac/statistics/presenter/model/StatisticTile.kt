package com.thomaskioko.tvmaniac.statistics.presenter.model

/**
 * One cell in the summary grid. [caption] is empty when the tile has nothing to add below its value.
 */
public data class StatisticTile(
    val id: StatisticTileId,
    val label: String,
    val value: String,
    val caption: String,
)
