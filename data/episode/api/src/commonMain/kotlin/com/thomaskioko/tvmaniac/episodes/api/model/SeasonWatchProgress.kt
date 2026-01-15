package com.thomaskioko.tvmaniac.episodes.api.model

public data class SeasonWatchProgress(
    val showTraktId: Long,
    val seasonNumber: Long,
    val watchedCount: Int,
    val totalCount: Int,
) {
    init {
        require(totalCount >= 0) { "totalCount must be non-negative" }
        require(watchedCount >= 0) { "watchedCount must be non-negative" }
        require(watchedCount <= totalCount) { "watchedCount cannot exceed totalCount" }
    }

    val isSeasonWatched: Boolean get() = watchedCount == totalCount && totalCount > 0
    val progressPercentage: Float get() = if (totalCount > 0) watchedCount.toFloat() / totalCount else 0f
}
