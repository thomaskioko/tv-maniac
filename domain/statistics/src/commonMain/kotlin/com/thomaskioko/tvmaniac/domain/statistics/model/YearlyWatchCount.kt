package com.thomaskioko.tvmaniac.domain.statistics.model

public data class YearlyWatchCount(
    val year: Int,
    val episodeCount: Int,
    val minutes: Long,
)
