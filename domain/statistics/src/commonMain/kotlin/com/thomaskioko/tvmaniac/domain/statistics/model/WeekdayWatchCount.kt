package com.thomaskioko.tvmaniac.domain.statistics.model

import kotlinx.datetime.DayOfWeek

public data class WeekdayWatchCount(
    val dayOfWeek: DayOfWeek,
    val episodeCount: Int,
    val minutes: Long,
)
