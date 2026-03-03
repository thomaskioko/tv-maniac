package com.thomaskioko.tvmaniac.data.calendar

public data class CalendarEntry(
    val showTraktId: Long,
    val episodeTraktId: Long,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val episodeTitle: String?,
    val airDate: Long,
    val showTitle: String,
    val showPosterPath: String?,
    val network: String?,
    val runtime: Int?,
    val overview: String?,
    val rating: Double?,
    val votes: Int?,
)
