package com.thomaskioko.tvmaniac.domain.statistics.model

import kotlinx.datetime.Month

public data class MonthlyWatchCount(
    val month: Month,
    val episodeCount: Int,
    val minutes: Long,
)
