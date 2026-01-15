package com.thomaskioko.tvmaniac.presenter.showdetails.model

public data class ContinueTrackingEpisodeModel(
    val episodeId: Long,
    val seasonId: Long,
    val showTraktId: Long,
    val episodeNumber: Long,
    val seasonNumber: Long,
    val episodeNumberFormatted: String,
    val episodeTitle: String,
    val imageUrl: String?,
    val isWatched: Boolean,
    val daysUntilAir: Int?,
    val hasAired: Boolean,
)
