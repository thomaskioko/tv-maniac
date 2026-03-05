package com.thomaskioko.tvmaniac.domain.calendar.model

public data class GroupedCalendarEntry(
    val dateLabel: DateLabel,
    val episodes: List<GroupedEpisodeEntry>,
)
