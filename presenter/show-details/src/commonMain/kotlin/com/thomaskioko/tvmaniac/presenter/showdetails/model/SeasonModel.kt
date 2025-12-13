package com.thomaskioko.tvmaniac.presenter.showdetails.model

public data class SeasonModel(
    val seasonId: Long,
    val tvShowId: Long,
    val name: String,
    val seasonNumber: Long,
    val watchedCount: Int = 0,
    val totalCount: Int = 0,
) {
    val progressPercentage: Float
        get() = if (totalCount > 0) watchedCount.toFloat() / totalCount else 0f
    val isSeasonWatched: Boolean
        get() = watchedCount == totalCount && totalCount > 0
}
