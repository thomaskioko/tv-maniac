package com.thomaskioko.tvmaniac.data.calendar

public data class RemoteCalendarEntry(
    val tmdbId: Long,
    val showTitle: String,
    val episodeTitle: String?,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val firstAiredIso: String,
    val runtime: Int?,
    val overview: String?,
    val rating: Double?,
    val votes: Int?,
)
