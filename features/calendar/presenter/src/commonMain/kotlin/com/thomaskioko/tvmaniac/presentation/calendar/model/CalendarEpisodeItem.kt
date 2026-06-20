package com.thomaskioko.tvmaniac.presentation.calendar.model

public data class CalendarEpisodeItem(
    val showId: Long,
    val episodeId: Long?,
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
