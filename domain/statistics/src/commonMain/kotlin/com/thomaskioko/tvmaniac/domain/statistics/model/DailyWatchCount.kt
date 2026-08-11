package com.thomaskioko.tvmaniac.domain.statistics.model

import kotlinx.datetime.LocalDate

public data class DailyWatchCount(
    val date: LocalDate,
    val episodeCount: Int,
    val minutes: Long,
)
