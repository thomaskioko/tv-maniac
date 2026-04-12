package com.thomaskioko.tvmaniac.presentation.calendar.model

import kotlinx.collections.immutable.ImmutableList

public data class CalendarDateGroup(
    val dateLabel: String,
    val episodes: ImmutableList<CalendarEpisodeItem>,
)
