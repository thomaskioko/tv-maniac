package com.thomaskioko.tvmaniac.domain.statistics.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

public data class TopWeekday(
    val dayOfWeek: DayOfWeek,
    val episodeCount: Int,
    val mostRecentDate: LocalDate,
)
