package com.thomaskioko.tvmaniac.episodes.api.model

public data class ShowWatchProgress(
    val showTraktId: Long,
    val watchedCount: Int,
    val totalCount: Int,
) {
    init {
        require(totalCount >= 0) { "totalCount must be non-negative" }
        require(watchedCount >= 0) { "watchedCount must be non-negative" }
    }

    val isShowWatched: Boolean get() = totalCount > 0 && watchedCount >= totalCount
    val progressPercentage: Float get() = if (totalCount > 0) (watchedCount.toFloat() / totalCount).coerceAtMost(1f) else 0f
}
