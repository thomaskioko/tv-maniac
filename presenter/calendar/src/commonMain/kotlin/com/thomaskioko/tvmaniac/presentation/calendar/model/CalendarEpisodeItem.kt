package com.thomaskioko.tvmaniac.presentation.calendar.model

public data class CalendarEpisodeItem(
    val showTraktId: Long,
    val episodeTraktId: Long,
    val showTitle: String,
    val posterUrl: String?,
    val episodeInfo: String,
    val airTime: String?,
    val network: String?,
    val additionalEpisodesCount: Int,
    val overview: String?,
    val rating: Double?,
    val votes: Int?,
    val runtime: Int?,
    val formattedAirDate: String?,
)
