package com.thomaskioko.tvmaniac.domain.widget

public data class WidgetShow(
    val tmdbId: Long,
    val showName: String,
    val episodeName: String,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val posterUrl: String?,
)
