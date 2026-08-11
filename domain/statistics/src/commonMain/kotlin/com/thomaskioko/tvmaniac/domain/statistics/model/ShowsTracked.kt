package com.thomaskioko.tvmaniac.domain.statistics.model

public data class ShowsTracked(
    val total: Long,
    val completed: Long,
) {
    val completionRate: Int
        get() = if (total == 0L) 0 else ((completed * 100) / total).toInt()
}
